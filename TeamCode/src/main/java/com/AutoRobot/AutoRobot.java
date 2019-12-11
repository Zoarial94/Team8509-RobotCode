package com.AutoRobot;

import com.playingField.Movement;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.teamcode.HardwareBot;

import java.util.ArrayList;

import dalvik.system.DelegateLastClassLoader;

class MoveInfo {
    public static int side, forward;
    static boolean sideFinished, forwardFinished;
    public static double startTime;

    public static void reset() {
        side = 0;
        forward = 0;
        sideFinished = false;
        forwardFinished = false;
        startTime = 0;
    }
}

class WaitTaskInfo {
    public static double time = 0;
    public static double startTime = 0;

    public static void reset() {
        time = 0;
        startTime = 0;
    }
}

class ApproachTargetInfo {
    public static double startTime;
    public static double curTime;

    public static void reset() {
        startTime = 0;
        curTime = 0;
    }
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

    ArrayList<QueueItem> autoQueue = new ArrayList<>();
    VuforiaTrackable trackable = null;

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

    public void nextTask() {
        if(autoQueue.isEmpty()){
            taskIsFinished = true;
            return;
        } else {
            taskIsFinished = false;
        }

        QueueItem next =  autoQueue.get(0);
        autoQueue.remove(0);

        if(next.task == CurrentTask.Move) {
            move(next.a, next.b);
            curTask = CurrentTask.Move;
        } else if(next.task == CurrentTask.Wait){
            autoWait(next.a);
            curTask = CurrentTask.Wait;
        } else if(next.task == CurrentTask.ApproachTarget) {
            curTask = CurrentTask.ApproachTarget;
        } else {
            curTask = CurrentTask.None;
        }
    }

    public void addToQueue(QueueItem q) {
        autoQueue.add(q);
    }

    public boolean isQueueEmpty() {
        return autoQueue.isEmpty();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void findElevatorSetpoint() {

    }

    public void move(int forwardUnits, int sideUnits) {
        MoveInfo.reset();

        MoveInfo.startTime = time.milliseconds();

        MoveInfo.forward = forwardUnits;
        MoveInfo.side = sideUnits;

        if (forwardUnits == 0) {
            MoveInfo.forwardFinished = true;
        }
        if (sideUnits == 0) {
            MoveInfo.sideFinished = true;
        }

        taskIsFinished = false;
    }

    public void autoWait(int period) {
        WaitTaskInfo.startTime = time.milliseconds();
        WaitTaskInfo.time = period;
    }

    public boolean continueTask() {

        telemetry.addLine("Current Task: " + curTask);

        if(taskIsFinished()) {
            return true;
        }

        //  Move Task
        if(curTask == CurrentTask.Move) {
            if(!MoveInfo.sideFinished) {
                double timeSinceStart = time.milliseconds() - MoveInfo.startTime;

                if(timeSinceStart < MoveInfo.side * 500) {
                    hwBot.drive(0, 0, maxForward, false);
                } else {
                    MoveInfo.sideFinished = true;
                    hwBot.drive(0, 0);
                }
            } else if(!MoveInfo.forwardFinished) {
                double timeSinceStart = time.milliseconds() - MoveInfo.startTime;

                if(timeSinceStart < MoveInfo.forward * 500) {
                    hwBot.drive(maxForward, 0, 0, false);
                } else {
                    MoveInfo.forwardFinished = true;
                    hwBot.drive(0, 0);
                }
            } else {
                curTask = CurrentTask.None;
                taskIsFinished = true;
            }
        }
        //  Wait Task
        else if (curTask == CurrentTask.Wait) {
            if(time.milliseconds() - WaitTaskInfo.startTime >= WaitTaskInfo.time) {
                taskIsFinished = true;
            }
        }
        //  Approach Target
        else if (curTask == CurrentTask.ApproachTarget) {
            OpenGLMatrix robotLoc = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
            if(robotLoc != null) {
                if(robotLoc.getTranslation().get(0) > 20) {
                    hwBot.drive(0, 0, 0.2, false);
                } else if(robotLoc.getTranslation().get(1) > 20) {
                    hwBot.drive(0.2, 0, 0, false);
                }
            }
        }

        return taskIsFinished;
    }

    public void setTargetToTrack(VuforiaTrackable target) {
        trackable = target;
    }

}
