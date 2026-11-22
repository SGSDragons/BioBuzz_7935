package org.firstinspires.ftc.teamcode.pedro

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import org.json.JSONArray
import org.json.JSONObject

/**
 * Loads a Pedro Pathing visualizer export at runtime.
 *
 * File location:
 *     TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/paths/<fileName>
 * (Gradle must be told to package non-code files from src/main/java.)
 *
 * Individual paths (by name or 0-based number in the file's sequence order):
 *     val auto = PointArrayLoader.load("TwelveBallAuto", poseFactory)
 *     auto["FlowerPickup1"].path()      // works even if the path lives inside a group
 *     auto[2].path()
 *
 * Groups (the "compound" lines in the visualizer):
 *     auto.group("Pickup1")             // Group
 *     auto.group("Pickup1")["FlowerPickup1"].path()
 *     auto.group("Pickup1").steps       // its paths, in order
 *     auto.groupNames
 *
 * The sequence the file specifies:
 *     auto.sequence                     // List<Step>, in the order the file runs them
 *     (Automator.runSequence(auto) turns this into a Command.)
 *
 * Customizing (local values). Call BEFORE path(). Order applied:
 * customizeAll -> customizeGroup -> per-path, so more specific wins.
 *     auto.customizeAll { it.with(...) }
 *     auto.customizeGroup("Pickup1") { it.with(...) }
 *     auto.customize("Traverse1") { it.with(...) }
 *
 * NOTE: wait times are only exposed as data (and picked up by Automator.goto /
 * gotoGroup / runSequence). This loader does not wait for you.
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

    /** The path's OWN waits (not its group's), or null if it wasn't built by this loader. */
    fun waitsFor(path: Path): Waits? = waitsByPath[path]

    class Step(
        /** Position in the file's sequence (0-based). */
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
        /** The group this path belongs to in the visualizer, or null if it isn't in one. */
        var group: Group? = null
            internal set

        private val globalModifiers = ArrayList<(Path) -> Path>()
        private val groupModifiers = ArrayList<(Path) -> Path>()
        private val localModifiers = ArrayList<(Path) -> Path>()

        /** Applied to this path every time it is built, after customizeAll / customizeGroup modifiers. */
        fun customize(block: (Path) -> Path): Step {
            localModifiers += block
            return this
        }

        internal fun customizeGlobal(block: (Path) -> Path) {
            globalModifiers += block
        }

        internal fun customizeGroupLevel(block: (Path) -> Path) {
            groupModifiers += block
        }

        /** Builds a fresh Path each time it is called, with all customizations applied. */
        fun path(): Path {
            var p = build()
            for (m in globalModifiers) p = m(p)
            for (m in groupModifiers) p = m(p)
            for (m in localModifiers) p = m(p)
            waitsByPath[p] = Waits(waitBeforeMs, waitAfterMs, waitBeforeName, waitAfterName)
            return p
        }

        operator fun invoke(): Path = path()

        override fun toString() = if (name.isEmpty()) "#$index" else "#$index '$name'"
    }

    /** A "compound" line from the visualizer: several paths with shared waits. */
    class Group(
        /** Empty string if the group was not named. */
        val name: String,
        /** Waits before the group's first path / after its last path. */
        val waitBeforeMs: Long,
        val waitAfterMs: Long,
        val waitBeforeName: String,
        val waitAfterName: String,
    ) {
        private val members = ArrayList<Step>()

        internal fun add(step: Step) {
            members += step
        }

        /** The group's paths, in sequence order. */
        val steps: List<Step> get() = members

        val size: Int get() = members.size

        /** Names of the group's named paths. */
        val names: List<String> get() = members.map { it.name }.filter { it.isNotEmpty() }

        operator fun get(index: Int): Step =
            members.getOrNull(index)
                ?: error("Group '$name' has no path #$index (it has ${members.size})")

        operator fun get(name: String): Step =
            members.firstOrNull { it.name == name }
                ?: error("Group '${this.name}' has no path named '$name'. Its paths: ${names.ifEmpty { listOf("(none)") }}")

        fun path(index: Int): Path = get(index).path()
        fun path(name: String): Path = get(name).path()

        /** All of the group's paths, as functions. */
        val paths: Array<() -> Path> get() = Array(members.size) { members[it]::path }

        /** Apply a modifier to every path in this group. */
        fun customize(block: (Path) -> Path): Group {
            members.forEach { it.customizeGroupLevel(block) }
            return this
        }

        override fun toString() = if (name.isEmpty()) "group(${members.size} paths)" else "group '$name'"
    }

    class Loaded(
        val startPose: Pose,
        /** Every path, in the order the file's sequence runs them. */
        val steps: List<Step>,
        /** Things in the file this loader could not honor exactly. Worth logging to telemetry. */
        val warnings: List<String> = emptyList(),
        val groups: List<Group> = emptyList(),
    ) {

        private val byName: Map<String, Step>
        private val groupsByName: Map<String, Group>

        init {
            val map = LinkedHashMap<String, Step>()
            for (s in steps) {
                if (s.name.isEmpty()) continue
                val clash = map.put(s.name, s)
                require(clash == null) { "Duplicate path name '${s.name}' (paths #${clash!!.index} and #${s.index})" }
            }
            byName = map

            val gmap = LinkedHashMap<String, Group>()
            for (g in groups) {
                if (g.name.isEmpty()) continue
                require(gmap.put(g.name, g) == null) { "Duplicate group name '${g.name}'" }
            }
            groupsByName = gmap
        }

        /** The sequence the file specifies. Same list as [steps]. */
        val sequence: List<Step> get() = steps

        val size: Int get() = steps.size

        /** Only the paths that were named, in order. */
        val names: List<String> get() = byName.keys.toList()

        val groupNames: List<String> get() = groupsByName.keys.toList()

        fun has(name: String) = name in byName
        fun hasGroup(name: String) = name in groupsByName

        operator fun get(index: Int): Step =
            steps.getOrNull(index)
                ?: error("No path #$index; file has ${steps.size} paths (0..${steps.size - 1})")

        operator fun get(name: String): Step =
            byName[name]
                ?: error("No path named '$name'. Named paths: ${byName.keys.ifEmpty { listOf("(none)") }}")

        fun group(name: String): Group =
            groupsByName[name]
                ?: error("No group named '$name'. Groups: ${groupsByName.keys.ifEmpty { listOf("(none)") }}")

        fun path(index: Int): Path = get(index).path()
        fun path(name: String): Path = get(name).path()

        /** Apply a modifier to every path. Runs before group and per-path customizations. */
        fun customizeAll(block: (Path) -> Path): Loaded {
            steps.forEach { it.customizeGlobal(block) }
            return this
        }

        fun customizeGroup(name: String, block: (Path) -> Path): Loaded {
            group(name).customize(block)
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

        /** All paths in sequence order, as functions. */
        val paths: Array<() -> Path> get() = Array(steps.size) { steps[it]::path }
    }

    fun load(fileName: String, poseFactory: PoseFactory): Loaded {
        val root = JSONObject(readResource(fileName))
        val warnings = ArrayList<String>()

        // 1. Flatten `lines`: every atomic path by id (including those inside compound
        //    groups), every compound by id, and which compound owns each atomic path.
        val atomicById = LinkedHashMap<String, JSONObject>()
        val compoundById = LinkedHashMap<String, JSONObject>()
        val ownerOf = HashMap<String, JSONObject>()

        fun collect(l: JSONObject, owner: JSONObject?) {
            if (l.optString("kind") == "compound") {
                compoundById[l.getString("id")] = l
                val segs: JSONArray? = l.optJSONArray("segments")
                if (segs != null) for (i in 0 until segs.length()) collect(segs.getJSONObject(i), l)
            } else {
                val id = l.getString("id")
                atomicById[id] = l
                if (owner != null) ownerOf[id] = owner
            }
        }

        val lines = root.getJSONArray("lines")
        for (i in 0 until lines.length()) collect(lines.getJSONObject(i), null)

        fun atomicsIn(c: JSONObject): List<JSONObject> {
            val out = ArrayList<JSONObject>()
            val segs: JSONArray = c.optJSONArray("segments") ?: return out
            for (i in 0 until segs.length()) {
                val s = segs.getJSONObject(i)
                if (s.optString("kind") == "compound") out += atomicsIn(s) else out += s
            }
            return out
        }

        // 2. Order by `sequence` (a lineId may name a path or a whole group).
        //    With no sequence, use file order.
        val ordered = ArrayList<JSONObject>()
        val seq = root.optJSONArray("sequence")
        if (seq != null && seq.length() > 0) {
            for (i in 0 until seq.length()) {
                val e = seq.getJSONObject(i)
                val kind = e.optString("kind")
                if (kind == "path") {
                    val id = e.getString("lineId")
                    val atomic = atomicById[id]
                    val compound = compoundById[id]
                    when {
                        atomic != null -> ordered += atomic
                        compound != null -> ordered += atomicsIn(compound)
                        else -> error("sequence references unknown lineId '$id'")
                    }
                } else {
                    warnings += "sequence entry #$i of kind '$kind' was skipped (only 'path' is supported)"
                }
            }
            val used = ordered.map { it.getString("id") }.toSet()
            for ((id, a) in atomicById) {
                if (id !in used) {
                    warnings += "Path '${a.optString("name").ifEmpty { id }}' is in the file but not in the sequence, so it was not loaded"
                }
            }
        } else {
            ordered += atomicById.values
        }

        val sp = root.getJSONObject("startPoint")
        val startPose = poseFactory.of(sp.getDouble("x"), sp.getDouble("y"), sp.optDouble("headingDeg", 0.0))

        var px = sp.getDouble("x")
        var py = sp.getDouble("y")
        val steps = ArrayList<Step>()
        val groups = LinkedHashMap<String, Group>()   // compound id -> Group

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

            val step = Step(
                index = index,
                name = line.optString("name"),
                waitBeforeMs = line.optLong("waitBeforeMs", 0),
                waitAfterMs = line.optLong("waitAfterMs", 0),
                waitBeforeName = line.optString("waitBeforeName"),
                waitAfterName = line.optString("waitAfterName"),
                build = build,
            )

            val owner = ownerOf[line.getString("id")]
            if (owner != null) {
                val g = groups.getOrPut(owner.getString("id")) {
                    Group(
                        name = owner.optString("name"),
                        waitBeforeMs = owner.optLong("waitBeforeMs", 0),
                        waitAfterMs = owner.optLong("waitAfterMs", 0),
                        waitBeforeName = owner.optString("waitBeforeName"),
                        waitAfterName = owner.optString("waitAfterName"),
                    )
                }
                g.add(step)
                step.group = g
            }

            steps += step
            px = ex
            py = ey
        }

        return Loaded(startPose, steps, warnings, groups.values.toList())
    }

    private fun readResource(fileName: String): String {
        val loader = PointArrayLoader::class.java.classLoader
        val stream = loader?.getResourceAsStream("$RESOURCE_DIR/$fileName")
            ?: loader?.getResourceAsStream("$RESOURCE_DIR/$fileName.pp.json")
            ?: error("Path file not found on classpath: $RESOURCE_DIR/$fileName")
        return stream.bufferedReader().use { it.readText() }
    }
}