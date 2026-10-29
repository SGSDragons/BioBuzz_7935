package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.math.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.RobotController

@TeleOp(name = "Blue Team TeleOp 1")
class TeleOpBlue1: OpMode() {

    private lateinit var robotController: RobotController

    override fun init() {
        robotController = RobotController(gamepad1, gamepad2, hardwareMap, false)
    }

    override fun start() {
        robotController.setPose(Pose(70.0, 70.0, 0.0))
    }

    override fun loop() {
        robotController.update()
        Telemetry.send()
    }
}

@TeleOp(name = "Blue Team TeleOp 2")
class TeleOpBlue2: OpMode() {

    private lateinit var robotController: RobotController

    override fun init() {
        robotController = RobotController(gamepad1, gamepad2, hardwareMap, false)
    }

    override fun start() {
        robotController.setPose(Pose(10.0, 10.0, 0.0))
    }

    override fun loop() {
        robotController.update()
        Telemetry.send()
    }
}