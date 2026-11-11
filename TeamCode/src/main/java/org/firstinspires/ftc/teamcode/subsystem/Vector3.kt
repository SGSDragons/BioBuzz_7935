package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.math.Pose

class Vector3(var x: Double, var y: Double, var z: Double, var headingDegrees: Double = 0.0) {
    fun getAsPose(): Pose {
        return Pose(x, y, Math.toRadians(headingDegrees))
    }
}