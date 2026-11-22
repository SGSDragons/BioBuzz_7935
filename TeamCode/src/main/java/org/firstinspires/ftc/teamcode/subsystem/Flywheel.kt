package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.utils.Telemetry
import org.firstinspires.ftc.teamcode.utils.Telemetry.put


@Config
class Flywheel(hardwareMap: HardwareMap) {
    private val leftMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "leftshooter")
    private val rightMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "rightshooter")
    private var targetSpeed = 0.0
    private var lastLeftVelocity = 0.0
    private var lastRightVelocity = 0.0

    init {
        leftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER

        leftMotor.direction = if (LEFT_FORWARD) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE
        rightMotor.direction = if (RIGHT_FORWARD) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE

        reconfigure()
    }

    fun reconfigure() {
        leftMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF)
        rightMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF)
    }

    // Use cached values recorded on update to avoid many slow velocity requests
    val velocity: Double get() = (lastLeftVelocity + lastRightVelocity) / 2

    fun run(speed: Double) {
        if (targetSpeed != speed) {
            targetSpeed = speed
            leftMotor.velocity = speed
            rightMotor.velocity = speed
        }
    }

    fun atTargetSpeed(): Boolean {
        val error = targetSpeed - velocity
        return -10.0 <= error && error < 25.0
    }

    fun stop() {
        targetSpeed = 0.0
        leftMotor.power = 0.0
        rightMotor.power = 0.0
    }

    fun update() {
        if (Telemetry.PRACTICE_MODE) {
            reconfigure()
        }

        lastLeftVelocity = leftMotor.velocity
        lastRightVelocity = rightMotor.velocity

        put("flywheel/target") { targetSpeed }
        put("flywheel/left/velocity") { lastLeftVelocity }
        put("flywheel/right/velocity") { lastRightVelocity }
        put("flywheel/averageVelocity") { velocity }
    }

    @Config
    companion object {
        @JvmField var kF: Double = 14.0
        @JvmField var kP: Double = 25.0
        @JvmField var kI: Double = 0.0
        @JvmField var kD: Double = 0.0
        @JvmField var LEFT_FORWARD: Boolean = true
        @JvmField var RIGHT_FORWARD: Boolean = true
    }
}