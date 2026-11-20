package org.firstinspires.ftc.teamcode.opmode.tests;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Telemetry;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystem.Targeter;
import org.firstinspires.ftc.teamcode.subsystem.Turret;

@TeleOp(name = "Turret Joystick", group = "Test")
public class TurretJoystick extends LinearOpMode {
    private static final double STICK_ENGAGE = 0.7;
    private static final double BODY_AIM_KP = 1.5; // TUNE

    @Override
    public void runOpMode() {
        Follower follower = Constants.create(hardwareMap);
        Targeter targeter = new Targeter(follower, true); // true = red side, false = blue
        Turret turret = new Turret(hardwareMap);
        turret.rehome();

        boolean lastNext = false;

        waitForStart();

        while (opModeIsActive()) {
            follower.update();

            // Turret: right stick aims, A = zero, B = rehome
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

            // X cycles targets, once per press
            boolean next = gamepad1.x;
            if (next && !lastNext) {
                targeter.nextTarget();
            }
            lastNext = next;

            // rive; hold left bumper to auto-face the target
            double turn = -gamepad1.right_stick_x * 0; // no stick turning here: right stick runs the turret
            if (gamepad1.left_bumper) {
                turn = targeter.getBodyHeadingError() * BODY_AIM_KP;
                turn = Math.max(-1, Math.min(1, turn));
            }
            follower.manual(ManualDrive.fieldCentric(-gamepad1.left_stick_y, -gamepad1.left_stick_x, turn, follower.pose().heading()));

            // Telemetry
            Telemetry.put("stick x", x);
            Telemetry.put("stick y", y);
            Telemetry.put("mag", mag);
            Telemetry.put("A", gamepad1.a);
            Telemetry.put("target", targeter.getCurrentTarget().name);
            Telemetry.put("body error", targeter.getBodyHeadingError());
            Telemetry.send();

            turret.update();
        }
    }
}
