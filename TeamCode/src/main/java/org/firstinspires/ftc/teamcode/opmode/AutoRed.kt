package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Automator

@Autonomous(name = "Red Autonomous")
class AutoRed: OpMode() {
    private lateinit var automator: Automator

    //region PedroPathing Values
    private val poseFactory = PoseFactory.degrees()
    private val start = poseFactory.of(58.8389, 7.2903, 90.0)
    private val path1 = poseFactory.of(12.4909, 98.3104, 90.0)
    private val path1Control1 = poseFactory.of(18.6174, 29.3906, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).linear(start, path1)
    //endregion

    private val schedule = automator.newSchedule(
        automator.shoot,
        automator.goto(path1())
    )

    override fun init() {
        automator = Automator(hardwareMap, start, true)
    }

    override fun start() {
        automator.followSchedule(schedule)
    }

    override fun loop() {
        automator.update()
        Telemetry.send()
    }
}