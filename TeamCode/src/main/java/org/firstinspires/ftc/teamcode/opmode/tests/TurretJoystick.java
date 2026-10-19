package org.firstinspires.ftc.teamcode.opmode.tests;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystem.ServoMotor;
import org.firstinspires.ftc.teamcode.subsystem.Turret;

@TeleOp(name = "Turret Joystick", group = "Test")
public class TurretJoystick extends LinearOpMode {
    static final double STICK_ENGAGE = 0.7;

    @Override
    public void runOpMode() {
        Turret turret = new Turret(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            double x = -gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            double mag = Math.hypot(x, y);

            if (gamepad1.a) {
                turret.setTargetAngle(0.0);
            } else if (gamepad1.b) {
                turret.rehome();
            } else if (mag > STICK_ENGAGE) {
                double targetAngle = Math.toDegrees(Math.atan2(x, y));
                turret.setTargetAngle(targetAngle);
            }

            turret.update();
            PanelsTelemetry.INSTANCE.getTelemetry().update();

        }
    }
}