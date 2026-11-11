package org.firstinspires.ftc.teamcode.subsystem

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

    private val follower: Follower = Constants.create(hardwareMap)
    private val turret: Turret = Turret(hardwareMap).also { it.rehome() }
    private val targeter: Targeter = Targeter(follower, isRed)
    private val flywheel = Flywheel(hardwareMap)
    private val audioPlayer = AudioPlayer(hardwareMap)

    private var autoAim: Boolean = true

    fun setPose(x: Double, y: Double, headingDegrees: Double) {
        follower.localizer.setPose(Pose(x, y, Math.toRadians(headingDegrees)))
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

        if (operatorGamepad.start || driverGamepad.start) {
            audioPlayer.errorSound()
        }

        if (driverGamepad.aWasPressed()) {
            if (isRed) {
                setPose(56.0, 134.0, 270.0)
            } else {
                setPose(104.0, 7.0, 90.0)
            }
        }

        if (autoAim) {
            turret.setTargetAngle(targeter.getAimValue())
        } else {
            turret.setTargetAngle(turret.turretRotations + operatorGamepad.left_trigger - operatorGamepad.right_trigger)
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

        when {
            driverGamepad.aWasPressed() -> follower.setPose(FieldPoints.nearFlower)
            driverGamepad.xWasPressed() -> follower.setPose(FieldPoints.leftFlower)
            driverGamepad.bWasPressed() -> follower.setPose(FieldPoints.rightFlower)
            driverGamepad.yWasPressed() -> follower.setPose(FieldPoints.farFlower)
            driverGamepad.dpadUpWasPressed() -> follower.setHeading(0.0)
            driverGamepad.dpadDownWasPressed() -> follower.setHeading(180.0)
            driverGamepad.dpadLeftWasPressed() -> follower.setHeading(90.0)
            driverGamepad.dpadRightWasPressed() -> follower.setHeading(-90.0)
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