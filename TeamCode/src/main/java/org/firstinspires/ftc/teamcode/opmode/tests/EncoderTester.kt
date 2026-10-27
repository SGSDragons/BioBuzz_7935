package org.firstinspires.ftc.teamcode.opmode.tests

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp(name = "Encoder Tester", group = "Test")
class EncoderTester: OpMode() {

    private lateinit var encoderDummyMotor: DcMotorEx

    override fun init() {
        encoderDummyMotor = hardwareMap.get(DcMotorEx::class.java, "port3")
    }

    override fun start() {
    }

    override fun loop() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        telemetry.addData("Encode rPose", encoderDummyMotor.currentPosition)
        telemetry.update()

    }
}