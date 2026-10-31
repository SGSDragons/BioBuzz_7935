package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.race
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedro.Constants

class Automator(val hardwareMap: HardwareMap, val start: Pose, val isRed: Boolean) {
    init {
        Scheduler.reset()
    }
    val follower: Follower = Constants.create(hardwareMap).also { it.setPose(start) }
    val targeter: Targeter = Targeter(follower, isRed)
    val flywheel: Flywheel = Flywheel(hardwareMap)
    val turret: Turret = Turret(hardwareMap).also { it.rehome() }
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder").also {
        it.direction = DcMotorSimple.Direction.REVERSE
    }
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")

    val windupTurret: Command = Command.build()
        .setExecute { flywheel.run(targeter.flywheelSpeed) }
        .setDone { false }

    val shootTurret: Command = Command.build()
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

    val rehome: Command = Command.build()
        .setExecute { turret.rehome() }
        .setDone { !turret.isRehoming }

    private val runIntake: Command = Command.build()
        .setExecute { intakeMotor.power = 1.0 }
        .setDone { false }
        .setEnd { intakeMotor.power = 0.0 }

    val intake: Command = race(runIntake, waitMs(10000.0))

    val shoot: Command = race(windupTurret, waitMs(2000.0)).then(
        race(shootTurret, waitMs(3000.0)))

    fun runSchedule(schedule: Command) {
        schedule(schedule)
    }

    fun goto(path: com.pedropathing.paths.Path): Command {
        return follow(follower, path)
    }

    fun newSchedule(vararg commands: Command): Command {
        return sequential(*commands)
    }

    fun Command.forMs(ms: Double) = race(this, waitMs(ms))

    fun update() {
        follower.update()
        Scheduler.execute()
        turret.update()
        flywheel.update()
    }

}