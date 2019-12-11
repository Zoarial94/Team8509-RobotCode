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

package org.firstinspires.ftc.teamcode.OpModes;

import com.AutoRobot.AutoRobot;
import com.playingField.Movement;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.*;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Test OpMode", group="Linear Opmode")
public class TestOpMode extends OpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    DigitalChannel Vac1, Valve1;
    HardwareBot robot = new HardwareBot(telemetry, runtime);
    Movement m = new Movement();
    AutoRobot autoBot = new AutoRobot(robot, m, telemetry, runtime);

    boolean oldVacButton = false;
    boolean vacToggle = false;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //  Initialize Hardware
        robot.init(hardwareMap, true);

        Vac1 = hardwareMap.get(DigitalChannel.class, "Vac1");
        Valve1 = hardwareMap.get(DigitalChannel.class, "Valve1");

        Vac1.setMode(DigitalChannel.Mode.OUTPUT);

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */

    boolean b = false;
    @Override
    public void init_loop() {
        if(!b) {
            telemetry.addLine("Press a to find the elevator setpoint automatically");
            telemetry.addLine("Or use the joystick to adjust it");
        } else {
            telemetry.addLine("Setting automatically");
            autoBot.findElevatorSetpoint();

        }

        if(gamepad1.a) {
            b = true;
        }
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
        robot.setMode(HardwareBot.DriveMode.MechanumDrive);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double forward = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        double strafe = gamepad1.left_stick_x;


        robot.drive(forward, turn, strafe, gamepad1.left_trigger > 0.6);

        if (gamepad2.x){
            robot.retractArm();
        } else if (gamepad2.y){
            robot.extendArm();
        } else {
            robot.stopArm();
        }

        if (gamepad2.b) {
            robot.liftElevator(gamepad2.left_trigger > 0.6);
        }else if(gamepad2.a){
            robot.lowerElevator();
        } else {
            robot.stopElevator();
        }




        // vacuum can be toggled on or off with the a button
        if(gamepad2.right_bumper && oldVacButton == false) {
            vacToggle = !vacToggle;
        }

        oldVacButton = gamepad2.right_bumper;

        Vac1.setState(vacToggle);

        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}

// beginning of work on the vacuum button thing


