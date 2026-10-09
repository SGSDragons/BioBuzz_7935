package org.firstinspires.ftc.teamcode.opmode.tests;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

//this class is a place to put utils to get motor things using the DcMotor or DcMotorX hardware map so it does not conflict with pedro.

public class MotorUtils {

    public Double getcurent(int motor) {

        //I have not tested this code. I will remove this line once I have confirmed it works)

        //this double can be used by giving it motor number (1 = frontleft, 2 = frontright, 3 = backleft, 4 = backright).
        //it returns the current being used in a motor in amps. if the value is null, the input int was not between 1 and 4, or the library had a problem.

        DcMotorEx frontLeftDrive = null;
        DcMotorEx backLeftDrive = null;
        DcMotorEx frontRightDrive = null;
        DcMotorEx backRightDrive = null;

        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "port2");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "port3");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "port1");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "port0");

        if (motor == 1) {
            return frontLeftDrive.getCurrent(CurrentUnit.AMPS);
        }
        if (motor == 2) {
            return frontRightDrive.getCurrent(CurrentUnit.AMPS);
        }
        if (motor == 3) {
            return backLeftDrive.getCurrent(CurrentUnit.AMPS);
        }
        if (motor == 4) {
            return backRightDrive.getCurrent(CurrentUnit.AMPS);
        }
        else {
            return null;
        }

    }

}
