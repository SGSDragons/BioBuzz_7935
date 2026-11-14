package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Telemetry

class Turret(hardwareMap: HardwareMap) {
    val servos: List<Servo> = listOf(
        hardwareMap.get(Servo::class.java, "mini"),
        hardwareMap.get(Servo::class.java, "max")
            .also { it.direction = Servo.Direction.REVERSE }
    )

    @Config
    companion object {
        @JvmField var minAngle: Double = -300.0
        @JvmField var maxAngle: Double = -90.0
    }
    val validAngleRange
        get() = minAngle..maxAngle

    fun setAngle(angle: Double) {
        var newAngle = -angle
        while (newAngle > 0) {
            newAngle -= 360
        }
        if (newAngle !in validAngleRange) {
            return
        }

        val servoPosition: Double = (newAngle - validAngleRange.start) / (validAngleRange.endInclusive - validAngleRange.start)
        servos.forEach {
            it.position = servoPosition
        }

        //FieldDrawing.updateTurretTarget()
    }

    fun getAngle(): Double {
        return (servos[0].position * 360)
    }
}