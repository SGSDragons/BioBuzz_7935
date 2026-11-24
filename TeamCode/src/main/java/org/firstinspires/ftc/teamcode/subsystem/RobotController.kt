package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.follower.Follower
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.commands.DriverCommands
import org.firstinspires.ftc.teamcode.utils.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.utils.AudioPlayer

class RobotController(val driverGamepad: Gamepad, val operatorGamepad: Gamepad, val hardwareMap: HardwareMap, val isRed: Boolean) {
    private val follower: Follower = Constants.create(hardwareMap)
    private val turret: Turret = Turret(hardwareMap)
    private val targeter: Targeter = Targeter(follower, isRed)
    private val flywheel = Flywheel(hardwareMap)
    private val intakeFeeder = IntakeFeeder(hardwareMap)
    private val ramp = Ramp(hardwareMap)
    private val audioPlayer = AudioPlayer(hardwareMap.appContext)
    private var autoAim: Boolean = true

    private val driverCommands = DriverCommands(driverGamepad, follower)

    fun setPose(x: Double, y: Double, headingDegrees: Double) {
        follower.localizer.setPose(Pose(x, y, Math.toRadians(headingDegrees)))
    }

    fun update() {
        if (operatorGamepad.left_bumper) {
            intakeFeeder.feedShooter()
        } else {
            // Feeder need runs slower
            intakeFeeder.setPowers(-operatorGamepad.left_stick_y.toDouble(), -operatorGamepad.right_stick_y.toDouble())
        }

        if (operatorGamepad.dpadUpWasPressed()) {
            Flywheel.kF = minOf(Flywheel.kF - 0.2, 20.0)
            flywheel.reconfigure()
        } else if (operatorGamepad.dpadDownWasPressed()) {
            Flywheel.kF = maxOf(Flywheel.kF - 0.2, 13.0)
            flywheel.reconfigure()
        } else if (operatorGamepad.dpadLeftWasPressed() || operatorGamepad.dpadRightWasPressed()) {
            Flywheel.kF = 14.0
            flywheel.reconfigure()
        }

        if (operatorGamepad.bWasPressed()) {
            autoAim = !autoAim
        }

        if (autoAim) {
            turret.setAngle(targeter.aimValue)
        } else {
            turret.setAngle(180.0)
        }
        if (follower.pose().y() >= 70.75) {
            targeter.setTarget(1)
        } else {
            targeter.setTarget(0)
        }

        if (operatorGamepad.x) {
            ramp.lower()
        }

        if (operatorGamepad.a) {
            ramp.agitate()
        }

        if (operatorGamepad.y) {
            ramp.raise()
        }

        if (operatorGamepad.start || driverGamepad.start) {
            audioPlayer.playSound(audioPlayer.factorioDestroyed)
        }

        if (operatorGamepad.right_bumper) {
            flywheel.run(targeter.flywheelSpeed)
        } else {
            flywheel.stop()
        }

        driverCommands.update()

        follower.update()
        ramp.update()
        intakeFeeder.update()
        flywheel.update()
        Telemetry.send()
    }
}