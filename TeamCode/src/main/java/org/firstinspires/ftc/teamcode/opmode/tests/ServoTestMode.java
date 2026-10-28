package org.firstinspires.ftc.teamcode.opmode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystem.ServoMotor;

@TeleOp(name="Chase Servo Test", group="Test")
public class ServoTestMode extends LinearOpMode {

    @Override
    public void runOpMode() {

        ServoMotor motor = new ServoMotor(
                hardwareMap.get(Servo.class, "360servo"),
                hardwareMap.get(DcMotorEx.class, "encoder")
        );

        waitForStart();
        double targetPosition = 1.0;

        while (opModeIsActive()) {

            if (gamepad1.aWasPressed()) {
                targetPosition += 1.5;
            }
            if (gamepad1.bWasPressed()) {
                targetPosition -= 1.0/3.0;
            }
            if (gamepad1.yWasPressed()) {
                motor.setPower(0.2);
            } else {
                motor.gotoPosition(targetPosition);
            }


            motor.update();
        }
    }
}
