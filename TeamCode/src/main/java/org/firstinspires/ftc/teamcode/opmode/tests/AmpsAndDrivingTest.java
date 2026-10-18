package org.firstinspires.ftc.teamcode.opmode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name="Amps and Driving Test", group="Test")

//this code might have some bugs. I have not tested it, but it builds.
public class AmpsAndDrivingTest extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private Follower follower;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        follower = Constants.create(hardwareMap);

        while (opModeIsActive()) {

            telemetry.addData("FrontLeftMotorAmprage", MotorUtils.GetCurent(1));
            telemetry.addData("FrontRightMotorAmprage", MotorUtils.GetCurent(2));
            telemetry.addData("BackLeftMotorAmprage", MotorUtils.GetCurent(3));
            telemetry.addData("BackRightMotorAmprage", MotorUtils.GetCurent(4));

            follower.manual(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
            follower.update();
        }
    }
}
