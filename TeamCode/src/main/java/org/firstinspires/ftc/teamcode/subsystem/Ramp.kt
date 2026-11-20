package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

@Config
class Ramp(hardwareMap: HardwareMap) {
    val rampServo: Servo = hardwareMap.get(Servo::class.java, "rampservo")

    fun lower() {
        rampServo.position = lowered
    }

    fun raise() {
        rampServo.position = raised
    }

    @Config
    companion object {
        @JvmField var lowered: Double = 0.15
        @JvmField var raised: Double = 0.9
        // 0.9, 0.15
    }
}