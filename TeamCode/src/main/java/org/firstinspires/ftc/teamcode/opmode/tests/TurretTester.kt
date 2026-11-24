package org.firstinspires.ftc.teamcode.opmode.tests

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.utils.Telemetry

@Config
object TurretTest {
    @JvmField var angle: Double = 0.0
}

@TeleOp(name = "Turret Tester", group = "Tests")
class TurretTester : LinearOpMode() {

    override fun runOpMode() {

        val turret = Turret(hardwareMap)

        waitForStart()

        while (opModeIsActive()) {
            turret.setAngle(TurretTest.angle)
            Telemetry.send()
        }
    }

}