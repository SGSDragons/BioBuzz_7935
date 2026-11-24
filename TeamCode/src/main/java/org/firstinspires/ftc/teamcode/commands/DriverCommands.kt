package org.firstinspires.ftc.teamcode.commands

import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.FieldPoints
import org.firstinspires.ftc.teamcode.utils.Telemetry

class DriverCommands(val gamepad: Gamepad, val follower: Follower) {


    fun update() {
        follower.manual(
            ManualDrive.fieldCentric(
                -gamepad.left_stick_y.toDouble(),
                -gamepad.left_stick_x.toDouble(),
                -gamepad.right_stick_x.toDouble(),
                follower.pose().heading()));

        // A -> Snap position to the nearest flower. Don't change header
        // X -> Snap position to the left corner.
        // B -> Snap position to the right corner.
        // Y -> Snap to the nearest wall assuming a reasonable pose estimate

        when {
            gamepad.aWasPressed() -> snapXY(FieldPoints.nearFlower)
            gamepad.yWasPressed() -> snapWall()

            gamepad.dpadUpWasPressed() -> follower.setHeading(0.0)
            gamepad.dpadDownWasPressed() -> follower.setHeading(Math.toRadians(180.0))
            gamepad.dpadLeftWasPressed() -> follower.setHeading(Math.toRadians(90.0))
            gamepad.dpadRightWasPressed() -> follower.setHeading(Math.toRadians(-90.0))
        }

        // Bind the right bumper to use manual.headingLock and align with the
        // nearest N/S/E/W axis. headingPID will need tuning
        // On release of the right bumper, reset the robot's heading
        // nearest axis. While holding the bumper, use the driver's left/right
        // joystick at 1/3 power

        Telemetry.put("left stick y") { -gamepad.left_stick_y }
        Telemetry.put("left stick x") { -gamepad.left_stick_x }
        Telemetry.put("right stick x") { -gamepad.right_stick_x }
    }

    fun snapXY(point: Pose) {
        // Snap the robot's X/Y to the input point. Do not change the heading
        follower.setPose(point.withHeading(follower.pose().heading()))
    }

    fun snapWall() {
        // Assume the robot's position is correct to within a tile
        // If the X is within 24" of a wall and Y is not, then snap
        // X to its nearest wall, accounting for the bot's hit box
        // Similarly, if Y is within 24" of a wall and X is not,
        // snap Y to a wall

        // If we want to be very fancy, account for the bot's orientation
        // and non-square shape. It should only matter by an inch or two
        // though.

        val botWidth = 16.0/2.0
        val fieldWidth = 141.5

        val initPose = follower.pose()
        val x = initPose.x()
        val y = initPose.y()

        val xNearWall = x < 24 || x > fieldWidth - 24
        val yNearWall = y < 24 || y > fieldWidth - 24

        if (xNearWall == yNearWall) {
            // Ambiguous which one to set. Do nothing
            return
        }


        // Exactly one of these will be true
        val newPose = when {
            x < 24 -> initPose.withX(botWidth)
            x > fieldWidth - 24 -> initPose.withX(fieldWidth - botWidth)
            y < 24 -> initPose.withY(botWidth)
            else -> initPose.withY(fieldWidth - botWidth)
        }
        follower.setPose(newPose)
    }
}