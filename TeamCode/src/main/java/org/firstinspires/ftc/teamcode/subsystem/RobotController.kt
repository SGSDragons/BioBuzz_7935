package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants

class RobotController(val driverGamepad: Gamepad, val operatorGamepad: Gamepad, val hardwareMap: HardwareMap, val isRed: Boolean) {
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder")
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")

    // Reuse an existing follower, since it could be coming from Autonomous
    private val follower: Follower = Constants.getOrCreate(hardwareMap)
    private val turret: Turret = Turret(hardwareMap)
    private val targeter: Targeter = Targeter(follower, isRed)
    private val flywheel = Flywheel(hardwareMap)

    private var autoAim: Boolean = true

    fun setPose(pose: Pose) {
        follower.localizer.setPose(pose)
    }

    fun update() {
        feederMotor.power = operatorGamepad.right_stick_y.toDouble()
        intakeMotor.power = operatorGamepad.left_stick_y.toDouble()

        // Operator Controls

        if (operatorGamepad.x) {
            targeter.setTarget(0)
        } else if (operatorGamepad.y) {
            targeter.setTarget(1)
        }

        if (operatorGamepad.aWasPressed()) {
            turret.rehome()
        }
        if (operatorGamepad.bWasPressed()) {
            autoAim = !autoAim
        }

        if (autoAim) {
            turret.setTargetAngle(targeter.getAimValue() * 360.0)
        } else {
            turret.setTargetAngle(turret.turretRotations + operatorGamepad.left_trigger / 90 - operatorGamepad.right_trigger / 90)
        }

        if (operatorGamepad.right_bumper) {
            flywheel.run(targeter.flywheelSpeed)
        } else {
            flywheel.stop()
        }

        // Driver Controls

        val powers = DrivePowers(
            -driverGamepad.left_stick_y.toDouble(),
            -driverGamepad.left_stick_x.toDouble(),
            -driverGamepad.right_stick_x.toDouble());

        if (driverGamepad.y) {
            ManualDrive.driveOrHold(
                follower,
                powers
            )
        } else {
            ManualDrive.fieldCentric(
                powers,
                follower.pose().heading()
            )
        }

        turret.update()
        follower.update()
        flywheel.update()
        Telemetry.send()
    }
}