package com.AutoRobot;

import com.playingField.Movement;

import org.firstinspires.ftc.teamcode.HardwareBot;

public class AutoRobot {

    Movement m;
    HardwareBot hwBot;
    double maxTurn = 0.2;
    double maxForward = 0.3;

    boolean enabled = false;

    public AutoRobot(HardwareBot bot, Movement m) {
        hwBot = bot;
        this.m = m;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        stopMotors();
        enabled = false;
    }

    public void drive() {
        if(enabled) {
            double diffA = m.getAngle() - m.getH();
            if(Math.abs(diffA) < 20) {
                hwBot.drive(maxForward, 0, 0);
            } else if(diffA > 0) {
                hwBot.drive(0, -maxTurn, 0);
            } else {
                hwBot.drive(0, maxTurn, 0);
            }
        } else {
            stopMotors();
            return;
        }
    }

    public void stopMotors() {
        hwBot.drive(0, 0, 0);
    }
}
