package com.playingField;

import com.AStart.AStar;
import com.AStart.Node;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import java.util.List;

import static org.firstinspires.ftc.teamcode.Constants.*;



class Location {
    public final int x;
    public final int y;
    Location(VectorF v) {
        x = Range.clip((int)(v.get(0) / mmPerInch) + fieldCols/2, 1, 142);
        y = Range.clip((int)(v.get(1) / mmPerInch) + fieldRows/2, 1, 142);
    }
}

public class PlayingField {

    boolean fieldIsValid = false;
    boolean pathIsValid = false;
    int validFieldCounter = 0;

    Node initialNode = new Node(2, 1);
    Node finalNode = new Node(12, 12);

    VectorF lastLocation;

    AStar aStar = new AStar(fieldRows, fieldCols, initialNode, finalNode);
    List<Node> path;
    Telemetry telemetry;

    public PlayingField(Telemetry t) {
        telemetry = t;
    }

    public void setRobotPosition(VectorF pos) {
        lastLocation = pos;
        Location l = vectorToLocation(pos);

        initialNode.setRow(l.x);
        initialNode.setCol(l.y);

        validFieldCounter = 0;

        pathIsValid = false;
    }

    public void setTarget(VectorF pos) {

        Location l = vectorToLocation(pos);

        finalNode.setRow(l.x);
        finalNode.setCol(l.y);

        pathIsValid = false;

    }

    public void printDebug() {
        /*
        telemetry.addData("Position", "%d, %d", initialNode.getRow(), initialNode.getCol());
        telemetry.addData("Map Pos", "%d, %d", aStar.getInitialNode().getRow(), aStar.getInitialNode().getCol());
        */
    }

    public void update() {
        validFieldCounter++;
        if(validFieldCounter > 20) {
            fieldIsValid = false;
        }
    }

    public Movement nextDirection() {
        if(!pathIsValid) {
            path = aStar.findPath();
            pathIsValid = true;
        }
        int y = path.get(0).getCol() - initialNode.getCol();
        int x = path.get(0).getRow() - initialNode.getRow();
        double a = Math.atan2(y, x) * (180.0/Math.PI);
        double d = Math.sqrt(y*y + x*x);

        telemetry.addLine("Start:  X:" + initialNode.getRow() + " Y:" + initialNode.getCol());
        telemetry.addLine("Finish: X:" + finalNode.getRow() + " Y:" + finalNode.getCol());
        telemetry.addLine("Next Move: " + a + " degrees, " + d + " inches");

        return new Movement(a, d);
    }

    public boolean isValid() {
        return fieldIsValid;
    }

    public static Location vectorToLocation(VectorF v) {
        return new Location(v);
    }
}
