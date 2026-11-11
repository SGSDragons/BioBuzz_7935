package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Localizer
import org.firstinspires.ftc.teamcode.Telemetry
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

@Config
class TargeterNew(follower: Follower, isRed: Boolean) {
    // named spot on field
    class Target(
        val name: String?, val x: Double, // Field inches
        var y: Double
    ) {
        init {
            this.y = y
        }
    }

    companion object {
        var velocity: Double = -1625.0
    }

    private val localizer: Localizer = follower.localizer
    private val targets: MutableList<Target> = ArrayList()
    private var currentIndex = 0

    val turretVector3: Vector3
        get() = Vector3(localizer.pose().x(), localizer.pose().y(), 11.0)

    val flywheelSpeed: Double
        get() = velocity //distanceToTarget()

    //        Return flywheelBase + flywheelPerInch * distanceToTarget();

    fun addTarget(t: Target?) {
        targets.add(t!!)
    }

    fun setTarget(index: Int) {
        if (index >= 0 && index < targets.size) {
            currentIndex = index
        } else {
            throw IndexOutOfBoundsException("Target index out of bounds")
        }
    }

    fun nextTarget() {
        currentIndex = (currentIndex + 1) % targets.size
    }

    val currentTarget: Target
        get() = targets[currentIndex]

    // Turret range in radians, reflective to the robot's forward direction TUNE
    private val turretMinAngle = 0.0
    private val turretMaxAngle = 2 * Math.PI

    // Flywheel speed = base + perInch * distance. TUNE ON FEILD.
    private val flywheelBase = 1000.0 // base speed in ticks per second
    private val flywheelPerInch = 8.0

    // Constructor: gets the Localizer from the Follower
    init {

        // Placeholder positions. Replace with real field coordinates.
        if (isRed) {
            addTarget(Target("A", 58.0, 50.0))
            addTarget(Target("B", 58.0, 90.0))
        } else {
            addTarget(Target("A", 84.0, 90.0))
            addTarget(Target("B", 84.0, 50.0))
        }
    }

    private fun distanceToTarget(): Double {
        val p = turretVector3
        val t = this.currentTarget
        val t3 = Vector3(t.x, t.y, 60.0)
        Telemetry.put("targeter/distance", hypot(hypot(t3.x - p.x, t3.y - p.y), t3.z - p.z))
        return hypot(hypot(t3.x - p.x, t3.y - p.y), t3.z - p.z)
    }

    val aimValue: Double
        // 0-1 turret position for the turret aimer./
        get() {
            val p = localizer.pose()
            val t = this.currentTarget

            val fieldAngle = atan2(t.y - p.y(), t.x - p.x())
            val turretAngle = wrap(fieldAngle - p.heading())

            val value = (turretAngle - turretMinAngle) / (turretMaxAngle - turretMinAngle)
            return 360 * max(0.0, min(1.0, value))
        }

    // Wrap an angle into -PI..PI
    private fun wrap(a: Double): Double {
        var a = a
        while (a > turretMaxAngle) a -= 2 * Math.PI
        while (a < turretMinAngle) a += 2 * Math.PI
        return a
    }
}