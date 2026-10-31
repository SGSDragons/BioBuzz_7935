package org.firstinspires.ftc.teamcode.opmode.tests

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Automator

@Autonomous(name = "Blue Automator Test", group = "Test")
class AutoBlueTest: OpMode() {
    private val poseFactory = PoseFactory.degrees()
    private lateinit var automator: Automator

    private val start = poseFactory.of(82.6144, 133.7975, 270.0)
    private val path1 = poseFactory.of(125.7298, 40.7906, 270.0)
    private val path1Control1 = poseFactory.of(121.8211, 86.763, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).linear(start, path1)

    private fun autoRoutine(): Command {
        return sequential(
            automator.rehome,
            automator.shoot,
            follow(automator.follower, path1())
        )
    }

    override fun init() {
        automator = Automator(hardwareMap, start, false)
    }

    override fun start() {
        automator.start(autoRoutine())
    }

    override fun loop() {
        automator.update()
        Telemetry.send()
    }
}