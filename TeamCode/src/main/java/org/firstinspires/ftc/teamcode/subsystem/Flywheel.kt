package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Telemetry.put

@Config
class Flywheel(hardwareMap: HardwareMap) {
    private var ballShot = false
    private val leftMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "leftshooter")
    private val rightMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "rightshooter")
    private var targetSpeed = 0.0

    init {
        leftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER

        leftMotor.direction = if (LEFT_FORWARD) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE
        rightMotor.direction = if (RIGHT_FORWARD) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE
    }

    fun reconfigure() {
        leftMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF)
        rightMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF)
    }

    fun run(speed: Double) {
        targetSpeed = speed
        leftMotor.velocity = speed
        rightMotor.velocity = speed
    }

    val velocity: Double
        get() = (leftMotor.velocity + rightMotor.velocity) / 2

    fun wasBallShot(): Boolean {
        if (ballShot) {
            ballShot = false
            return true
        } else {
            return false
        }
    }

    fun stop() {
        targetSpeed = 0.0
        leftMotor.power = 0.0
        rightMotor.power = 0.0
    }

    fun update() {
        reconfigure()
        put("flywheel/target", targetSpeed)

        put(
            "flywheel/left/position",
            leftMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER).p
        )
        put("flywheel/left/position", leftMotor.currentPosition)
        put("flywheel/left/velocity", leftMotor.velocity)
        put("flywheel/right/position", rightMotor.currentPosition)
        put("flywheel/right/velocity", rightMotor.velocity)
        put("flywheel/averageVelocity", this.velocity)
    }

    companion object {
        @JvmField var kF: Double = 15.0
        @JvmField var kP: Double = 50.0
        @JvmField var kI: Double = 0.0
        @JvmField var kD: Double = 20.0
        @JvmField var LEFT_FORWARD: Boolean = true
        @JvmField var RIGHT_FORWARD: Boolean = true
    }
}