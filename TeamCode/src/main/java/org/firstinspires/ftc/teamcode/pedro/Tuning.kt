package org.firstinspires.ftc.teamcode.pedro

import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.localizers.PinpointLocalizer
import com.pedropathing.tuning.autotune.Procedure
import com.pedropathing.tuning.autotune.Tuner
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests
import java.util.function.Function


object Tuning {
    @Tuner @JvmStatic
    fun mecanumTuner(): Procedure {
        return MecanumTuner()
    }

    @Tuner @JvmStatic
    fun pinpointTuner(): Procedure {
        return PinpointTuner()
    }

    @Tuner @JvmStatic
    fun foresightTuner(): Procedure {
        return ForesightTuner(Constants::newLocalizer, Constants::newDrivetrain)
    }

    @Tuner @JvmStatic
    fun tests(): Procedure {
        return Tests(Constants::newDrivetrain, Constants::newLocalizer, Constants::newForesight)
    }
}
