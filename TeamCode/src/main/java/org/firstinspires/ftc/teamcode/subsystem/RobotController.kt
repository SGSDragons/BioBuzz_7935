package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants

class RobotController(val driverGamepad: Gamepad, val operatorGamepad: Gamepad, val hardwareMap: HardwareMap, val isRed: Boolean) {
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder").also {
        it.direction = DcMotorSimple.Direction.REVERSE
    }
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")
    val intakePower = 0.9
    val feederPower = 1.0

    // Reuse an existing follower, since it could be coming from Autonomous
    private val follower: Follower = Constants.recreate(hardwareMap)
    private val turret: Turret = Turret(hardwareMap)
    private val targeter: Targeter = Targeter(follower, isRed)
    private val flywheel = Flywheel(hardwareMap)

    private var autoAim: Boolean = true

    fun setPose(pose: Pose) {
        follower.localizer.setPose(pose)
    }

    fun update() {
        if (operatorGamepad.left_bumper) {
            intakeMotor.power = intakePower
            feederMotor.power = feederPower
        } else {
            intakeMotor.power = -operatorGamepad.left_stick_y.toDouble()
            feederMotor.power = -operatorGamepad.right_stick_y.toDouble()
        }

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
        follower.manual(
            ManualDrive.fieldCentric(
            -driverGamepad.left_stick_y.toDouble(),
            -driverGamepad.left_stick_x.toDouble(),
            -driverGamepad.right_stick_x.toDouble(),
            follower.pose().heading()));

        if (driverGamepad.dpad_up) {
            follower.setHeading(0.0)
        }

        Telemetry.put("left stick y", -driverGamepad.left_stick_y)
        Telemetry.put("left stick x", -driverGamepad.left_stick_x)
        Telemetry.put("right stick x", -driverGamepad.right_stick_x)

        turret.update()
        follower.update()
        flywheel.update()
        Telemetry.send()
    }
}