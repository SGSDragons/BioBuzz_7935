package org.firstinspires.ftc.teamcode.opmode.tests

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs

@TeleOp(name = "Servo Lag Timer", group = "Tests")
class ServoTimer : LinearOpMode() {
    override fun runOpMode() {

        val timer = ElapsedTime()
        val stationaryTimer = ElapsedTime()
        val servo = hardwareMap.get(Servo::class.java, "360servo")
        val servoPos = hardwareMap.get(AnalogInput::class.java, "servopose")

        waitForStart()

        var state = "IDLING"
        var idlePos = 0.0
        var lastPos = 0.0
        var stopPos = 0.0

        var lastTime = System.nanoTime()

        while (opModeIsActive()) {
            val pos = servoPos.voltage / servoPos.maxVoltage
            val now = System.nanoTime()

            if (lastPos < pos) {
                lastPos = pos
                lastTime = now
                continue
            }

            val dtMillis = (now - lastTime) / 1e3
            val rotsPerS = (lastPos - pos) / dtMillis

            val packet = TelemetryPacket()
            packet.put("Volts", servoPos.voltage)
            packet.put("Speed", rotsPerS)
            FtcDashboard.getInstance().sendTelemetryPacket(packet)

            when(state) {
                "READY" -> {
                    idlePos = pos
                    servo.position = 0.9

                    timer.reset()
                    state = "RUNNING"
                }
                "RUNNING" -> {
                    if (abs(pos - idlePos) > 0.005) {
                        val packet = TelemetryPacket()
                        packet.put("Start Delay", timer.milliseconds())
                        FtcDashboard.getInstance().sendTelemetryPacket(packet)

                        timer.reset()
                        state = "SPINNING"
                    }
                }
                "SPINNING" -> {
                    if (timer.seconds() > 10.0) {
                        stopPos = pos
                        servo.position = 0.5

                        stationaryTimer.reset()
                        timer.reset()
                        state = "STOPPING"
                    }
                }
                "STOPPING" -> {
                    if (abs(pos - lastPos) > 0.005) {
                        stationaryTimer.reset()
                    } else if (stationaryTimer.milliseconds() > 50) {
                        val packet = TelemetryPacket()
                        packet.put("Stop Delay", timer.milliseconds() - stationaryTimer.milliseconds())
                        packet.put("Stop Drift", abs(pos - stopPos))
                        FtcDashboard.getInstance().sendTelemetryPacket(packet)
                        state = "IDLING"
                    }
                }
                "IDLING" -> {
                    if (timer.seconds() > 5.0) {
                        state = "READY"
                    }
                }
            }

            lastPos = pos
            lastTime = now
        }
    }

}