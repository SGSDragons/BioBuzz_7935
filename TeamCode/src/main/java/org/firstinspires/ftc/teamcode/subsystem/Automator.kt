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
import org.firstinspires.ftc.teamcode.Telemetry
import org.firstinspires.ftc.teamcode.pedro.Constants
import kotlin.math.floor

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

    //endregion

    init {
        Scheduler.reset()
        activeAutomator = this
    }

    //region Commands
    fun scheduleWrapper(command: Command) {
        schedule(command)
    }

    val windupFlywheel: Command = newCommand(
        loop = { flywheel.run(targeter.flywheelSpeed) },
        endCondition = { flywheel.velocity in (targeter.flywheelSpeed - 10)..(targeter.flywheelSpeed + 75) }
    )

    @JvmField var feedPulseRate = 100;
    var startTime: Double = -1.0;

    fun shootBalls(balls: Int, timeout: Int): Command {
        var ballsShot = 0

        return newCommand(
            start = {
                feederMotor.power = 0.9

                // Pulse the intake to the feeder to give the flywheel time to catch up
                if (startTime == -1.0) {
                    startTime = System.currentTimeMillis().toDouble()
                }

                val difference = System.currentTimeMillis() - startTime
                if (floor(difference / feedPulseRate)%3 == 2.0) {
                    intakeMotor.power = 1.0
                } else {
                    intakeMotor.power = 0.0
                }
            },
            loop = {
                flywheel.run(targeter.flywheelSpeed)
                if (flywheel.wasBallShot()) {
                    ballsShot += 1
                }
            },
            end = {
                flywheel.stop()
                intakeMotor.power = 0.0
                feederMotor.power = 0.0
                startTime = -1.0
            },
            endCondition = {
                ballsShot >= balls
            }
        ).forTime(timeout)
    }

    val runIntake: Command = newCommand(
        start = { intakeMotor.power = 1.0 },
        end = { intakeMotor.power = 0.0 }
    )

    val shoot: Command = scheduledCommand(
        windupFlywheel,
        shootBalls(4, 4000)
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

    fun firstToFinishCommand(vararg commands: Command): Command {
        return race(*commands)
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