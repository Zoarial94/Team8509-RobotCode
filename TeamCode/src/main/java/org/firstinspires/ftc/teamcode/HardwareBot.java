/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.ServoFlavor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import dalvik.system.DelegateLastClassLoader;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class HardwareBot
{

    public enum DriveMode {
        ArcadeDrive,
        MechanumDrive
    }

    double sens = .4;
    /* Public OpMode members. */
    private DcMotor frontRightMotor, frontLeftMotor, backRightMotor, backLeftMotor, elevatorMotor;

    private CRServo arm;

    private DriveMode mode = DriveMode.ArcadeDrive;

    /* local OpMode members. */
    HardwareMap hwMap           = null;
    Telemetry telemetry;
    ElapsedTime runtime;

    double prevTime = 0;
    double powerPerMilli = 0.005;


    /* Constructor */
    public HardwareBot(Telemetry t, ElapsedTime r){
        telemetry = t;
        runtime = r;
    }

    public void setSens(double sens) {
        this.sens = Range.clip(Math.abs(sens), 0, 1);
    }

    public double getSens() {
        return sens;
    }

    public void init(HardwareMap ahwmap) {
        init(ahwmap, false);
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap, boolean encoders) {
        // Save reference to Hardware map
        hwMap = ahwMap;


        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontLeftMotor  = hwMap.get(DcMotor.class, "FrontLeftMotor");
        frontRightMotor = hwMap.get(DcMotor.class, "FrontRightMotor");
        backLeftMotor  = hwMap.get(DcMotor.class, "BackLeftMotor");
        backRightMotor = hwMap.get(DcMotor.class, "BackRightMotor");
        elevatorMotor = hwMap.get (DcMotor.class, "ElevatorMotor");
        arm = hwMap.get (CRServo.class, "ServoArm");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        elevatorMotor.setDirection(DcMotor.Direction.REVERSE);
        arm.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        elevatorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stopMotors();

        if(encoders) {
            // Set all motors to run without encoders.
            // May want to use RUN_USING_ENCODERS if encoders are installed.
            frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            // Set all motors to run without encoders.
            // May want to use RUN_USING_ENCODERS if encoders are installed.
            frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }



    }

    public void liftElevator(boolean slow) {
        if (slow) {
            elevatorMotor.setPower(.4);
        } else {
            elevatorMotor.setPower(.9);
        }
    }

    public void lowerElevator() {
        elevatorMotor.setPower(-0.05);
    }

    public void stopElevator() {
        elevatorMotor.setPower(.1);
    }

    public void extendArm() {
        arm.setPower(0.8);
    }

    public void retractArm() {
        arm.setPower(-0.8);
    }

    public void stopArm() {
        arm.setPower(0);
    }

    public void drive(double f, double t) {
        drive(f, t, 0, false);
    }

    public void drive(double f, double t, boolean slow) {
        drive(f, t, 0, slow);
    }

    public void drive(double forward, double turn, double strafe, boolean slow) {

        double frontRightPower = 0, frontLeftPower = 0, backRightPower = 0, backLeftPower = 0;

        forward = applyDeadzone(forward) * sens;
        turn = applyDeadzone(turn) * sens;
        strafe = applyDeadzone(strafe) * sens;

        if(slow) {
            forward *= sens;
            turn *= sens;
            strafe *= sens;
        }

        if(mode == DriveMode.ArcadeDrive) {
            frontRightPower = forward - turn;
            backRightPower = forward - turn;
            frontLeftPower = forward + turn;
            backLeftPower = forward + turn;
        } else if(mode == DriveMode.MechanumDrive) {
            frontRightPower = forward - strafe - turn;
            backRightPower = forward + strafe - turn;
            frontLeftPower = forward + strafe + turn;
            backLeftPower = forward - strafe + turn;

            backLeftPower *= -1;
            backRightPower *= -1;
        }


        double runTime = runtime.milliseconds();
        double timeE = runtime.milliseconds() - prevTime;
        prevTime = runTime;
        double powerToAdd = timeE * powerPerMilli;

        frontLeftPower = calcRamp(frontLeftMotor.getPower(), frontLeftPower, powerToAdd);
        frontRightPower = calcRamp(frontRightMotor.getPower(), frontRightPower, powerToAdd);
        backLeftPower = calcRamp(backLeftMotor.getPower(), backLeftPower, powerToAdd);
        backRightPower = calcRamp(backRightMotor.getPower(), backRightPower, powerToAdd);


        // Send calculated power to wheels
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);


        telemetry.addData("Front Motors", "left (%.2f), right (%.2f)", frontLeftPower, frontRightPower);
        telemetry.addData("Back Motors", "left (%.2f), right (%.2f)", backLeftPower, backRightPower);
    }

    public void setMode(DriveMode i) {
        mode = i;
    }

    public static double applyDeadzone(double position) {
        if(position == 0.0)
            return 0.0;

        boolean positive = position > 0;

        double abs = Math.abs(position);

        if(abs >= 1) {
            return positive ? 1 : -1;
        } else if(abs < Constants.joystickDeadzone) {
            return 0.0;
        } else {
            return positive ? (abs - Constants.joystickDeadzone) / (1.0 - Constants.joystickDeadzone) : -1.0 * ((abs - Constants.joystickDeadzone) / (1.0 - Constants.joystickDeadzone));
        }
    }

    public void stopMotors() {
        // Set all motors to zero power
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        elevatorMotor.setPower(0);
    }

    public double calcRamp(double oldP, double newP, double pToAdd) {
        return (oldP > newP) ?
                ((oldP - pToAdd) < newP) ? newP : oldP - pToAdd :   //  Check to see if power goes below
                ((oldP + pToAdd) > newP) ? newP : oldP + pToAdd ;   //  Check to see if power goes to high
    }

}

