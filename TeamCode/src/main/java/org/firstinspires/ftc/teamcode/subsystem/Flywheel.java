package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Telemetry;

@Config
public class Flywheel {
    public static double kF = -20.0;
    public static double kP = -70.0;
    public static double kI;
    public static double kD;

    public static boolean LEFT_FORWARD = true;
    public static boolean RIGHT_FORWARD = false;

    private final DcMotorEx leftMotor;
    private final DcMotorEx rightMotor;
    private double targetSpeed = 0.0;

    public Flywheel(HardwareMap hardwareMap) {
        leftMotor = hardwareMap.get(DcMotorEx.class, "leftshooter");
        rightMotor = hardwareMap.get(DcMotorEx.class, "rightshooter");

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setDirection(LEFT_FORWARD ?
                DcMotorEx.Direction.FORWARD :
                DcMotorEx.Direction.REVERSE);
        rightMotor.setDirection(RIGHT_FORWARD ?
                DcMotorEx.Direction.FORWARD :
                DcMotorEx.Direction.REVERSE);
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
        reconfigure();
        Telemetry.put("flywheel/target", targetSpeed);

        Telemetry.put("flywheel/left/position", leftMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER).p);
        Telemetry.put("flywheel/left/position", leftMotor.getCurrentPosition());
        Telemetry.put("flywheel/left/velocity", leftMotor.getVelocity());
        Telemetry.put("flywheel/right/position", rightMotor.getCurrentPosition());
        Telemetry.put("flywheel/right/velocity", rightMotor.getVelocity());
    }
}
