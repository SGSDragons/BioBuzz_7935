package org.firstinspires.ftc.teamcode.subsystem;

/**
 * A turret driven by a ServoMotor through a gear. You give it a target angle
 * in degrees; it picks the shortest route and tells the servo where to go.
 * Angle 0 is wherever the servo was when the Turret was created.
 */
public class Turret {

    private final ServoMotor servo;

    // How many turret rotations you get per servo rotation.
    // Servo gear has 20 teeth, turret has 100 -> 0.2
    private final double turretRevsPerServoRev;

    private double targetDeg;

    public Turret(ServoMotor servo, int servoTeeth, int turretTeeth) {
        this.servo = servo;
        this.turretRevsPerServoRev = (double) servoTeeth / turretTeeth;

        // ServoMotor starts with its target at 0, which could make it move on
        // startup. Tell it to hold where it is instead.
        servo.setPosition(servo.getPosition());
        targetDeg = getAngleDegrees();
    }

    /** Current turret angle, 0 to 360. */
    public double getAngleDegrees() {
        return normalize360(servo.getPosition() * turretRevsPerServoRev * 360.0);
    }

    public double getTargetAngleDegrees() {
        return targetDeg;
    }

    /** Point the turret at this angle (any value; it gets wrapped to 0-360). */
    public void setTargetAngle(double degrees) {

        double targetRotations = degrees / 360.0;
        double servoRotations = servo.getPosition();
        double turretRotations = servoRotations * turretRevsPerServoRev;

        double turretWholeRotations = Math.floor(turretRotations);
        double turretTarget = turretWholeRotations + targetRotations;
        if (turretTarget - turretRotations > 0.5) {
            turretTarget -= 1.0;
        } else if (turretRotations - turretTarget > 0.5) {
            turretTarget += 1.0;
        }

        double servoTarget = turretTarget / turretRevsPerServoRev;

        servo.setPosition(servoTarget);
    }

    /** True when within tolerance degrees of the target. */
    public boolean atTarget(double toleranceDegrees) {
        return Math.abs(angleDiff(targetDeg, getAngleDegrees())) <= toleranceDegrees;
    }

    /** Call once every loop. */
    public void update() {
        servo.update();
    }

    private static double normalize360(double deg) {
        deg %= 360;
        return deg < 0 ? deg + 360 : deg;
    }

    private static double angleDiff(double target, double current) {
        return (target - current + 540) % 360 - 180;
    }
}
