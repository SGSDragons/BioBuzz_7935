package org.firstinspires.ftc.teamcode.utils

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket

@Config
object Telemetry {
    var packet = TelemetryPacket(false)
    private var lastTick = 0L
    @JvmField var PRACTICE_MODE: Boolean = false

    inline fun put(key: String, value: () -> Any) {
        if (PRACTICE_MODE) {
            packet.put(key, value())
        }
    }

    @JvmStatic
    fun send() {
        val now = System.currentTimeMillis()
        packet.put("latencyMs", now - lastTick)
        lastTick = now

        if (PRACTICE_MODE) {
            FieldDrawing.update(packet.fieldOverlay())
        }
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        packet = TelemetryPacket(false)
    }
}