package com.playingField;

import com.AStart.AStar;
import com.AStart.Node;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.teamcode.Constants.*;

public class PlayingField {

    boolean fieldIsValid = false;
    boolean pathIsValid = false;
    int validFieldCounter = 0;

    Movement m = new Movement();

    Node initialNode = new Node(2, 1);
    Node finalNode = new Node(12, 12);

    VectorF lastLocation;

    AStar aStar = new AStar(fieldRows, fieldCols, initialNode, finalNode);
    List<Node> path;
    Telemetry telemetry;

    public PlayingField(Telemetry t) {
        telemetry = t;
    }

    public void setRobotPosition(OpenGLMatrix pos) {
        Orientation rotation = Orientation.getOrientation(pos, EXTRINSIC, XYZ, DEGREES);
        lastLocation = pos.getTranslation();
        m.setFromVector(lastLocation);
        m.setH(rotation.thirdAngle);

        initialNode.setRow((int)m.getX());
        initialNode.setCol((int)m.getY());

        validFieldCounter = 0;

        pathIsValid = false;
    }

    public void setTarget(VectorF pos) {

        double x = Range.clip((pos.get(0) / mmPerInch) + fieldRows/2, 1.0, 142.0);
        double y = Range.clip((pos.get(1) / mmPerInch) + fieldCols/2, 1.0, 142.0);

        finalNode.setRow((int)x);
        finalNode.setCol((int)y);

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

    public void updateMovement() {
        if(!pathIsValid) {
            path = aStar.findPath();
            pathIsValid = true;
        }
        if(path.isEmpty()) {
            return;
        }
        int y = path.get(1).getCol() - initialNode.getCol();
        int x = path.get(1).getRow() - initialNode.getRow();
        double a = Math.atan2(y, x) * (180.0/Math.PI);
        double d = Math.sqrt(y*y + x*x);

        telemetry.addLine("Start:  X:" + initialNode.getRow() + " Y:" + initialNode.getCol());
        telemetry.addLine("Finish: X:" + finalNode.getRow() + " Y:" + finalNode.getCol());
        telemetry.addLine("Next Move: " + a + " degrees, " + d + " inches");
    }

    public Movement getMovement() {
        return m;
    }

    public boolean isValid() {
        return fieldIsValid;
    }

    public double getDistFromTarget() {
        int y = path.get(0).getCol() - initialNode.getCol();
        int x = path.get(0).getRow() - initialNode.getRow();
        double d = Math.sqrt(y*y + x*x);
        return d;
    }

}
