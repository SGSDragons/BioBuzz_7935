package org.firstinspires.ftc.teamcode;

/*
 * When panels is successfully configured, uncomment all commented lines.
 *
 * Build & Deploy
 * Go to 192.168.43.1:8001 to view the dashboard
 * Run this opmode to measure the time between the while loop cycle
 */

//import com.bylazar.telemetry.PanelsTelemetry;
//import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Panels Test", group="Tests")
public class PanelsTest extends LinearOpMode {
    @Override
    public void runOpMode() {

//        TelemetryManager telemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        waitForStart();

        long lastTime = System.nanoTime();
        while (opModeIsActive()) {
            long currentTime = System.nanoTime();

            telemetry.addData("Cycle Duration", currentTime - lastTime);
            telemetry.update();

            lastTime = currentTime;
        }
    }
}
