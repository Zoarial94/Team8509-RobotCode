package com.AutoRobot;

import com.playingField.Movement;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareBot;

import dalvik.system.DelegateLastClassLoader;

enum CurrentTask {
    None,
    Wait,
    FindEleveatorSetPoint,
    Move
}

class MoveInfo {
    public int side, forward;
    boolean sideFinished, forwardFinished;
    public double startTime;
}

class WaitTaskInfo {
    public static double time = 0;
    public static double startTime = 0;
}

public class AutoRobot {

    Movement m;
    HardwareBot hwBot;
    Telemetry telemetry = null;
    double maxTurn = .5;
    double maxForward = .6;

    boolean enabled = false;
    ElapsedTime time;
    double timer = 0;

    boolean taskIsFinished = true;
    CurrentTask curTask = CurrentTask.None;
    MoveInfo moveTaskInfo = new MoveInfo();

    public AutoRobot(HardwareBot bot, Movement m, Telemetry t, ElapsedTime r) {
        hwBot = bot;
        this.m = m;
        telemetry = t;
        time = r;
    }

    public void enable() {
        if(!enabled) {
            enabled = true;
        }
    }

    public void disable() {
        if(enabled) {
            stopMotors();
            enabled = false;
        }
    }

    public void drive() {
        if(enabled) {
            double diffA = m.getAngle() - m.getH();
            //telemetry.addLine("Angle to Turn: " + diffA + "\n(" + m.getAngle() + " " + m.getH() + ")");
            if(Math.abs(diffA) < 10) {
                hwBot.drive(maxForward, 0, 0, false);
            } else if(diffA > 0) {
                hwBot.drive(0, -maxTurn, 0, false);
            } else {
                hwBot.drive(0, maxTurn, 0, false );
            }
        }
    }

    public void stopMotors() {
        if(enabled) {
           hwBot.stopMotors();
        }
    }

    public boolean taskIsFinished() {
        return taskIsFinished;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void findElevatorSetpoint() {

    }

    public void move(int forwardUnits, int sideUnits) {
        if(curTask == CurrentTask.None) {
            curTask = CurrentTask.Move;
        } else {
            telemetry.addLine("A task is in progress: " + curTask);
            return;
        }

        moveTaskInfo.forward = forwardUnits;
        moveTaskInfo.side = sideUnits;

        if (forwardUnits == 0) {
            moveTaskInfo.forwardFinished = true;
        }
        if (sideUnits == 0) {
            moveTaskInfo.sideFinished = true;
        }

        taskIsFinished = false;
    }

    public boolean continueTask() {
        if(taskIsFinished()) {
            return true;
        }

        //  Move Task
        if(curTask == CurrentTask.Move) {
            if(!moveTaskInfo.sideFinished) {
                double timeSinceStart = time.milliseconds() - moveTaskInfo.startTime;

                if(timeSinceStart < moveTaskInfo.side * 500) {
                    hwBot.drive(0, 0, maxForward, false);
                } else {
                    hwBot.drive(0, 0);
                    curTask = CurrentTask.None;
                    taskIsFinished = true;
                }
            } else if(!moveTaskInfo.forwardFinished) {
                double timeSinceStart = time.milliseconds() - moveTaskInfo.startTime;

                if(timeSinceStart < moveTaskInfo.forward * 500) {
                    hwBot.drive(maxForward, 0, 0, false);
                } else {
                    hwBot.drive(0, 0);
                    curTask = CurrentTask.None;
                    taskIsFinished = true;
                }
            }
        }
        //  Wait Task
        else if (curTask == CurrentTask.Wait) {
            if(time.milliseconds() - WaitTaskInfo.startTime >= WaitTaskInfo.time) {
                taskIsFinished = true;
            }
        }

        return taskIsFinished;
    }



}
