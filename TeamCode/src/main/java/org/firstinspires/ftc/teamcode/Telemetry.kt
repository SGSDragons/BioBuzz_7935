package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.firstinspires.ftc.teamcode.subsystem.FieldDrawing

object Telemetry {
    private var packet = TelemetryPacket(false)

    @JvmStatic
    fun put(key: String, value: Any) {
        packet.put(key, value)
    }

    @JvmStatic
    fun send() {
        FieldDrawing.update(packet.fieldOverlay())
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        packet = TelemetryPacket(false)
    }
}