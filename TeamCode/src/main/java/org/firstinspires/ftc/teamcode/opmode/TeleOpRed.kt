package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.subsystem.Targeter
import java.lang.Math.toDegrees
import kotlin.math.atan2


@TeleOp(name = "Red Team TeleOp")
class TeleOpRed: OpMode() {
    private lateinit var follower: Follower
    private lateinit var turret: Turret
    private lateinit var targeter: Targeter

    private var rezeroed: Boolean = false

    private val leftHive: Array<Double> = arrayOf(58.0, 90.0)
    private val rightHive: Array<Double> = arrayOf(58.0, 52.0)

    override fun init() {
        follower = Constants.create(hardwareMap)
        follower.localizer.setPose(Pose(70.0, 70.0, Math.toRadians(90.0)))
        turret = Turret(hardwareMap)

        targeter = Targeter(follower)
    }

    override fun start() {
    }

    override fun loop() {
        val robotPose = follower.pose()

        if (gamepad1.a) {
            ManualDrive.driveOrHold(
                follower,
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.left_stick_x.toDouble(),
                -gamepad1.right_stick_x.toDouble()
            )
        } else {
            follower.manual(
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.left_stick_x.toDouble(),
                -gamepad1.right_stick_x.toDouble()
            )
        }

        if (gamepad1.right_trigger.toDouble() > 0.0) {
            rezeroed = false
            // Put shooter code here
        } else {
            if (!rezeroed && !turret.is_rehoming) {
                turret.rehome()
                rezeroed = true
            }
        }

        turret.setTargetAngle(targeter.getAimValue() * 360.0)

        turret.update()
        follower.update()
    }
}