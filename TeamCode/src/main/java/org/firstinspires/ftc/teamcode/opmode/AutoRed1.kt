package org.firstinspires.ftc.teamcode.opmode

import com.pedropathing.api.Paths
import com.pedropathing.api.PoseFactory
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.utils.Telemetry
import org.firstinspires.ftc.teamcode.subsystem.Automator
import org.firstinspires.ftc.teamcode.subsystem.forTime
import org.firstinspires.ftc.teamcode.subsystem.run

@Autonomous(name = "Red Autonomous")
class AutoRed1: OpMode() {
    private lateinit var automator: Automator

    //region PedroPathing Values
    private val poseFactory = PoseFactory.degrees()

    private val start = poseFactory.of(58.3583, 6.8902, 270.0)
    private val path1 = poseFactory.of(9.8044, 7.1451, 270.0)
    private val path1Control1 = poseFactory.of(33.3401, 36.7588, 0.0)
    private val point2Start = poseFactory.of(9.8044, 7.1451, 270.0)
    private val point2 = poseFactory.of(57.9274, 126.9949, 90.0)
    private val point2Control1 = poseFactory.of(34.2401, 43.7758, 0.0)
    private val point2Control2 = poseFactory.of(6.9299, 138.3176, 0.0)
    private val point2Control3 = poseFactory.of(59.7605, 104.8926, 0.0)
    private val point3Start = poseFactory.of(57.9274, 128.9949, 90.0)
    private val point3 = poseFactory.of(11.1804, 114.2562, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).constant(path1)

    fun path2(): Path = Paths.curve(point2Start, point2Control1, point2Control2, point2Control3, point2).linear(point2Start, point2)

    fun path3(): Path = Paths.line(point3Start, point3).linear(point3Start, point3)
    //endregion

    override fun init() {
        automator = Automator(hardwareMap, start, true)
    }

    val schedule = automator.scheduledCommand(
        automator.shoot,
        automator.concurrentCommand(automator.goto(path1()), automator.runIntake.forTime(4000)),
        automator.goto(path2()),
        automator.shoot,
        automator.goto(path3())
    )

    override fun start() {
        schedule.run()
    }

    override fun loop() {
        automator.update()
        Telemetry.send()
    }
}