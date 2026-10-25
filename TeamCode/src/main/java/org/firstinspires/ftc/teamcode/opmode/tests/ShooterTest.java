package org.firstinspires.ftc.teamcode.opmode.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.Sorter;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="shooter test", group="Test")
@Configurable
public class ShooterTest extends LinearOpMode{

    @Sorter(sort = 0)
    public static double kp = 0.0;
    @Sorter(sort = 1)
    public static double ki = 0.0;
    @Sorter(sort = 2)
    public static double kd = 0.0;
    @Sorter(sort = 3)
    public static double kf = 0.0;
    @Sorter(sort = 4)
    public static double targetSpeed = 1000;

    @Sorter(sort = 5)
    public static double intakeSpeed = 0.5;
    @Sorter(sort = 6)
    public static double feedSpeed = 0.9;

    DcMotorEx leftShooter;
    DcMotorEx rightShooter;
    DcMotorEx intake;
    DcMotorEx feeder;

    @Override
    public void runOpMode() {

        TelemetryManager.TelemetryWrapper telemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
        waitForStart();

        leftShooter = hardwareMap.get(DcMotorEx.class, "leftshooter");
        leftShooter.setDirection(DcMotorEx.Direction.FORWARD);

        rightShooter = hardwareMap.get(DcMotorEx.class, "rightshooter");
        rightShooter.setDirection(DcMotorEx.Direction.FORWARD);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setDirection(DcMotorEx.Direction.REVERSE);

        feeder = hardwareMap.get(DcMotorEx.class, "feeder");
        feeder.setDirection(DcMotorEx.Direction.REVERSE);

        while (opModeIsActive()) {
            leftShooter.setVelocityPIDFCoefficients(kp,ki,kd,kf);
            rightShooter.setVelocityPIDFCoefficients(kp,ki,kd,kf);

            if (gamepad1.right_bumper) {
                leftShooter.setVelocity(targetSpeed);
                rightShooter.setVelocity(targetSpeed);
            } else {
                leftShooter.setVelocity(0.0);
                rightShooter.setPower(0.0);
            }

            if (gamepad1.a) {
                intake.setPower(intakeSpeed);
                feeder.setPower(feedSpeed);
            } else {
                intake.setPower(-gamepad1.left_stick_y);
                feeder.setPower(-gamepad1.right_stick_y);
            }

            telemetry.addData("Target speed ", targetSpeed);
            telemetry.addData("Current speed", (rightShooter.getVelocity()+leftShooter.getVelocity()/2));

            telemetry.update();
        }
    }
}
