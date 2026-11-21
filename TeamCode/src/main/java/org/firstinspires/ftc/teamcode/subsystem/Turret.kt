package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import kotlin.math.max

class Turret(hardwareMap: HardwareMap) {
    val servos: List<Servo> = listOf(
        hardwareMap.get(Servo::class.java, "mini"),
        hardwareMap.get(Servo::class.java, "max")
            .also { it.direction = Servo.Direction.REVERSE }
    )

    @Config
    companion object {
        @JvmField var minAngle: Double = -270.0
        @JvmField var maxAngle: Double = -90.0
    }

    init {
        setAngle(180.0)
    }

    fun setAngle(angle: Double) {
        var newAngle = -angle
        while (newAngle > 0) {
            newAngle -= 360
        }

        // Ignore it if it's too out of bounds.
        if (newAngle < minAngle - 15.0 || newAngle > maxAngle + 15.0) {
            return
        }

        newAngle = maxOf(minAngle, newAngle)
        newAngle = minOf(maxAngle, newAngle)

        val servoPosition: Double = (newAngle - minAngle) / (maxAngle - minAngle)
        servos.forEach {
            it.position = servoPosition
        }

        //FieldDrawing.updateTurretTarget()
    }
}