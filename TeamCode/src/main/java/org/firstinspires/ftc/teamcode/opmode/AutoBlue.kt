package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Automator

@Autonomous(name = "Blue Autonomous")
class AutoBlue: OpMode() {
    private lateinit var automator: Automator

    //region PedroPathing Values
    private val poseFactory = PoseFactory.degrees()
    private val start = poseFactory.of(82.6144, 133.7975, 270.0)
    private val path1 = poseFactory.of(125.7298, 40.7906, 270.0)
    private val path1Control1 = poseFactory.of(121.8211, 86.763, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).linear(start, path1)
    //endregion

    private val schedule = automator.newSchedule(
        automator.shoot,
        automator.goto(path1())
    )

    override fun init() {
        automator = Automator(hardwareMap, start, false)
    }

    override fun start() {
        automator.followSchedule(schedule)
    }

    override fun loop() {
        automator.update()
        Telemetry.send()
    }
}