package org.firstinspires.ftc.teamcode.opmode.tests

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.interpolator.Interpolator
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.pedro.PointArrayLoader
import org.firstinspires.ftc.teamcode.subsystem.Automator
import org.firstinspires.ftc.teamcode.subsystem.forTime
import org.firstinspires.ftc.teamcode.utils.Telemetry
import kotlin.math.cos
import kotlin.math.sin

@Autonomous(name = "TwelveBallAutoExperimental", group = "Test")
class TwelveBallAutoExperimental : LinearOpMode() {
    val isRed = true
    val poseFactory: PoseFactory = PoseFactory.degrees().let {
        if (isRed) {
            it
        } else {
            it.mirrorAroundPoint(70.75, 70.75)
        }
    }

    lateinit var automator: Automator
    val auto = PointArrayLoader.load("TwelveBallAuto", poseFactory).also { it ->
        listOf("FlowerPickup1", "FlowerPickup2").forEach { name ->
            it.customize(name) { it.with(Constants.foresightConfig.maxPathSpeed.at(0.4)) }
        }
    }

    fun autoRoutine(a: Automator): Command = sequential(
        a.windupFlywheel,
        a.shoot,

        // Take from flower 1
        a.startIntake,
        a.goto(auto["PrepFlowerPickup1"].path()),
        a.lowerRamp,
        a.concurrentCommand(
            a.goto(auto["FlowerPickup1"].path()),
            a.agitateRamp
        ),
        a.goto(auto["FlowerBackup1"].path()),
        a.raiseRamp,
        a.stopIntake,

        // Traverse and shoot
        a.goto(auto["Traverse1"].path()),
        a.shoot,

        // Take from flower 2
        a.startIntake,
        a.goto(auto["PrepFlowerPickup2"].path()),
        a.lowerRamp,
        a.concurrentCommand(
            a.goto(auto["FlowerPickup2"].path()),
            a.agitateRamp
        ),
        a.goto(auto["FlowerBackup2"].path()),
        a.raiseRamp,
        a.stopIntake,

        // Shoot
        a.goto(auto["Traverse2"].path()),
        a.shoot,
        a.goto(auto["Park"].path())

    )

    override fun runOpMode() {

        automator = Automator(hardwareMap, auto.startPose, isRed)

        waitForStart()
        schedule(autoRoutine(automator))

        while (opModeIsActive()) {
            automator.update()
            Telemetry.send()
        }
    }
}