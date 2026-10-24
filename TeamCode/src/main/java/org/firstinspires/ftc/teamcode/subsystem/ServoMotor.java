package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Telemetry;

/**
 * A class to control a Continuous Rotation (CR) Servo like it's a motor
 */
public class ServoMotor {

    private final Servo servo;
    private final AnalogInput servoPos;

    // Fields to track and drive servo behavior.
    private double lastPosition;
    private double absolutePosition;

    private final double proportional = 0.5;
    private final double feedForward = 0.5;

    public ServoMotor(Servo servo, AnalogInput servoPos) {
        this.servo = servo;
        this.servoPos = servoPos;
        absolutePosition = currentRotation();
        lastPosition = absolutePosition;
    }

    public double getPosition() {
        return absolutePosition;
    }

    public void rezero() {
        absolutePosition = 0.0;
    }

    public void setPower(double power) {
        if (power < -1.0) {
            power = -1.0;
        }
        if (power > 1.0) {
            power = 1.0;
        }

        // Map [-1.0, 1.0] on [0.0, 1.0]
        power = (power + 1)/2.0;
        servo.setPosition(power);

        Telemetry.INSTANCE.put("servo/power", power);
    }

    // Drives the servo in the direction of position.
    public void gotoPosition(double position) {
        double error = position - absolutePosition;
        if (Math.abs(error) < 0.03) {
            setPower(0.0);
        } else {
            double power = proportional * error;
            // Apply feed-forward in the correct direction.
            power += feedForward * (error < 0 ? -1.0 : 1.0);
            setPower(power);
        }
        Telemetry.INSTANCE.put("servo/target", position);
    }

    // Must be called once every cycle to update position tracking
    public void update() {
        double current = currentRotation();

        absolutePosition = absolutePosition + (current - lastPosition);
        if(current - lastPosition > 0.4) {
            absolutePosition = absolutePosition - 1.0;
        } else if (current - lastPosition < -0.4) {
            absolutePosition = absolutePosition+ 1.0;
        }
        lastPosition = current;

        // Send a signal to drive the servo to the desired position
        // Send telemetry information (power and position)
        Telemetry.INSTANCE.put("servo/position", absolutePosition);
    }

    private double currentRotation() {
        // An increasing voltage implies clockwise rotation of the servo.
        // Rotations should increase in counter-clockwise. Hence the negation.
        return -servoPos.getVoltage() / servoPos.getMaxVoltage();
    }
}