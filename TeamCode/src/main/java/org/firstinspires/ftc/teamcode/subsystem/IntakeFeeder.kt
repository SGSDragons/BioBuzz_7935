package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.utils.Telemetry.put
import kotlin.math.floor

@Config
class IntakeFeeder(hardwareMap: HardwareMap) {

    private val intakeMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "intake")
    private val feederMotor: DcMotorEx = hardwareMap.get(DcMotorEx::class.java, "feeder")
    private var startTime = -1.0
    private var currentDipCount = 1

    init {
        intakeMotor.direction = DcMotorSimple.Direction.FORWARD
        feederMotor.direction = DcMotorSimple.Direction.REVERSE
    }

    fun setPowers(intake: Double, feeder: Double) {
        startTime = -1.0
        intakeMotor.power = intake
        feederMotor.power = feeder
    }

    fun feedShooter() {
        feederMotor.power = feederPower

        // Pulse the intake to the feeder to give the flywheel time to catch up
        if (startTime == -1.0) {
            startTime = System.currentTimeMillis().toDouble()
        }

        val difference = System.currentTimeMillis() - startTime
        if (floor(difference / pulseTime)%pulseRate == 6.0) {
            intakeMotor.power = intakePower
        } else {
            intakeMotor.power = 0.0
        }
    }

    fun update() {
        put("intake/current", intakeMotor.getCurrent(CurrentUnit.AMPS))
        put("feeder/current", feederMotor.getCurrent(CurrentUnit.AMPS))
    }

    @Config
    companion object {
        @JvmField var intakePower: Double = 1.0
        @JvmField var feederPower: Double = 1.0
        @JvmField var pulseTime = 50;
        @JvmField var pulseRate = 7.0;
        @JvmField var detectPollen = 2.0;
    }
}

