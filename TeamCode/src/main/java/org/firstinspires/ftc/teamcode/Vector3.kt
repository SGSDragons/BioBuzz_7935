package org.firstinspires.ftc.teamcode

import com.pedropathing.math.Pose

class Vector3(var x: Double, var y: Double, var z: Double, var horizontalHeading: Double = 0.0, var verticalHeading: Double = 0.0) {
    fun getAsPose(): Pose {
        return Pose(x, y, Math.toRadians(horizontalHeading))
    }
}