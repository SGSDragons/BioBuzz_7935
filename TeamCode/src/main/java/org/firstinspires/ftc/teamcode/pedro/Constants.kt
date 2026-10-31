package org.firstinspires.ftc.teamcode.pedro

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
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

    val foresightConfig = ForesightConfig{ c: ForesightConfig? ->
            val primaryTranslationalForward = Controller.proportional(0.5250084248240626)
            val secondaryTranslationalForward = Controller.proportional(0.19397658448901356)
            val primaryTranslationalLateral = Controller.proportional(1.1223768223042243)
            val secondaryTranslationalLateral = Controller.proportional(0.4146882453803767)

        c!!.forwardTranslational.set(
            Controller.piecewise(secondaryTranslationalForward)
                .put(2.5, primaryTranslationalForward)
        )
        c.strafeTranslational.set(
            Controller.piecewise(secondaryTranslationalLateral)
                .put(2.5, primaryTranslationalLateral)
        )

        c.coast.set(Controller.proportionalFeedforward(0.02028701283076027))
        c.brake.set(Controller.proportionalFeedforward(0.01724396090614623))

        c.headingFeedback.set(Controller.proportional(2.6204420331818055))
        c.headingBrakeCoefficients.set(
            Vector2D.cartesian(
                0.032507246489180255,
                0.014917238139437527
            )
        )

        c.linearBrakeCoefficients.set(Matrix.diag(0.03978624919415515, 0.012549843364495822))
        c.quadraticBrakeCoefficients.set(
            Matrix.diag(
                0.0030391409197645506,
                0.004028811023955176
            )
        )

        c.maxAchievableForwardVelocity.set(51.526981803468075)
        c.maxAchievableStrafeVelocity.set(46.38879405290293)
        c.naturalForwardDeceleration.set(50.32116921646626)
        c.naturalStrafeDeceleration.set(65.69426386210297)
    }

    fun newDrivetrain(hardwaremap: HardwareMap) = Mecanum(hardwaremap, drivetrainConfig)
    fun newLocalizer(hardwaremap: HardwareMap) = PlottingLocalizer(PinpointLocalizer(hardwaremap, localizerConfig))
    fun newForesight() = Foresight(foresightConfig)
}