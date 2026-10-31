package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.RobotController

@TeleOp(name = "Blue Team TeleOp 1")
class TeleOpBlue1: OpMode() {

    private lateinit var robotController: RobotController

    override fun init() {
        robotController = RobotController(gamepad1, gamepad2, hardwareMap, false)
    }

    override fun start() {
        robotController.setPose(125.7298, 40.7906, 270.0)
    }

    override fun loop() {
        robotController.update()
    }
}