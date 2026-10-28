package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Telemetry;

/**
 * A turret driven by a ServoMotor through a gear. You give it a target angle
 * in degrees; it picks the shortest route and tells the servo where to go.
 * Angle 0 is wherever the servo was when the Turret was created.
 */
public class Turret {

    private final String servoPwmName = "360servo";
    private final String encoderName = "turretencoder";
    private final String magswitchName = "turretmagnet";

    // Set the gear ratio using constants
    private final int servoTeeth = 12;
    private final int turretTeeth = 30;

    // Watch the negations:
    // With two gears, increasing turret rotations means decreasing servo.
    // With three, they will not be inverted.
    private final double turretRevsPerServoRev = -(double)servoTeeth / turretTeeth;
    private final DigitalChannel magSwitch;

    private final ServoMotor servo;
    private boolean rehoming = true;
    private double target = 0.0;

    @Config
    public static class TurretBias {
        public static double bias = 0.338;
    }

    public Turret(HardwareMap hardwareMap) {
        this.servo = new ServoMotor(
            hardwareMap.get(Servo.class, servoPwmName),
            hardwareMap.get(DcMotorEx.class, encoderName));
        servo.setPower(0.0);

        this.magSwitch = hardwareMap.get(DigitalChannel.class, magswitchName);
        this.magSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean is_rehoming() {return rehoming;}

    public void rehome() {
        this.rehoming = true;
    }

    /** Point the turret at this angle (any value; it gets wrapped to 0-360). */
    public void setTargetAngle(double degrees) {
        if (rehoming) {
            return;
        }

        double targetRotations = (degrees / 360.0) + TurretBias.bias;
        double servoRotations = servo.getPosition();
        double turretRotations = servoRotations * turretRevsPerServoRev;

        double turretWholeRotations = Math.floor(turretRotations);

        target = turretWholeRotations + targetRotations;
        if (target - turretRotations > 0.5) {
            target -= 1.0;
        } else if (turretRotations - target > 0.5) {
            target += 1.0;
        }
    }

    public double getTurretRotations() {
        double absPos = getAbsolutePosition();
        return absPos - Math.floor(absPos);
    }

    /** Call once every loop. */
    public void update() {
        servo.update();
        if (!rehoming) {
            servo.gotoPosition(target/turretRevsPerServoRev);
        } else if (this.magSwitch.getState()) {
            servo.setPower(0.2);
        } else {
            servo.setPower(0.0);
            servo.rezero();
            rehoming = false;
            setTargetAngle(-TurretBias.bias);
        }

        Telemetry.INSTANCE.put("turret/target", target);
        Telemetry.INSTANCE.put("turret/rotations", getTurretRotations());
        Telemetry.INSTANCE.put("turret/rehoming", rehoming);
        Telemetry.INSTANCE.put("turret/magswitch", magSwitch.getState());

        FieldDrawing.INSTANCE.updateTurretRotations(getTurretRotations());
    }

    private double getAbsolutePosition() {
        double servoRotations = servo.getPosition();
        return servoRotations * turretRevsPerServoRev;
    }
}
