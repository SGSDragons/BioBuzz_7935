package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystem.ServoMotor;

@TeleOp(name="Servo Test", group="Tests")
public class ServoTestMode extends LinearOpMode {

    @Override
    public void runOpMode() {

        ServoMotor motor = new ServoMotor(
                hardwareMap.get(Servo.class, ""),
                hardwareMap.get(AnalogInput.class, "")
        );

        waitForStart();

        double targetPosition = 0.0;

        while (opModeIsActive()) {

            if (gamepad1.aWasPressed()) {
                targetPosition += 1.5;
            }
            if (gamepad1.bWasPressed()) {
                targetPosition -= 1.0/3.0;
            }
            motor.setPosition(targetPosition);

            motor.update();
        }

    }
}
