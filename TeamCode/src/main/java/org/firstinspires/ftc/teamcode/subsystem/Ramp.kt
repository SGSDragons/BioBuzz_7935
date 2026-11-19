package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class Ramp(hardwareMap: HardwareMap) {
    val rampServo: Servo = hardwareMap.get(Servo::class.java, "rampservo")

    // THIS FILE IS NOT DONE!!!! DO NOT USE!

    fun lower() {
        rampServo.position = 0.25
    }

    fun raise() {
        rampServo.position = 0.0
    }
}