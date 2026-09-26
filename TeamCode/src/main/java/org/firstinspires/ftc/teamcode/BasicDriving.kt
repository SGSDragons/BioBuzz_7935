package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Basic Driving", group = "OpMode")
class BasicDriving : LinearOpMode() {

    override fun runOpMode() {
        val follower = Constants.newFollower(hardwareMap)
        follower.update()

        telemetry.addData("Status", "Initialized")
        telemetry.update()

        waitForStart()

        while (opModeIsActive()) {

            // CB: These operations require 0.25 seconds to sample enough data
            // for a good calibration. During that period the robot should be
            // stationary. It would be good for the software to stop all motors
            // before running this, and prevent motion until the sampling finishes.
            if (gamepad1.a) {
                follower.localizer.reset()
            } else {
                val forward = -gamepad1.left_stick_y.toDouble()
                val lateral = gamepad1.left_stick_x.toDouble()
                val turn = gamepad1.right_stick_x.toDouble()
                follower.manual(forward, lateral, turn)
            }

            follower.update()
            telemetry.update()
        }
    }
}