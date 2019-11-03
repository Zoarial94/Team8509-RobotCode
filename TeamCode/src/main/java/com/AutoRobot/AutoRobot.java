package com.AutoRobot;

import com.playingField.Movement;

import org.firstinspires.ftc.teamcode.HardwareBot;

public class AutoRobot {

    Movement m;
    HardwareBot hwBot;
    double maxTurn = 0.1;
    double maxForward = 0.2;

    boolean enabled = false;

    public AutoRobot(HardwareBot bot, Movement m) {
        hwBot = bot;
        this.m = m;
        stopMotors();
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
            if(Math.abs(diffA) < 10) {
                hwBot.drive(maxForward, 0);
            } else if(diffA > 0) {
                hwBot.drive(0, -maxTurn);
            } else {
                hwBot.drive(0, maxTurn);
            }
        } else {
            stopMotors();
            return;
        }
    }

    public void stopMotors() {

    }
}
