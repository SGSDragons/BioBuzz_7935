package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Telemetry;

/**
 * A class to control a Continuous Rotation (CR) Servo like it's a motor
 */
public class ServoMotor {

    private final Servo servo;
    private final DcMotorEx servoPose;
    private double bias = 0;

    private final double proportional = -0.5;
    private final double feedForward = 0.5;

    public ServoMotor(Servo servo, DcMotorEx servoPos) {
        this.servo = servo;
        this.servoPose = servoPos;
    }

    public double getPosition() {
        return (servoPose.getCurrentPosition() - bias) / 8192;
    }

    public void rezero() {bias = servoPose.getCurrentPosition();}

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
        double error = position - getPosition();
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
        // Send a signal to drive the servo to the desired position
        // Send telemetry information (power and position)
        Telemetry.INSTANCE.put("servo/position", servoPose.getCurrentPosition() - bias);
    }
}