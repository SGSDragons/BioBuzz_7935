package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * A class to control a Continuous Rotation (CR) Servo like it's a motor
 */
public class ServoMotor {

    TelemetryManager panels = PanelsTelemetry.INSTANCE.getTelemetry();

    private final Servo servo;
    private final AnalogInput servoPos;

    // Fields to track and drive servo behavior.
    private double lastPosition;
    private double absolutePosition;
    private double targetPosition;
    private double currentDirection;

    public ServoMotor(Servo servo, AnalogInput servoPos) {
        this.servo = servo;
        this.servoPos = servoPos;
        absolutePosition = currentRotationPercent();
        lastPosition = absolutePosition;
    }

    // Drives the servo to some target in absolute terms. If the servo's is
    // idle and the current position is 0, then setPosition(2.0) should
    // cause it to make 2 full rotations. Then setPosition(1.5) should cause
    // it to make a half rotation in the opposite direction.
    public void setPosition(double target) {
        targetPosition = target;
    }

    public double getPosition() {
        return absolutePosition;
    }

    // Must be called once every cycle
    public void update() {

        // Update the current position
        double current = currentRotationPercent();

        absolutePosition = absolutePosition + (current - lastPosition);
        if(current - lastPosition > 0.4) {
            absolutePosition = absolutePosition - 1.0;
        } else if (current - lastPosition < -0.4) {
            absolutePosition = absolutePosition+ 1.0;
        }

        double error = targetPosition - absolutePosition;
        double power = 0.5 + error * -1.0;
        if (power < 0) {
            power = 0;
        }
        if (power > 1)
            power = 1;
        servo.setPosition(power);
        panels.addData("servotarget", targetPosition);
        panels.addData("servopower", power);

        // Send a signal to drive the servo to the desired position
        // Send telemetry information (power and position)
        panels.addData("servopos", absolutePosition);


        lastPosition = current;
    }



    // Returns a value between 0 and 1 indicating the instantaneous position
    // of the servo. This is not like ticks on a normal motor's encoder because
    // if the value is climbing, then when it passes 1.0, it'll wrap back to 0.0.
    private double currentRotationPercent() {
        return servoPos.getVoltage() / servoPos.getMaxVoltage();
    }
}