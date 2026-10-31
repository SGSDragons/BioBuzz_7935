package org.firstinspires.ftc.teamcode.opmode

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
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Targeter
import org.firstinspires.ftc.teamcode.subsystem.Turret

@Autonomous(name = "Red Autonomous")
class AutoRed: OpMode() {
    private val poseFactory = PoseFactory.degrees()
    private lateinit var follower: Follower
    private lateinit var flywheel: Flywheel
    private lateinit var targeter: Targeter
    private lateinit var turret: Turret

    private lateinit var feederMotor: DcMotor
    private lateinit var intakeMotor: DcMotor

    private val start = poseFactory.of(58.4971, 6.474, 90.0);
    private val path1 = poseFactory.of(9.6657, 92.7387, 90.0)
    private val path1Control1 = poseFactory.of(18.8284, 27.1184, 0.0)

    fun path1(): Path = Paths.curve(start, path1Control1, path1).constant(path1)

    val windup = Command.build()
        .setExecute { flywheel.run(targeter.flywheelSpeed) }
        .setDone { false }
    val shoot: Command = Command.build()
        .setExecute {
            flywheel.run(targeter.flywheelSpeed)
            intakeMotor.power = 0.9
            feederMotor.power = 0.7
        }
        .setDone { false }
        .setEnd {
            flywheel.stop()
            intakeMotor.power = 0.0
            feederMotor.power = 0.0
        }

    val rehome = Command.build()
        .setExecute { turret.rehome() }
        .setDone { !turret.isRehoming }

    private fun autoRoutine(): Command {
        return sequential(
            rehome,
            race(windup, waitMs(2000.0)),
            race(shoot, waitMs(3000.0)),
            follow(follower, path1())
        )
    }

    override fun init() {
        Scheduler.reset()
        follower = Constants.create(hardwareMap)
        follower.setPose(start)
        follower.update()
        targeter = Targeter(follower, true)
        flywheel = Flywheel(hardwareMap)
        turret = Turret(hardwareMap)
        turret.rehome()

        feederMotor = hardwareMap.get(DcMotor::class.java, "feeder").also {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
        intakeMotor = hardwareMap.get(DcMotor::class.java, "intake")
    }

    override fun start() {
        schedule(autoRoutine())
    }

    override fun loop() {
        follower.update()
        Scheduler.execute()
        turret.update()
        flywheel.update()
        Telemetry.send()
    }
}