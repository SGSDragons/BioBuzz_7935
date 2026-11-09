package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.api.PoseFactory
import com.pedropathing.math.Pose

object FieldPoints {

    // Coordinates pulled from Pedro.
    val poseFactory = PoseFactory.degrees()

    // All poses assume the robot is facing the wall to grab pollen
    val nearFlower = poseFactory.of(8.0, 56.0, 180.0)
    val leftFlower = poseFactory.of(57.0, 133.0, 90.0)
    val rightFlower = poseFactory.of(83.0, 8.0, -90.0)
    val farFlower = poseFactory.of(133.0, 84.0, 0.0)
}