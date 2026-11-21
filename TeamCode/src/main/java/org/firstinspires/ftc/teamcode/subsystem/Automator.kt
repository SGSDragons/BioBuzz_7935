package org.firstinspires.ftc.teamcode.subsystem

import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.parallel
import com.pedropathing.ivy.groups.Groups.race
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.utils.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants

fun Command.forTime(ms: Int): Command = race(this, waitMs(ms.toDouble()))
private var activeAutomator: Automator? = null
fun Command.run() {
    if (activeAutomator == null) {
        Telemetry.put("automator/nullError", "Attempt to run .run() without automator initialized!")
        return
    }
    activeAutomator!!.scheduleWrapper(this)
}

class Automator(val hardwareMap: HardwareMap, val start: Pose, val isRed: Boolean) {

    //region Class Definitions
    val follower: Follower = Constants.create(hardwareMap).also { it.setPose(start) }
    val targeter: Targeter = Targeter(follower, isRed)
    val flywheel: Flywheel = Flywheel(hardwareMap)
    val turret: Turret = Turret(hardwareMap)
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder").also { it.direction = DcMotorSimple.Direction.REVERSE }
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")
    val ramp: Ramp = Ramp(hardwareMap)

    //endregion

    init {
        Scheduler.reset()
        activeAutomator = this
        turret.setAngle(targeter.aimValue)
    }

    //region Commands
    fun scheduleWrapper(command: Command) {
        schedule(command)
    }

    val windupFlywheel: Command = newCommand(
        loop = { flywheel.run(targeter.flywheelSpeed) },
        endCondition = {
            val flywheelSpeed: Double = targeter.flywheelSpeed
            flywheel.velocity in (flywheelSpeed - 10)..(flywheelSpeed + 25)
        }
    )

    val feedPulseRate = 100;
    fun runFlywheel(): Command {
        var startTime = -1L

        return newCommand(
            start = {
                startTime = System.currentTimeMillis()
                feederMotor.power = 0.9
            },
            loop = {
                flywheel.run(targeter.flywheelSpeed)

                // Pulse the intake 1/3 of the time to give the flywheel time to recover
                val elapsed = System.currentTimeMillis() - startTime
                intakeMotor.power = if ((elapsed / feedPulseRate) % 3 == 2L) 1.0 else 0.0
            },
            end = {
                flywheel.stop()
                intakeMotor.power = 0.0
                feederMotor.power = 0.0
            },
            endCondition = { false }
        )
    }

    val startIntake: Command = instantCommand { intakeMotor.power = 1.0 }

    val stopIntake: Command = instantCommand { intakeMotor.power = 0.0 }

    val lowerRamp: Command = instantCommand { ramp.lower() }

    val raiseRamp: Command = instantCommand { ramp.raise() }

    val shoot = scheduledCommand(
            windupFlywheel,
            runFlywheel().forTime(2000)
        )
    //endregion

    //region Helper Functions
    fun goto(path: com.pedropathing.paths.Path): Command {
        return follow(follower, path)
    }

    fun scheduledCommand(vararg commands: Command): Command {
        return sequential(*commands)
    }

    fun concurrentCommand(vararg commands: Command): Command {
        return parallel(*commands)
    }

    fun instantCommand(op: () -> Unit): Command {
        return Command.build()
            .setStart { op }
            .setDone { true }
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

    fun update() {
        follower.update()
        Scheduler.execute()
        turret.setAngle(targeter.aimValue)
        flywheel.update()
    }
    //endregion

}