package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Telemetry;

@Config
public class Flywheel {
    public static double kF;
    public static double kP;
    public static double kI;
    public static double kD;

    private final DcMotorEx leftMotor;
    private final DcMotorEx rightMotor;
    private double targetSpeed = 0.0;

    public Flywheel(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.get(DcMotorEx.class, "leftshooter");
        rightMotor = hardwareMap.get(DcMotorEx.class, "rightshooter");
    }

    public void reconfigure() {
        leftMotor.setVelocityPIDFCoefficients(kP,kI,kD,kF);
        rightMotor.setVelocityPIDFCoefficients(kP,kI,kD,kF);
    }

    public void run(double speed) {
        targetSpeed = speed;
        leftMotor.setVelocity(speed);
        rightMotor.setVelocity(speed);
    }

    public void stop() {
        targetSpeed = 0.0;
        leftMotor.setPower(0.0);
        rightMotor.setPower(0.0);
    }

    public void update() {
        Telemetry.put("flywheel/target", targetSpeed);
        Telemetry.put("flywheel/left/position", leftMotor.getCurrentPosition());
        Telemetry.put("flywheel/left/velocity", leftMotor.getVelocity());
    }
}
