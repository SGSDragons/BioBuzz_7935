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
import java.util.function.BooleanSupplier

fun Command.forTime(ms: Int): Command = race(this, waitMs(ms.toDouble()))

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
    val windupTurret: Command = newCommand(
        { flywheel.run(targeter.flywheelSpeed) },
        {  },
        { false }
    )

    val shootTurret: Command = newCommand(
        {
            flywheel.run(targeter.flywheelSpeed)
            intakeMotor.power = 0.9
            feederMotor.power = 0.7
        },
        {
            flywheel.stop()
            intakeMotor.power = 0.0
            feederMotor.power = 0.0
        },
        { false }
    )
    val rehome: Command = Command.build()
        .setExecute { turret.rehome() }
        .setDone { !turret.isRehoming }

    val runIntake: Command = Command.build()
        .setExecute { intakeMotor.power = 1.0 }
        .setDone { false }
        .setEnd { intakeMotor.power = 0.0 }

    val shoot: Command = windupTurret.forTime(2000).then(
        shootTurret.forTime(3000))
    //endregion

    //region Helper Functions
    fun followSchedule(schedule: Command) {
        schedule(schedule)
    }

    fun goto(path: com.pedropathing.paths.Path): Command {
        return follow(follower, path)
    }

    fun newSchedule(vararg commands: Command): Command {
        return sequential(rehome, *commands)
    }

    fun newCommand(onStart: Runnable, onFinish: Runnable, endCondition: BooleanSupplier): Command {
        return Command.build()
            .setExecute { onStart.run() }
            .setDone { endCondition.asBoolean }
            .setEnd { onFinish.run() }
    }
    //endregion

    fun update() {
        follower.update()
        Scheduler.execute()
        turret.update()
        flywheel.update()
    }

}