package org.firstinspires.ftc.teamcode

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.follower.Follower
import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.drivetrains.MecanumConfig
import com.pedropathing.revhub.localizers.PinpointConfig
import com.pedropathing.revhub.localizers.PinpointLocalizer
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

object Constants {

    object DriveMotors {
        data class Config(val name: String, val dir: DcMotorSimple.Direction) {
            fun apply(hw: HardwareMap): DcMotor {
                return hw.get(DcMotor::class.java, name).also { it.direction = dir }
            }
        }

        val backLeft = Config("port3", DcMotorSimple.Direction.REVERSE)
        val frontLeft = Config("port2", DcMotorSimple.Direction.REVERSE)
        val backRight = Config("port0", DcMotorSimple.Direction.FORWARD)
        val frontRight = Config("port1", DcMotorSimple.Direction.FORWARD)
    }

    val pinpoint = PinpointConfig { c ->

        // The forward encoder measures forward motion (+x direction). We must
        // no how far it is left or right (y direction) from the center of the
        // robot so the pinpoint can account for swing motion when the bot
        // turns
        // x-motion when turning = y-distance * radians without moving forward
        c.xPodOffset.set(-4.5)
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED)

        // Same principal as above, but for the encoder that measures left
        // motion (+y direction). So it must know its position in front of or
        // behind the center of the bot (x direction).
        c.yPodOffset.set(-4.25)
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED)

        // The unit for the numbers above and for position on the field.
        c.globalDistanceUnit.set(DistanceUnit.INCH)

        // The i2c port name that the Pinpoint's plugged into
        c.name.set("odo")

        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
    }

    val pedroDriveTrain: MecanumConfig = MecanumConfig { c ->
        c.backLeftName.set(DriveMotors.backLeft.name)
        c.backLeftDirection.set(DriveMotors.backLeft.dir)

        c.frontLeftName.set(DriveMotors.frontLeft.name)
        c.frontLeftDirection.set(DriveMotors.frontLeft.dir)

        c.backRightName.set(DriveMotors.backRight.name)
        c.backRightDirection.set(DriveMotors.backRight.dir)

        c.frontRightName.set(DriveMotors.frontRight.name)
        c.frontRightDirection.set(DriveMotors.frontRight.dir)
    }

    val foresightConfig = ForesightConfig { c ->
        // TODO: Tune the Pedro following algorithm
    }

    fun newFollower(hardwareMap: HardwareMap) = Follower(
        PinpointLocalizer(hardwareMap, pinpoint),
        Mecanum(hardwareMap, pedroDriveTrain),
        Foresight(foresightConfig),
    )
}