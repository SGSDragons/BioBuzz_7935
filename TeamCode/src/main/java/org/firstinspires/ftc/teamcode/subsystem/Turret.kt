package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.utils.Telemetry
import kotlin.math.max

@Config
object TurretConf {
    @JvmField var minAngle: Double = -290.0
    @JvmField var maxAngle: Double = -60.0
}

class Turret(hardwareMap: HardwareMap) {
    val servos: List<Servo> = listOf(
        hardwareMap.get(Servo::class.java, "mini")
            .also { it.direction = Servo.Direction.REVERSE },
        hardwareMap.get(Servo::class.java, "max")
    )

    @Config
    companion object {
    }

    init {
        setAngle(180.0)
    }

    fun setAngle(angle: Double) {
        var newAngle = angle
        while (newAngle > 0) {
            newAngle -= 360
        }

        val minAngle = TurretConf.minAngle
        val maxAngle = TurretConf.maxAngle

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

        Telemetry.put("turret/servo") { servoPosition }
        Telemetry.put("turret/angle") { newAngle }

        //FieldDrawing.updateTurretTarget()
    }
}