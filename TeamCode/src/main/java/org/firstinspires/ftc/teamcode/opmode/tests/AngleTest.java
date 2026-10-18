package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp(name = "Angle Test", group = "Test")
public class AngleTest extends LinearOpMode {


    public static double Angle = 0;

    @Override
    public void runOpMode() {
        waitForStart();
        while (opModeIsActive()) {

            double x = gamepad1.right_stick_x;
            double y = gamepad1.right_stick_y;

            Angle = Math.toDegrees(Math.atan2(x, y));
            telemetry.addData("Angle", Angle);
            telemetry.update();
        }
    }

}
