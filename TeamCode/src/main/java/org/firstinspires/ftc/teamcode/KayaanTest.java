package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Hello World!")
public class KayaanTest extends OpMode {
    @Override
    public void init() {
        telemetry.addLine("Hello World!");
    }

    @Override
    public void loop() {
    }
}