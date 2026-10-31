package org.firstinspires.ftc.teamcode.opmode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Telemetry;

@TeleOp(name="robot test", group="Test")
@Config
public class RobotTest extends LinearOpMode {
    public static double kp = -50.0;
    public static double ki = 0.0;
    public static double kd = 0.0;
    public static double kf = 0.0;
    public static double targetSpeed = 700.0;
    public static double intakeSpeed = 0.9;
    public static double feedSpeed = 0.9;

    DcMotorEx leftShooter;
    DcMotorEx rightShooter;
    DcMotorEx intake;
    DcMotorEx feeder;

    DcMotorEx frontleft;
    DcMotorEx frontright;
    DcMotorEx backleft;
    DcMotorEx backright;
    IMU imu;

    @Override
    public void runOpMode() {

        waitForStart();

        leftShooter = hardwareMap.get(DcMotorEx.class, "leftshooter");
        leftShooter.setDirection(DcMotorEx.Direction.REVERSE);
        rightShooter.setDirection(DcMotorEx.Direction.FORWARD);

        rightShooter = hardwareMap.get(DcMotorEx.class, "rightshooter");
        rightShooter.setDirection(DcMotorEx.Direction.FORWARD);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setDirection(DcMotorEx.Direction.FORWARD);

        feeder = hardwareMap.get(DcMotorEx.class, "feeder");
        feeder.setDirection(DcMotorEx.Direction.REVERSE);

        frontleft = hardwareMap.get(DcMotorEx.class, "frontleft");
        frontright = hardwareMap.get(DcMotorEx.class, "frontright");
        backleft = hardwareMap.get(DcMotorEx.class, "backleft");
        backright = hardwareMap.get(DcMotorEx.class, "backright");
        imu = hardwareMap.get(IMU.class,"imu");

        frontleft.setDirection(DcMotorEx.Direction.FORWARD);
        frontright.setDirection(DcMotorEx.Direction.REVERSE);
        backleft.setDirection(DcMotorEx.Direction.FORWARD);
        backright.setDirection(DcMotorEx.Direction.REVERSE);

        imu.resetYaw();

        waitForStart();

        int count = 0;

        while (opModeIsActive()) {

            double driveInput = -gamepad1.left_stick_y;
            double strafeInput = gamepad1.left_stick_x;
            double turn = Math.pow(gamepad1.right_stick_x,3);

            double currentheading = -imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) * Math.PI / 180;
            double drive = driveInput * Math.cos(currentheading) + strafeInput * Math.sin(currentheading);
            double strafe = strafeInput * Math.cos(currentheading) - driveInput * Math.sin(currentheading);

            double frontLeftPower = drive + strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backLeftPower = drive - strafe + turn;
            double backRightPower = drive + strafe - turn;

            double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            frontleft.setPower(frontLeftPower);
            frontright.setPower(frontRightPower);
            backleft.setPower(backLeftPower);
            backright.setPower(backRightPower);

            if (gamepad1.dpad_up) {
                imu.resetYaw();
            }

            leftShooter.setVelocityPIDFCoefficients(kp,ki,kd,kf);
            rightShooter.setVelocityPIDFCoefficients(kp,ki,kd,kf);

            if (gamepad2.right_bumper) {
                leftShooter.setVelocity(targetSpeed);
                rightShooter.setVelocity(targetSpeed);
            } else {
                leftShooter.setPower(0.0);
                rightShooter.setPower(0.0);
            }

            if (gamepad2.a) {
                intake.setPower(intakeSpeed);
                feeder.setPower(feedSpeed);
            } else {
                intake.setPower(-gamepad2.left_stick_y);
                feeder.setPower(Math.pow(-gamepad2.right_stick_y,3));
            }

            if (gamepad2.dpad_right) {
                kp++;
            } if (gamepad2.dpad_left) {
                kp--;
            } if (gamepad2.dpad_up) {
                targetSpeed++;
            } if (gamepad2.dpad_down) {
                targetSpeed--;
            }

            Telemetry.put("Target speed", targetSpeed);
            Telemetry.put("kp", kp);
            Telemetry.put("Current speed", (rightShooter.getVelocity()+leftShooter.getVelocity())/2);
            Telemetry.put("Count", count++);
            Telemetry.send();
        }
    }
}
