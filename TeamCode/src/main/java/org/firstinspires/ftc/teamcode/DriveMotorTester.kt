package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple

@TeleOp(name = "Drive Motor Tester")
class DriveMotorTester : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val frontLeft = hardwareMap.get(DcMotorSimple::class.java, "port2")
        val frontRight = hardwareMap.get(DcMotorSimple::class.java, "port1")
        val backLeft = hardwareMap.get(DcMotorSimple::class.java, "port3")
        val backRight = hardwareMap.get(DcMotorSimple::class.java, "port0")

        frontRight.direction = DcMotorSimple.Direction.REVERSE
        backRight.direction = DcMotorSimple.Direction.REVERSE

        waitForStart()

        while (opModeIsActive()) {
            if (gamepad1.dpad_left) {
                frontLeft.power = 0.5
            } else {
                frontLeft.power = 0.0
            }

            if (gamepad1.dpad_down) {
                frontRight.power = 0.5
            } else {
                frontRight.power = 0.0
            }

            if (gamepad1.dpad_up) {
                frontRight.power = 0.5
            } else {
                frontRight.power = 0.0
            }
            if (gamepad1.dpad_right) {
                backRight.power = 0.5
            } else {
                backRight.power = 0.0
            }
        }
    }
}