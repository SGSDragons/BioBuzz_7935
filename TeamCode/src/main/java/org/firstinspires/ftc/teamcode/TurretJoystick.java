/*package org.firstinspires.ftc.teamcode;

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

    public static double targetAngle = 0;

    @Override
    public void runOpMode() {
        ServoMotor servo = new ServoMotor(
                hardwareMap.get(Servo.class, "360servo"),
                hardwareMap.get(AnalogInput.class, "servopose"));
        Turret turret = new Turret(servo, 12, 30); // your real tooth counts

        boolean steering = false;
        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;
            double y = gamepad1.right_stick_y;
            double mag = Math.hypot(x, y);

//            if (!steering && mag > STICK_ENGAGE) steering = true;
//            else if (steering && mag < STICK_RELEASE) steering = false;

            targetAngle = Math.toDegrees(Math.atan2(x, y));

            if (mag > STICK_ENGAGE) {
                turret.setTargetAngle(targetAngle);
            }

            //turret.update();
            telemetry.addData("Target", turret.getTargetAngleDegrees());
            telemetry.addData("Angle", turret.getAngleDegrees());
            telemetry.update();
        }
    }
}*/

//this code does not work. get another version, or wait untill this version is not commented out, or fix it yourself. -eli