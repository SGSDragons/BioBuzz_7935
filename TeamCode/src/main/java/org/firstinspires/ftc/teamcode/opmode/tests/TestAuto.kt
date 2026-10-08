package org.firstinspires.ftc.teamcode.opmode.tests

import com.pedropathing.api.Paths.line
import com.pedropathing.api.PoseFactory
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.pedro.Constants

@Autonomous(name = "Test Autonomous", group = "Test")
class TestAuto: OpMode() {
    private lateinit var follower: Follower

    // Create waypoints along path
    private val p: PoseFactory = PoseFactory.degrees()

    private val startPose: Pose? = p.of(24.0, 24.0, 0.0)
    private val scorePose: Pose? = p.of(48.0, 48.0, 90.0)
    private val parkPose: Pose? = p.of(72.0, 48.0, 90.0)

    // Create paths between waypoints
    private fun startToScore(): Path {
        return line(startPose, scorePose).linear(startPose, scorePose)
    }
    private fun park(): Path {
        return line(scorePose, parkPose).linear(scorePose, parkPose)
    }

    // Tell Ivy what order to follow the paths
    private fun autoRoutine(): Command {
        return sequential(
            follow(follower, startToScore()),
            follow(follower, park())
        )
    }

    // Tie it into the auto
    override fun init() {
        // The init function and start function do not happen at the same time!
        // Start is when the Autonomous OpMode begins
        // Init is the first button you press BEFORE the start button
        Scheduler.reset()

        follower = Constants.create(hardwareMap)
        follower.setPose(startPose)
        follower.update()
    }

    override fun start() {
        schedule(autoRoutine())
    }

    override fun loop() {
        // This function runs continuously while the Autonomous is active
        follower.update()
        Scheduler.execute()

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}