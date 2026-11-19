package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    private static final double UP_POSITION = 0.0;    // resting position, TUNE
    private static final double DOWN_POSITION = 0.6;  // under the flower, TUNE

    private final Servo servo;
    private Servo servo2 = null; // second servo, added later
    private boolean down = false;

    public Arm(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "armServo"); // must match your config
        // When the second servo exists, uncomment these two lines:
        // servo2 = hardwareMap.get(Servo.class, "armServo2");
        // servo2.setDirection(Servo.Direction.REVERSE); // if mounted mirrored
        setDown(false); // start up at rest
    }

    public void setDown(boolean down) {
        this.down = down;
        double pos = down ? DOWN_POSITION : UP_POSITION;
        servo.setPosition(pos);
        if (servo2 != null) servo2.setPosition(pos);
    }

    public void toggle() {
        setDown(!down);
    }

    public boolean isDown() {
        return down;
    }
}