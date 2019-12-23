package com.AutoRobot;

import com.playingField.Movement;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cCompassSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.teamcode.HardwareBot;

import java.util.ArrayList;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.teamcode.Constants.mmPerInch;

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
    public static double time;
    public static Task curTask;
    enum Task {
        None,
        MoveX,
        MoveY,
        Rotate,
        Wait
    }

    public static void reset() {
        startTime = 0;
        time = 0;
        curTask = Task.None;
    }
}

public class AutoRobot {

    static final float far = 20 * mmPerInch, close = 10 * mmPerInch, headingError = 15, deadTime = 100;

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
    ModernRoboticsI2cCompassSensor compass;
    HardwareMap hwMap = null;

    public AutoRobot(HardwareBot bot, Movement m, Telemetry t, ElapsedTime r) {
        hwBot = bot;
        this.m = m;
        telemetry = t;
        time = r;
    }

    public void init(HardwareMap hwMap) {
        this.hwMap = hwMap;
        compass = hwMap.get(ModernRoboticsI2cCompassSensor.class, "compass");
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

        QueueItem next = autoQueue.remove(0);

        if(next.task == CurrentTask.Move) {
            setupAutoMove(next.a, next.b);
            curTask = CurrentTask.Move;
        } else if(next.task == CurrentTask.Wait){
            setupAutoWait(next.a);
            curTask = CurrentTask.Wait;
        } else if(next.task == CurrentTask.ApproachTarget) {
            setupApproachTarget();
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

    public void setupAutoMove(int forwardUnits, int sideUnits) {
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
    }

    public void setupAutoWait(int period) {
        WaitTaskInfo.startTime = time.milliseconds();
        WaitTaskInfo.time = period;
    }

    public void setupApproachTarget() {
        ApproachTargetInfo.reset();
    }

    public void continueTask() {

        telemetry.addLine("Current Task: " + curTask);

        if(taskIsFinished()) {
            return;
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
                    finishCurrentTask();
                }
            } else if(!MoveInfo.forwardFinished) {
                double timeSinceStart = time.milliseconds() - MoveInfo.startTime;

                if(timeSinceStart < MoveInfo.forward * 500) {
                    hwBot.drive(maxForward, 0, 0, false);
                } else {
                    MoveInfo.forwardFinished = true;
                    hwBot.drive(0, 0);
                    finishCurrentTask();
                }
            }
        }
        //  Wait Task
        else if (curTask == CurrentTask.Wait) {
            if(time.milliseconds() - WaitTaskInfo.startTime >= WaitTaskInfo.time) {
                finishCurrentTask();
            }
        }
        //  Approach Target
        else if (curTask == CurrentTask.ApproachTarget) {
            telemetry.addLine("Approack Task: " + ApproachTargetInfo.curTask);
            OpenGLMatrix robotLoc = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
            if(robotLoc != null) {
                switch(ApproachTargetInfo.curTask) {
                    case None:
                        if(Math.abs(getHeading(robotLoc)) > headingError){
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.Rotate;
                        } else if(Math.abs(getYPos(robotLoc)) > close) {
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.MoveX;
                        } else if(Math.abs(getXPos(robotLoc)) > close) {
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.MoveY;
                        }
                        break;
                    case MoveX:
                        double x = Math.abs(getXPos(robotLoc));
                        if (x > far) {
                            hwBot.drive(maxForward * 0.4, 0, 0, false);
                        } else if (x > close) {
                            hwBot.drive(maxForward * 0.2, 0, 0,  false);
                        } else {
                            hwBot.drive(0, 0);
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.Wait;
                            ApproachTargetInfo.time = deadTime;
                            ApproachTargetInfo.startTime = time.milliseconds();
                        }
                        break;
                    case MoveY:
                        double y = Math.abs(getYPos(robotLoc));
                        if (y > far) {
                            hwBot.drive(0, 0, maxForward * 0.4, false);
                        } else if (y > close) {
                            hwBot.drive(0, 0, maxForward * 0.2, false);
                        } else {
                            hwBot.drive(0, 0);
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.Wait;
                            ApproachTargetInfo.time = deadTime;
                            ApproachTargetInfo.startTime = time.milliseconds();
                        }
                        break;
                    case Rotate:
                        double toRot = getHeading(robotLoc);
                        telemetry.addLine("Heading: " + toRot);
                        if(Math.abs(toRot) < headingError) {
                            hwBot.drive(0, 0);
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.Wait;
                            ApproachTargetInfo.time = deadTime;
                            ApproachTargetInfo.startTime = time.milliseconds();
                        } else if(toRot > 0) {
                            hwBot.drive(0, maxForward * 0.25, 0, false);
                        } else {
                            hwBot.drive(0, maxForward * -0.25, 0, false);
                        }
                        break;
                    case Wait:
                        if(time.milliseconds() >= ApproachTargetInfo.startTime + ApproachTargetInfo.time) {
                            ApproachTargetInfo.curTask = ApproachTargetInfo.Task.None;
                        }
                        break;

                }
            }
        } else {
            finishCurrentTask();
        }
    }

    private double getHeading(OpenGLMatrix robotLoc) {
        return Orientation.getOrientation(robotLoc, EXTRINSIC, XYZ, DEGREES).thirdAngle;
    }

    private double getXPos(OpenGLMatrix robotLoc) {
        return robotLoc.getTranslation().get(0);
    }

    private double getYPos(OpenGLMatrix robotLoc) {
        return robotLoc.getTranslation().get(1);
    }




    public void setTargetToTrack(VuforiaTrackable target) {
        trackable = target;
    }

    void finishCurrentTask() {
        curTask = CurrentTask.None;
        taskIsFinished = true;
    }

}
