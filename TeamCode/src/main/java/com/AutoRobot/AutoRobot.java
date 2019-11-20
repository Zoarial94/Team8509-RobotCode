package com.AutoRobot;

import com.playingField.Movement;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareBot;

import dalvik.system.DelegateLastClassLoader;

public class AutoRobot {

    Movement m;
    HardwareBot hwBot;
    Telemetry telemetry = null;
    double maxTurn = .5;
    double maxForward = .6;

    boolean enabled = false;
    ElapsedTime time;
    double timer = 0;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void findElevatorSetpoint() {

    }
}
