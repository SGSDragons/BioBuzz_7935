package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.firstinspires.ftc.teamcode.subsystem.FieldDrawing

object Telemetry {
    private var packet = TelemetryPacket(false)
    private var lastTick = 0L


    @JvmStatic
    fun put(key: String, value: Any) {
        packet.put(key, value)
    }

    @JvmStatic
    fun send() {
        val now = System.currentTimeMillis()
        packet.put("latencyMs", now-lastTick)
        lastTick = now

        FieldDrawing.update(packet.fieldOverlay())
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        packet = TelemetryPacket(false)
    }
}