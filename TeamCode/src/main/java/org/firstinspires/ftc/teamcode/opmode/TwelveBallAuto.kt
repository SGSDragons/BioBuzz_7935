package org.firstinspires.ftc.teamcode.opmode

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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedro.Constants
import org.firstinspires.ftc.teamcode.subsystem.Automator
import org.firstinspires.ftc.teamcode.subsystem.forTime
import org.firstinspires.ftc.teamcode.utils.Telemetry
import kotlin.math.cos
import kotlin.math.sin

abstract class TwelveBallAuto(val isRed: Boolean) : LinearOpMode() {
    val poseFactory: PoseFactory = PoseFactory.degrees().let {
        if (isRed) {
            it.mirrorAroundPoint(70.75, 70.75)
        } else {
            it
        }
    }

    private val start = poseFactory.of(87.0, 133.0, 90.0)
    private val prepPickup1 = poseFactory.of(133.0, 131.0, 90.0)
    private val prepPickup1Control1 = poseFactory.of(133.0, 89.932, 0.0)
    private val postPickup1Start = poseFactory.of(133.0, 131.0, 90.0)
    private val postPickup1 = poseFactory.of(125.3705, 107.4345, -130.0)
    private val traverseStart = poseFactory.of(125.3705, 107.4345, -130.0)
    private val traverse = poseFactory.of(92.0, 13.6138, -90.0)
    private val traverseControl1 = poseFactory.of(117.4388, 85.4937, 0.0)
    private val traverseControl2 = poseFactory.of(121.6276, 30.299, 0.0)
    private val traverseControl3 = poseFactory.of(92.0, 41.4054, 0.0)
    private val reverse = poseFactory.of(92.0, 30.412, -90.0)
    private val prepPickup2 = poseFactory.of(92.0, 13.0, -90.0)
    private val postPickup2 = poseFactory.of(92.0, 18.0, -90.0)
    private val park = poseFactory.of(131.4913, 47.91, -165.6366)
    private val parkControl1 = poseFactory.of(94.6773, 38.4829, 0.0)

    fun prepPickup1(): Path = Paths.curve(start, prepPickup1Control1, prepPickup1).linear(start, prepPickup1)

    fun postPickup1(): Path = Paths.line(postPickup1Start, postPickup1).linear(postPickup1Start, postPickup1)

    fun traverse(): Path = Paths.curve(traverseStart, traverseControl1, traverseControl2, traverseControl3, traverse).linear(traverseStart, traverse)

    fun reverse(): Path = Paths.line(traverse, reverse).constant(reverse)

    fun prepPickup2(): Path = Paths.line(reverse, prepPickup2).constant(prepPickup2)

    fun postPickup2(): Path = Paths.line(prepPickup2, postPickup2).constant(postPickup2)

    fun park(): Path = Paths.curve(postPickup2, parkControl1, park).reverseTangent()

    fun autoRoutine(a: Automator): Command = sequential(
        a.windupFlywheel,
        a.shoot,

        // Corner pickup
        a.startIntake,
        a.goto(prepPickup1()),
        Commands.waitMs(1000.0),
        a.goto(reverseForward(prepPickup1, 6.0, 3.0)),
        Commands.waitMs(500.0),
        a.stopIntake,
        a.goto(postPickup1()),

        // Drive to other side and shoot
        a.goto(traverse()),
        a.shoot,

        a.lowerRamp,
        a.goto(reverse()),
        a.startIntake,
        a.goto(prepPickup2()).forTime(1000), // Overshoot and stop if it takes too long
        Commands.waitMs(1500.0),
        a.goto(reverseForward(prepPickup2, 6.0, 0.0)).forTime(1000),
        Commands.waitMs(500.0),
        a.stopIntake,
        a.shoot,

        // Park
        a.goto(postPickup2()),
        a.raiseRamp,
        a.goto(park())
    )

    override fun runOpMode() {

        val automator = Automator(hardwareMap, start, isRed)

        waitForStart()
        schedule(autoRoutine(automator))

        while (opModeIsActive()) {
            automator.update()
            Telemetry.send()
        }
    }

    fun reverseForward(startPose: Pose, reverse: Double, strafe: Double): Path {
        val heading = startPose.heading() // radians in Pedro's coordinate system

        // Step backward along (-cos(heading), -sin(heading)) so it works for any pose
        val reversePose = Pose(
            startPose.x() - reverse * cos(heading) - strafe * sin(heading),
            startPose.y() - reverse * sin(heading) + strafe * cos(heading),
            heading
        )

        val reversePath = Paths.line(startPose, reversePose).constant(heading)
        val forwardPath = Paths.line(reversePose, startPose).constant(heading)

        return Paths.path(reversePath, forwardPath)
    }

}