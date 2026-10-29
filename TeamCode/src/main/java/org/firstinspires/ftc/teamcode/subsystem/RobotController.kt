package org.firstinspires.ftc.teamcode.subsystem

//import com.bylazar.configurables.annotations.Configurable
//import com.bylazar.configurables.annotations.Configurable
//import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.Follower
import com.pedropathing.follower.ManualDrive
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedro.Constants
import kotlin.math.cos
import kotlin.math.sin

class RobotController(val driverGamepad: Gamepad, val operatorGamepad: Gamepad, val hardwareMap: HardwareMap, val isRed: Boolean) {
    val feederMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "feeder")
    val intakeMotor: DcMotor = hardwareMap.get(DcMotor::class.java, "intake")
    val leftShooterMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "leftshooter")
    val rightShooterMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "rightshooter")

//    val panelsTelemetry = PanelsTelemetry.ftcTelemetry
//    @Configurable
    companion object {
        @JvmField var kP: Double = -100.0;
        @JvmField var kI: Double = 0.0;
        @JvmField var kD: Double = 0.0;
        @JvmField var kF: Double = 0.0;
        @JvmField var targetShooterVelocity: Double = 500.0;
    }

    private val follower: Follower = Constants.create(hardwareMap)
    private val turret: Turret = Turret(hardwareMap)
    private val targeter: Targeter = Targeter(follower, isRed)

    private var turretToggle: Boolean = true

    fun setPose(pose: Pose) {
        follower.localizer.setPose(pose)
    }

    fun getPose(): Pose {
        return follower.pose()
    }

    fun runShooter() {
        leftShooterMotor.setVelocityPIDFCoefficients(kP,kI,kD,kF)
        rightShooterMotor.setVelocityPIDFCoefficients(kP,kI,kD,kF)

        leftShooterMotor.velocity = targetShooterVelocity
        rightShooterMotor.velocity = targetShooterVelocity
    }

    fun dontRunShooter() {
        leftShooterMotor.power = 0.0
        rightShooterMotor.power = 0.0
    }

    fun update() {
        feederMotor.power = operatorGamepad.right_stick_y.toDouble()
        intakeMotor.power = operatorGamepad.left_stick_y.toDouble()

        if (operatorGamepad.x) {
            targeter.setTarget(0)
        } else if (operatorGamepad.y) {
            targeter.setTarget(1)
        }

        if (operatorGamepad.a && !turret.is_rehoming) {
            turret.rehome()
        }

        if (operatorGamepad.b) {
            turretToggle = !turretToggle
        }

        if (operatorGamepad.right_bumper) {
            runShooter()
        } else {
            dontRunShooter()
        }

        val powers = DrivePowers(
            -driverGamepad.left_stick_y.toDouble(),
            -driverGamepad.left_stick_x.toDouble(),
            -driverGamepad.right_stick_x.toDouble());

        if (driverGamepad.y) {
            ManualDrive.driveOrHold(
                follower,
                powers
            )
        } else {
            ManualDrive.fieldCentric(
                powers,
                getPose().heading()
            )
        }

        if (turretToggle) {
            turret.setTargetAngle(targeter.getAimValue() * 360.0)
        } else {
            turret.setTargetAngle(turret.turretRotations + operatorGamepad.left_trigger / 90 - operatorGamepad.right_trigger / 90)
        }
        turret.update()
        follower.update()

//        panelsTelemetry.addData("Shooter Speed", (leftShooterMotor.velocity + rightShooterMotor.velocity)/2)
//        panelsTelemetry.addData("Target Shooter Speed", targetShooterVelocity)
//        panelsTelemetry.update()
    }
}