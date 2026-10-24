package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.pedropathing.math.Pose
import org.firstinspires.ftc.teamcode.Telemetry
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object FieldDrawing {
    private var prevBotPose: Pose = Pose.zero()
    private var prevTurret: Double = 0.0
    private var prevTarget: Double = 0.0
    private var needsUpdate = true

    fun updateBotPose(pose: Pose) {
        var noticeableDiff = false
        noticeableDiff = noticeableDiff or (abs(pose.x() - prevBotPose.x()) >= 1.0)
        noticeableDiff = noticeableDiff or (abs(pose.y() - prevBotPose.y()) >= 1.0)
        noticeableDiff = noticeableDiff or (abs(pose.heading() - prevBotPose.heading()) >= Math.toRadians(5.0))

        if (noticeableDiff) {
            prevBotPose = pose;
            needsUpdate = true;
        }
    }
    fun updateTurretRotations(rotations: Double) {
        if (abs(rotations - prevTurret) > Math.toDegrees(5.0)) {
            needsUpdate = true
            prevTurret = rotations
        }
    }

    fun updateTurretTarget(rotations: Double) {
        if (abs(rotations - prevTarget) > Math.toDegrees(5.0)) {
            needsUpdate = true
            prevTarget = rotations
        }
    }

    fun update(field: Canvas) {
        if (!needsUpdate) {
            return
        }

        val width = 144.0

        field.drawImage("/images/field.png", 0.0, 0.0, width, width)

        // Field Frame has (0,0) at the center of the field.

        val robotX: Double = -width/2.0 + prevBotPose.x()
        val robotY: Double = prevBotPose.y() - width/2.0
        val robotHeading: Double = prevBotPose.heading()

        field.drawImage(
            "/images/bot.png",
            robotX, robotY,
            18.0, 18.0,
            -robotHeading,
            9.0, 9.0,
            false
        )

        // Draw the way that the turret is pointing
        val worldAngle = Math.PI * 2 * prevTurret + robotHeading
        val length = 13.0

        field.setStrokeWidth(2)

        field.setStroke("#FF0000")
        field.strokeLine(0.0, 0.0, -72.0, 0.0)
        field.setStroke("#00FF00")
        field.strokeLine(0.0, 0.0, 0.0, -72.0)
        field.setStroke("#0000FF")
        field.strokeLine(0.0, 0.0, 72.0, 0.0)
        field.setStroke("#FF00FF")
        field.strokeLine(0.0, 0.0, 0.0, 72.0)
//            80.0, 80.0, 83.0, 83.0)
//            robotX + 3, robotY+3
//        )
    }
}