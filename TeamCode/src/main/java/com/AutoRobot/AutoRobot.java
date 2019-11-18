package com.AutoRobot;

import com.playingField.Movement;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareBot;

public class AutoRobot {

    Movement m;
    HardwareBot hwBot;
    Telemetry telemetry = null;
    double maxTurn = .5;
    double maxForward = .6;

    boolean enabled = false;

    public AutoRobot(HardwareBot bot, Movement m, Telemetry t) {
        hwBot = bot;
        this.m = m;
        telemetry = t;
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
            telemetry.addLine("Angle to Turn: " + diffA + "\n(" + m.getAngle() + " " + m.getH() + ")");
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
}
