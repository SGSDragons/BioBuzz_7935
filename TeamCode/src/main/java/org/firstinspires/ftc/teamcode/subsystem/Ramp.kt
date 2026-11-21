package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime

@Config
class Ramp(hardwareMap: HardwareMap) {
    val rampServo: Servo = hardwareMap.get(Servo::class.java, "rampservo")

    private var timer: ElapsedTime = ElapsedTime()
    private var lowered: Boolean = false
    private var agitate: Boolean = false

    fun lower() {
        lowered = true
        rampServo.position = loweredPos
    }

    fun agitate() {
        timer.reset()
        agitate = true
    }

    fun raise() {
        lowered = false
        agitate = false
        rampServo.position = raisedPos
    }

    fun update() {
        if (lowered && agitate) {
            if (timer.milliseconds() >= 250) {
                rampServo.position = loweredPos + 0.05
                if (timer.milliseconds() >= 500) timer.reset()
            } else {
                rampServo.position = loweredPos
            }
        }
    }

    @Config
    companion object {
        @JvmField var loweredPos: Double = 0.15
        @JvmField var raisedPos: Double = 0.9
        // 0.9, 0.15
    }
}