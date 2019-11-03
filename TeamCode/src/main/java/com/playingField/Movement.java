package com.playingField;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import static org.firstinspires.ftc.teamcode.Constants.fieldCols;
import static org.firstinspires.ftc.teamcode.Constants.fieldRows;
import static org.firstinspires.ftc.teamcode.Constants.mmPerInch;

public class Movement {
    double a, d, x, y, h;

    public void setFromVector(VectorF v) {
        this.x = Range.clip((int)(v.get(0) / mmPerInch) + fieldRows/2, 1, 142);
        this.y = Range.clip((int)(v.get(1) / mmPerInch) + fieldCols/2, 1, 142);
    }

    public void setAngle(double a) {
        this.a = a;
    }

    public void setDis(double d) {
        this.d = d;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getAngle() {
        return a;
    }

    public double getDis() {
        return d;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getH() { return h; }
}
