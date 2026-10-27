package org.firstinspires.ftc.teamcode.opmode.tests

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.acmerobotics.dashboard.config.Config
import kotlin.jvm.java

@TeleOp(name = "Cannon Flywheel Tuner", group = "Test")
class CannonFlywheelTuner: OpMode() {

    @Config
    companion object {
        @JvmField var tps: Double = 100.0
        @JvmField var flywheelMotor1Forward: Boolean = true
        @JvmField var flywheelMotor2Forward: Boolean = true
    }

    private lateinit var flywheelMotor1: DcMotorEx
    private lateinit var flywheelMotor2: DcMotorEx

    override fun init() {
        flywheelMotor1 = hardwareMap.get(DcMotorEx::class.java, "port0")
        flywheelMotor2 = hardwareMap.get(DcMotorEx::class.java, "port1")
    }

    override fun start() {
        if (!flywheelMotor1Forward) {
            flywheelMotor1.direction = DcMotorSimple.Direction.REVERSE
        }
        if (!flywheelMotor2Forward) {
            flywheelMotor2.direction = DcMotorSimple.Direction.REVERSE
        }
    }

    override fun loop() {
        if (gamepad1.a) {
            flywheelMotor1.velocity = tps
        }
        if (gamepad1.b) {
            flywheelMotor2.velocity = tps
        }
    }
}