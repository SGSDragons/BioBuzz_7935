package org.firstinspires.ftc.teamcode.subsystem

import com.bylazar.field.PanelsField
import com.pedropathing.math.Pose
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class FieldDrawing {
    val panelsField = PanelsField.field

    fun initField() {
        panelsField.setOffsets(PanelsField.presets.PEDRO_PATHING)
    }

    fun drawRobot(robotPose: Pose, turretRevolutions: Double) {
        val robotX: Double = robotPose.x()
        val robotY: Double = robotPose.y()
        val robotHeading: Double = robotPose.heading()

        panelsField.moveCursor(robotX, robotY) // This says where to draw everything

        // Draw the way that the robot is pointing
        panelsField.setStyle("#ed8796", "#ed8796", 3.0)
        panelsField.line(cos(robotHeading) * 6, sin(robotHeading) * 6)

        // Draw the way that the turret is pointing
        panelsField.setStyle("#7dc4e4", "#7dc4e4", 2.0)
        panelsField.line(cos(turretRevolutions * (2 * PI)) * 6, sin(turretRevolutions * (2 * PI)) * 6)

        // Draw the robot itself
        panelsField.setStyle("#8087a2", "#494d64", 3.0)
        panelsField.circle(2.0)

        panelsField.update()
    }
}