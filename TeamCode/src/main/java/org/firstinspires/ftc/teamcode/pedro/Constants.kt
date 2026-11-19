package org.firstinspires.ftc.teamcode.pedro

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.config.Configuration
import com.pedropathing.controllers.Controller
import com.pedropathing.follower.Follower
import com.pedropathing.math.Matrix
import com.pedropathing.math.Vector2D
import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.drivetrains.MecanumConfig
import com.pedropathing.revhub.localizers.PinpointConfig
import com.pedropathing.revhub.localizers.PinpointLocalizer
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

object Constants {

    private var follower: Follower? = null
    fun getOrCreate(h: HardwareMap): Follower {
        return follower ?: create(h)
    }

    @JvmStatic
    fun create(h: HardwareMap): Follower {
        return Follower(newLocalizer(h), newDrivetrain(h), newForesight()).also {
            follower = it
        }
    }

    var drivetrainConfig: MecanumConfig = MecanumConfig { c: MecanumConfig? ->
        c!!.frontLeftName.set("frontleft")
        c.frontRightName.set("frontright")
        c.backLeftName.set("backleft")
        c.backRightName.set("backright")
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD)
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE)
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD)
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE)
    }

    var localizerConfig: PinpointConfig = PinpointConfig { c: PinpointConfig? ->
        c!!.name.set("odo")
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
        c.xPodOffset.set(-3.1287101685531495)
        c.yPodOffset.set(-2.709128011868695)
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD)
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD)
        c.globalDistanceUnit.set(DistanceUnit.INCH)
        c.offsetUnits.set(DistanceUnit.INCH)
    }

    var foresightConfig: ForesightConfig = ForesightConfig(
        Configuration { c: ForesightConfig? ->
            val primaryTranslationalForward = Controller.proportional(0.4779313492769102)
            val secondaryTranslationalForward = Controller.proportional(0.17658286299696688)
            val primaryTranslationalLateral = Controller.proportional(0.8115369768393099)
            val secondaryTranslationalLateral = Controller.proportional(0.29984122827473164)

            c!!.forwardTranslational.set(
                Controller.piecewise(secondaryTranslationalForward)
                    .put(2.5, primaryTranslationalForward)
            )
            c.strafeTranslational.set(
                Controller.piecewise(secondaryTranslationalLateral)
                    .put(2.5, primaryTranslationalLateral)
            )

            c.coast.set(Controller.proportionalFeedforward(0.010710627108203731))
            c.brake.set(Controller.proportionalFeedforward(0.00910403304197317))

            c.headingFeedback.set(Controller.proportional(1.564840370351443))
            c.headingBrakeCoefficients.set(
                Vector2D.cartesian(
                    0.08061409865391288,
                    0.003593606680430538
                )
            )

            c.linearBrakeCoefficients.set(Matrix.diag(0.13874849728306451, 0.0854505962841659))
            c.quadraticBrakeCoefficients.set(
                Matrix.diag(
                    0.002146967388328984,
                    0.001993854137230208
                )
            )

            c.maxAchievableForwardVelocity.set(77.19701057620246)
            c.maxAchievableStrafeVelocity.set(61.95774734049519)
            c.naturalForwardDeceleration.set(36.716646571179425)
            c.naturalStrafeDeceleration.set(70.02150414448376)
        }
    )

    fun newDrivetrain(hardwaremap: HardwareMap) = Mecanum(hardwaremap, drivetrainConfig)
    fun newLocalizer(hardwaremap: HardwareMap) = PlottingLocalizer(PinpointLocalizer(hardwaremap, localizerConfig))
    fun newForesight() = Foresight(foresightConfig)
}