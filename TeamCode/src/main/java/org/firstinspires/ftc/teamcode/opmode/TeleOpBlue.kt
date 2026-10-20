package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.subsystem.Turret


@TeleOp(name = "Blue Team TeleOp")
class TeleOpBlue: OpMode() {
    private lateinit var follower: Follower

    override fun init() {
        follower = Constants.create(hardwareMap)
        follower.localizer.setPose(Pose(136.0, 82.0, 0.0))
    }

    override fun start() {
    }

    override fun loop() {
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

        follower.update()
    }
}