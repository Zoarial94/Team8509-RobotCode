package com.AutoRobot;

import org.firstinspires.ftc.teamcode.HardwareBot;

public class AutoRobot {

    double maxTurn = 0.1;
    double maxForward = 0.2;

    boolean enabled = false;

    public AutoRobot(HardwareBot bot) {
        stopMotors();
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        stopMotors();
        enabled = false;
    }

    public void drive(double a, double d) {

    }

    public void stopMotors() {

    }
}
