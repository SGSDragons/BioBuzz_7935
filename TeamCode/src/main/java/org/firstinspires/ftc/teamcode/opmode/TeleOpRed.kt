package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.math.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.RobotController


@TeleOp(name = "Red Team TeleOp 1")
class TeleOpRed1: OpMode() {

    private lateinit var robotController: RobotController

    override fun init() {
        robotController = RobotController(gamepad1, gamepad2, hardwareMap, true)
    }

    override fun start() {
        robotController.setPose(9.6657, 92.7387, 90.0)
    }

    override fun loop() {
        robotController.update()
    }
}