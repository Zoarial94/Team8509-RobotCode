package com.AutoRobot;

public class QueueItem {

    public QueueItem(CurrentTask t, int a, int b) {
        task = t;
        this.a = a;
        this.b = b;
    }

    public CurrentTask task;
    public int a, b;
}
