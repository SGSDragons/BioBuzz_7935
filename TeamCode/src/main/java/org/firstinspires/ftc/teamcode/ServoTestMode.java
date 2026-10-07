package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.ServoMotor;

@TeleOp(name="Chase Servo Test", group="Tests")
public class ServoTestMode extends LinearOpMode {

    @Override
    public void runOpMode() {

        TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();

        ServoMotor motor = new ServoMotor(
                hardwareMap.get(Servo.class, "360servo"),
                hardwareMap.get(AnalogInput.class, "servopose")
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

            panels.addData("opmode target", targetPosition);

            motor.update();
            panels.update();
        }

    }
}
