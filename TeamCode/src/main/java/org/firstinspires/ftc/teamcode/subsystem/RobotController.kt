package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedro.Constants

class RobotController(val driverGamepad: Gamepad, val operatorGamepad: Gamepad, val hardwareMap: HardwareMap, val isRed: Boolean) {
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder")
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")

    private val follower: Follower = Constants.create(hardwareMap)
    private val turret: Turret = Turret(hardwareMap)
    private val targeter: Targeter = Targeter(follower, isRed)

    private var turretToggle: Boolean = true

    fun setPose(pose: Pose) {
        follower.localizer.setPose(pose)
    }

    fun getPose(): Pose {
        return follower.pose()
    }

    fun update() {
        feederMotor.power = operatorGamepad.right_stick_y.toDouble()
        intakeMotor.power = operatorGamepad.left_stick_y.toDouble()

        if (operatorGamepad.x) {
            targeter.setTarget(0)
        } else if (operatorGamepad.y) {
            targeter.setTarget(1)
        }

        if (operatorGamepad.a && !turret.is_rehoming) {
            turret.rehome()
        }

        if (operatorGamepad.b) {
            turretToggle = !turretToggle
        }

        if (driverGamepad.y) {
            ManualDrive.driveOrHold(
                follower,
                -driverGamepad.left_stick_y.toDouble(),
                -driverGamepad.left_stick_x.toDouble(),
                -driverGamepad.right_stick_x.toDouble()
            )
        } else {
            follower.manual(
                -driverGamepad.left_stick_y.toDouble(),
                -driverGamepad.left_stick_x.toDouble(),
                -driverGamepad.right_stick_x.toDouble()
            )
        }

        if (turretToggle) {
            turret.setTargetAngle(targeter.getAimValue() * 360.0)
        } else {
            turret.setTargetAngle(turret.turretRotations + operatorGamepad.left_trigger / 90 - operatorGamepad.right_trigger / 90)
        }
        turret.update()
        follower.update()
    }
}