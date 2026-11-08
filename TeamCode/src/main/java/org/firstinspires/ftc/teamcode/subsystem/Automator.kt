package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.race
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedro.Constants

fun Command.forTime(ms: Int): Command = race(this, waitMs(ms.toDouble()))
fun Command.run() = schedule(this)

class Automator(val hardwareMap: HardwareMap, val start: Pose, val isRed: Boolean) {
    init {
        Scheduler.reset()
    }

    //region Class Definitions
    val follower: Follower = Constants.create(hardwareMap).also { it.setPose(start) }
    val targeter: Targeter = Targeter(follower, isRed)
    val flywheel: Flywheel = Flywheel(hardwareMap)
    val turret: Turret = Turret(hardwareMap).also { it.rehome() }
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder").also {
        it.direction = DcMotorSimple.Direction.REVERSE
    }
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")

    //endregion

    //region Commands
    val windupFlywheel: Command = newCommand(
        loop = { flywheel.run(targeter.flywheelSpeed) }
    )

    val runFlywheel: Command = newCommand(
        start = {
            intakeMotor.power = 0.9
            feederMotor.power = 0.7
        },
        loop = {
            flywheel.run(targeter.flywheelSpeed)
        },
        end = {
            flywheel.stop()
            intakeMotor.power = 0.0
            feederMotor.power = 0.0
        },
        endCondition = { false }
    )
    val rehome: Command = newCommand(
        start = { turret.rehome() },
        endCondition = { !turret.isRehoming }
    )

    val runIntake: Command = newCommand(
        start = { intakeMotor.power = 1.0 },
        end = { intakeMotor.power = 0.0 }
    )

    val shoot: Command = sequential(
        windupFlywheel.forTime(2000),
        runFlywheel.forTime(3000)
    )
    //endregion

    //region Helper Functions
    fun goto(path: com.pedropathing.paths.Path): Command {
        return follow(follower, path)
    }

    fun scheduledCommand(vararg commands: Command, withRehoming: Boolean = true): Command {
        if (withRehoming) {
            return sequential(
                rehome, *commands
            )
        } else {
            return sequential(
                *commands
            )
        }
    }

    fun newCommand(
        start: () -> Unit = {},
        loop: () -> Unit = {},
        end: () -> Unit = {},
        endCondition: () -> Boolean = { false }
    ): Command {
        return Command.build()
            .setStart { start() }
            .setExecute { loop() }
            .setDone { endCondition() }
            .setEnd { end() }
    }
    //endregion

    fun update() {
        follower.update()
        Scheduler.execute()
        turret.update()
        flywheel.update()
    }

}