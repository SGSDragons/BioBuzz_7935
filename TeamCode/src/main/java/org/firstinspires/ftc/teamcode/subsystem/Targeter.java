package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Pose;

import java.util.ArrayList;
import java.util.List;

@Config
public class Targeter {
     // named spot on field
     public static class Target {
        public final String name;
        public final double x, y; // field inches

        public Target(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
     }

     public static double velocity = -950.0;

     private final Localizer localizer;
     private final List<Target> targets = new ArrayList<>();
     private int currentIndex = 0;


    // constructor: gets the Localizer from the Follower
    public Targeter(Follower follower, boolean isRed) {
        this.localizer = follower.localizer;

        // Placeholder positions. Replace with real field coordinates.
        if (isRed) {
            addTarget(new Target("A", 58, 90));
            addTarget(new Target("B", 58, 50));
        } else {
            addTarget(new Target("A", 84, 90));
            addTarget(new Target("B", 84, 50));
        }
    }
    public void addTarget(Target t) {
        targets.add(t);
    }

    public void setTarget(int index) {
        if (index >= 0 && index < targets.size()) {
            currentIndex = index;
        } else {
            throw new IndexOutOfBoundsException("Target index out of bounds");
        }
    }

    public void nextTarget() {
        currentIndex = (currentIndex + 1) % targets.size();
    }

    public Target getCurrentTarget() {
        return targets.get(currentIndex);
    }

    // Turret range in radians, reflective to the robot's forward direction TUNE
    private double turretMinAngle = 0;
    private double turretMaxAngle = 2*Math.PI;

    //Flywheel speed = base + perInch * distance. TUNE ON FEILD.
    private double flywheelBase = 1000; // base speed in ticks per second
    private double flywheelPerInch =8;

    private double distanceToTarget() {
        Pose p = localizer.pose();
        Target t = getCurrentTarget();
        return Math.hypot(t.x - p.x(), t.y - p.y());

    }

    // 0-1 turret position for the turret aimer./
    public double getAimValue() {
        Pose p = localizer.pose();
        Target t = getCurrentTarget();

        double fieldAngle = Math.atan2(t.y - p.y(), t.x - p.x());
        double turretAngle = wrap(fieldAngle - p.heading());

        double value = (turretAngle - turretMinAngle) / (turretMaxAngle - turretMinAngle);
        return Math.max(0, Math.min(1, value));
    }

    // flywheel velocity based on distance to the current target. /
    public double getFlywheelSpeed() {
        // TODO: Compute this based on position
        return velocity;
//        return flywheelBase + flywheelPerInch * distanceToTarget();

    }

    // Wrap an angle into -PI..PI
    private double wrap(double a) {
        while (a > turretMaxAngle) a -= 2 * Math.PI;
        while (a < turretMinAngle) a += 2 * Math.PI;
        return a;
    }

}

