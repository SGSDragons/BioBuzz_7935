package org.firstinspires.ftc.teamcode.opmode.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="driving test", group="Test")
@Config
public class TestDriving extends LinearOpMode{

    DcMotorEx frontleft;
    DcMotorEx frontright;
    DcMotorEx backleft;
    DcMotorEx backright;
    IMU imu;

    @Override
    public void runOpMode() {

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

        while (opModeIsActive()) {

            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = Math.pow(gamepad1.right_stick_x,3);

//            double currentheading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) * Math.PI / 180;
//            double drive = driveInput * Math.cos(-currentheading) + strafeInput * Math.sin(-currentheading);
//            double strafe = -driveInput * Math.sin(-currentheading) + strafeInput * Math.cos(-currentheading);

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

            telemetry.update();
        }
    }
}
