package org.firstinspires.ftc.teamcode.pedro

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import org.json.JSONObject

/**
 * Loads a Pedro Pathing visualizer export at runtime. Paths can be fetched by
 * the name you gave them in the visualizer, or by number (0-based, in the order
 * sequenced in the visualizer).
 *
 * File location:
 *     TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/paths/<fileName>
 * (Gradle must be told to package non-code files from src/main/java.)
 *
 * Usage:
 *     val auto = PointArrayLoader.load("TwelveBallAuto.pp", poseFactory)
 *     follower.setStartingPose(auto.startPose)
 *
 *     auto["Traverse"].path()          // by name -> Path
 *     auto[2].path()                   // by number -> Path
 *
 * Customizing (local values, see pedropathing.com/docs/pathing/guide/modifiers):
 *     auto.customizeAll { it.with(Constants.foresightConfig.maxDecelerationScale.at(0.75)) }
 *     auto.customize("Traverse") { it.with(Constants.foresightConfig.maxPathSpeed.at(0.8)) }
 *     auto.customize(2) { it.with(...) }
 *   Call these BEFORE calling path(). customizeAll runs first, then the
 *   per-path ones, so per-path values win.
 *
 * NOTE: the wait times (waitBeforeMs / waitAfterMs) are only exposed as data.
 * This loader does not wait for you; your OpMode has to do that.
 */
@Suppress("unused")
object PointArrayLoader {

    /** Classpath directory (package path) that path files are read from. */
    const val RESOURCE_DIR = "org/firstinspires/ftc/teamcode/pedro/paths"

    /** Wait info that travels with a built Path. */
    class Waits(
        val beforeMs: Long,
        val afterMs: Long,
        val beforeName: String,
        val afterName: String,
    ) {
        val any: Boolean get() = beforeMs > 0 || afterMs > 0
    }

    // Every Path built by Step.path() is remembered here so goto(path) can look its waits up.
    // Weak keys: entries disappear when the Path is garbage collected.
    private val waitsByPath = java.util.Collections.synchronizedMap(java.util.WeakHashMap<Path, Waits>())

    /** The waits that came with this Path from the file, or null if it wasn't built by this loader. */
    fun waitsFor(path: Path): Waits? = waitsByPath[path]

    class Step(
        val index: Int,
        /** Empty string if the path was not named in the visualizer. */
        val name: String,
        val waitBeforeMs: Long,
        val waitAfterMs: Long,
        /** Optional labels from the visualizer; empty if unset. Use them to trigger actions. */
        val waitBeforeName: String,
        val waitAfterName: String,
        private val build: () -> Path,
    ) {
        private val globalModifiers = ArrayList<(Path) -> Path>()
        private val localModifiers = ArrayList<(Path) -> Path>()

        /** Applied to this path every time it is built, after any customizeAll modifiers. */
        fun customize(block: (Path) -> Path): Step {
            localModifiers += block
            return this
        }

        internal fun customizeGlobal(block: (Path) -> Path) {
            globalModifiers += block
        }

        /** Builds a fresh Path each time it is called, with all customizations applied. */
        fun path(): Path {
            var p = build()
            for (m in globalModifiers) p = m(p)
            for (m in localModifiers) p = m(p)
            waitsByPath[p] = Waits(waitBeforeMs, waitAfterMs, waitBeforeName, waitAfterName)
            return p
        }

        operator fun invoke(): Path = path()

        override fun toString() = if (name.isEmpty()) "#$index" else "#$index '$name'"
    }

    class Loaded(
        val startPose: Pose,
        val steps: List<Step>,
        /** Things in the file this loader could not honor exactly. Worth logging to telemetry. */
        val warnings: List<String> = emptyList(),
    ) {

        private val byName: Map<String, Step>

        init {
            val map = LinkedHashMap<String, Step>()
            for (s in steps) {
                if (s.name.isEmpty()) continue
                val clash = map.put(s.name, s)
                require(clash == null) { "Duplicate path name '${s.name}' (paths #${clash!!.index} and #${s.index})" }
            }
            byName = map
        }

        val size: Int get() = steps.size

        /** Only the paths that were named, in order. */
        val names: List<String> get() = byName.keys.toList()

        fun has(name: String) = name in byName

        operator fun get(index: Int): Step =
            steps.getOrNull(index)
                ?: error("No path #$index; file has ${steps.size} paths (0..${steps.size - 1})")

        operator fun get(name: String): Step =
            byName[name]
                ?: error("No path named '$name'. Named paths: ${byName.keys.ifEmpty { listOf("(none)") }}")

        fun path(index: Int): Path = get(index).path()
        fun path(name: String): Path = get(name).path()

        /** Apply a modifier to every path. Runs before per-path customizations. */
        fun customizeAll(block: (Path) -> Path): Loaded {
            steps.forEach { it.customizeGlobal(block) }
            return this
        }

        fun customize(name: String, block: (Path) -> Path): Loaded {
            get(name).customize(block)
            return this
        }

        fun customize(index: Int, block: (Path) -> Path): Loaded {
            get(index).customize(block)
            return this
        }

        /** All paths in order, as functions. */
        val paths: Array<() -> Path> get() = Array(steps.size) { steps[it]::path }
    }

    fun load(fileName: String, poseFactory: PoseFactory): Loaded {
        val root = JSONObject(readResource(fileName))
        val warnings = ArrayList<String>()

        // Index lines by id, then order them by `sequence` (falls back to `lines` order).
        val lines = root.getJSONArray("lines")
        val byId = LinkedHashMap<String, JSONObject>()
        for (i in 0 until lines.length()) lines.getJSONObject(i).let { byId[it.getString("id")] = it }

        val ordered = ArrayList<JSONObject>()
        val seq = root.optJSONArray("sequence")
        if (seq != null && seq.length() > 0) {
            for (i in 0 until seq.length()) {
                val e = seq.getJSONObject(i)
                val kind = e.optString("kind")
                if (kind == "path") {
                    ordered += byId[e.getString("lineId")]
                        ?: error("sequence references unknown lineId '${e.getString("lineId")}'")
                } else {
                    warnings += "sequence entry #$i of kind '$kind' was skipped (only 'path' is supported)"
                }
            }
        } else {
            ordered += byId.values
        }

        val sp = root.getJSONObject("startPoint")
        val startPose = poseFactory.of(sp.getDouble("x"), sp.getDouble("y"), sp.optDouble("headingDeg", 0.0))

        var px = sp.getDouble("x")
        var py = sp.getDouble("y")
        val steps = ArrayList<Step>()

        for ((index, line) in ordered.withIndex()) {
            val lineName = line.optString("name").ifEmpty { "#$index" }
            val end = line.getJSONObject("endPoint")
            val ex = end.getDouble("x")
            val ey = end.getDouble("y")
            val h = line.optJSONObject("heading")
            val type = h?.optString("type", "tangential") ?: "tangential"
            val reverse = h?.optBoolean("reverse", false) ?: false

            // Control points carry no heading (0.0), like the handwritten ones.
            val ctrl = line.optJSONArray("controlPoints")
            val controls = ArrayList<Pose>()
            if (ctrl != null) {
                for (i in 0 until ctrl.length()) {
                    val c = ctrl.getJSONObject(i)
                    controls += poseFactory.of(c.getDouble("x"), c.getDouble("y"), 0.0)
                }
            }

            // Start/end poses carry the heading that linear()/constant() read from.
            val startDeg: Double
            val endDeg: Double
            when (type) {
                "linear" -> {
                    startDeg = h!!.getDouble("startDeg")
                    endDeg = h.getDouble("endDeg")
                }
                "constant" -> {
                    val d = h!!.optDouble("degrees", h.optDouble("startDeg", 0.0))
                    startDeg = d
                    endDeg = d
                }
                "tangential", "tangent" -> {
                    startDeg = 0.0
                    endDeg = 0.0
                }
                else -> error("Path '$lineName': unsupported heading type '$type'")
            }
            if (reverse && type != "tangential" && type != "tangent") {
                warnings += "Path '$lineName': reverse=true is ignored for $type heading"
            }

            val startPoseSeg = poseFactory.of(px, py, startDeg)
            val endPoseSeg = poseFactory.of(ex, ey, endDeg)

            val build: () -> Path = {
                val geometry =
                    if (controls.isEmpty()) Paths.line(startPoseSeg, endPoseSeg)
                    else Paths.curve(startPoseSeg, *controls.toTypedArray(), endPoseSeg)

                when (type) {
                    "linear" -> geometry.linear(startPoseSeg, endPoseSeg)
                    "constant" -> geometry.constant(endPoseSeg)
                    else -> if (reverse) geometry.reverseTangent() else geometry.tangent()
                }
            }

            steps += Step(
                index = index,
                name = line.optString("name"),
                waitBeforeMs = line.optLong("waitBeforeMs", 0),
                waitAfterMs = line.optLong("waitAfterMs", 0),
                waitBeforeName = line.optString("waitBeforeName"),
                waitAfterName = line.optString("waitAfterName"),
                build = build,
            )
            px = ex
            py = ey
        }

        return Loaded(startPose, steps, warnings)
    }
    private fun readResource(fileName: String): String {
        val loader = PointArrayLoader::class.java.classLoader
        val stream = loader?.getResourceAsStream("$RESOURCE_DIR/$fileName")
            ?: loader?.getResourceAsStream("$RESOURCE_DIR/$fileName.pp")
            ?: loader?.getResourceAsStream("$RESOURCE_DIR/$fileName.pp.json")
            ?: loader?.getResourceAsStream("$RESOURCE_DIR/$fileName.json")
            ?: error("Path file not found on classpath: $RESOURCE_DIR/$fileName")
        return stream.bufferedReader().use { it.readText() }
    }
}