package org.firstinspires.ftc.teamcode.opmode.tests

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.race
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.subsystem.Automator
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Targeter
import org.firstinspires.ftc.teamcode.subsystem.Turret

@Autonomous(name = "Red Autonomous Autofeed Test", group = "Test")
class AutoRedFeedTest: OpMode() {
    private val poseFactory = PoseFactory.degrees()
    private lateinit var automator: Automator

    private val start = poseFactory.of(58.8389, 7.2903, 90.0)
    private val path1 = poseFactory.of(7.8777, 7.6439, 270.0)
    private val path1Control1 = poseFactory.of(18.6174, 29.3906, 0.0)
    private val point2Start = poseFactory.of(7.8777, 7.6439, 270.0)
    private val point2 = poseFactory.of(58.6397, 7.0206, 90.0)
    private val point2Control1 = poseFactory.of(40.0825, 36.244, 0.0)
    private val point3 = poseFactory.of(11.8282, 98.8892, 95.7969)
    private val point3Control1 = poseFactory.of(17.0616, 47.3385, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).linear(start, path1)

    fun path2(): Path = Paths.curve(point2Start, point2Control1, point2).linear(point2Start, point2)

    fun path3(): Path = Paths.curve(point2, point3Control1, point3).tangent()

    val schedule = automator.newSchedule(
        automator.rehome,
        automator.shoot,
        automator.goto(path1()),
        automator.intake,
        automator.goto(path2()),
        automator.shoot,
        automator.goto(path3())
    )

    override fun init() {
        automator = Automator(hardwareMap, start, true)
    }

    override fun start() {
        automator.runSchedule(schedule)
    }

    override fun loop() {
        automator.update()
        Telemetry.send()
    }
}