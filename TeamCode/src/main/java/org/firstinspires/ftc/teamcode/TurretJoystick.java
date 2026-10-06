package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystem.ServoMotor;
import org.firstinspires.ftc.teamcode.subsystem.Turret;

@TeleOp(name = "Turret Joystick")
public class TurretJoystick extends LinearOpMode {
    static final double STICK_ENGAGE = 0.7;
    static final double STICK_RELEASE = 0.4;

    @Override
    public void runOpMode() {
        ServoMotor servo = new ServoMotor(
                hardwareMap.get(Servo.class, "servo"),
                hardwareMap.get(AnalogInput.class, "servoEncoder"));
        Turret turret = new Turret(servo, 6, 14); // your real tooth counts

        boolean steering = false;
        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            double mag = Math.hypot(x, y);

            if (!steering && mag > STICK_ENGAGE) steering = true;
            else if (steering && mag < STICK_RELEASE) steering = false;

            if (steering) {
                turret.setTargetAngle(Math.toDegrees(Math.atan2(x, y)));
            }

            turret.update();
            telemetry.addData("Target", turret.getTargetAngleDegrees());
            telemetry.addData("Angle", turret.getAngleDegrees());
            telemetry.update();
        }
    }
}