package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Telemetry;

import java.util.function.Supplier;

/**
 * A turret driven by a ServoMotor through a gear. You give it a target angle
 * in degrees; it picks the shortest route and tells the servo where to go.
 * Angle 0 is wherever the servo was when the Turret was created.
 */
@Config
public class Turret {
    public static double bias = 0;
    public static boolean verbose = false;

    private final String servoPwmName = "360servo";
    private final String encoderName = "feeder";
    private final String magswitchName = "turretmagnet";

    private final double gear2Teeth = 60;
    private final double turretTeeth = 138;

    // Watch the negations:
    // With two gears, increasing turret rotations means decreasing servo.
    // With three, they will not be inverted.
    private final double turretRevsPerServoRev = -gear2Teeth / turretTeeth;
    private final DigitalChannel magSwitch;

    private final ServoMotor servo;
    private boolean rehoming = true;
    private double target = 0.0;
    private double lastTarget = Double.MAX_VALUE;


    public Turret(HardwareMap hardwareMap) {
        this.servo = new ServoMotor(
            hardwareMap.get(Servo.class, servoPwmName),
            hardwareMap.get(DcMotorEx.class, encoderName));
        servo.setPower(0.0);

        this.magSwitch = hardwareMap.get(DigitalChannel.class, magswitchName);
        this.magSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    public void rehome() {
        this.rehoming = true;
    }
    public boolean isRehoming() {
        return this.rehoming;
    }

    /** Point the turret at this angle (any value; it gets wrapped to 0-360). */
    public void setTargetAngle(double degrees) {
        if (rehoming) {
            return;
        }


        // Replace the direction the ball should go with where
        // the hood should be.
        degrees = -degrees;

        if (Math.abs(lastTarget - degrees) < 1) {
            // Avoid all the sensor reads and changes if there's no
            // real change.
            return;
        }

        double targetRotations = (degrees / 360.0) + bias;
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
// get absolute Position for turret/target

    public double getTurretTarget(){
        double absPos = getAbsolutePosition();
        return absPos - Math.floor(absPos);

    }
    /** Call once every loop. */
    public void update() {
        servo.update();
        if (!rehoming) {
            servo.gotoPosition(target/turretRevsPerServoRev);
        } else if (this.magSwitch.getState()) {
            servo.setPower(0.4*Math.signum(turretRevsPerServoRev));
        } else {
            servo.setPower(0.0);
            servo.rezero();
            rehoming = false;
            setTargetAngle(0.0);
        }

        if (verbose) {
            log("target", target - Math.floor(target));
            log("rotations", getTurretRotations());
            log("rehoming", rehoming);
            log("magswitch", magSwitch.getState());
        }

        FieldDrawing.INSTANCE.updateTurretRotations(getTurretRotations());
    }

    private double getAbsolutePosition() {
        double servoRotations = servo.getPosition();
        return servoRotations * turretRevsPerServoRev;
    }

    private void log(String name, Object value) {
        if (!verbose) return;
        Telemetry.put("turret/" + name, value);
    }
}
