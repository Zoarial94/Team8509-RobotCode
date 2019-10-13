package com.playingField;

import com.AStart.AStar;
import com.AStart.Node;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import static org.firstinspires.ftc.teamcode.Constants.mmPerInch;

public class PlayingField {
    static final int rows = 144; // 12ft x 12ft area
    static final int cols = 144;

    Node initialNode = new Node(2, 1);
    Node finalNode = new Node(12, 12);

    AStar aStar = new AStar(rows, cols, initialNode, finalNode);
    Telemetry telemetry;

    public PlayingField(Telemetry t) {
        telemetry = t;
    }

    public void setRobotPosition(VectorF position) {
        int x = Range.clip((int)(position.get(0) / mmPerInch) + rows/2, 1, 142);
        int y = Range.clip((int)(position.get(1) / mmPerInch) + rows/2, 1, 142);

        initialNode.setRow(x);
        initialNode.setCol(y);

    }

    public void setTarget(VectorF pos) {

        int x = Range.clip((int)(pos.get(0) / mmPerInch) + rows/2, 0, 143);
        int y = Range.clip((int)(pos.get(1) / mmPerInch) + rows/2, 0, 143);

        finalNode.setRow(x);
        finalNode.setCol(y);
    }

    public void printDebug() {
        telemetry.addData("Position", "%d, %d", initialNode.getRow(), initialNode.getCol());
        telemetry.addData("Map Pos", "%d, %d", aStar.getInitialNode().getRow(), aStar.getInitialNode().getCol());
    }
}
