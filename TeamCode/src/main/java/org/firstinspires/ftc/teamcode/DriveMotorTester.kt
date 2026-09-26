package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.Constants.DriveMotors

/*
 * A simple way to check the port name to drive wheel mapping
 * and ensure they all move in the correct location.
 *
 * Update actual values in the Constants.DriveMotors object.
 */
@TeleOp(name = "Test: Wheel Ports", group = "Tests")
class DriveMotorTester : LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Initialized")
        telemetry.update()

        waitForStart()

        var lastActiveDrive: DcMotor? = null

        while (opModeIsActive()) {
            val cfg = when {
                gamepad1.square -> DriveMotors.backLeft
                gamepad1.triangle -> DriveMotors.frontLeft
                gamepad1.cross -> DriveMotors.backRight
                gamepad1.circle -> DriveMotors.frontRight
                else -> null
            }

            // Get the motor and set its direction
            val motor = cfg?.apply(hardwareMap)

            // If the motor's changing, then stop the previous motor
            if (motor != lastActiveDrive) {
                lastActiveDrive?.power = 0.0
            }

            // Update the current motor's state
            lastActiveDrive = motor
            lastActiveDrive?.power = 0.2

            telemetry.addData("Current Wheel", cfg?.name ?: "none")
            telemetry.update()
        }
    }
}