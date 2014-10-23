package com.daveclay.processing.kinect.api;

import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;
import processing.core.PVector;

public class StageMonitor extends PApplet {

    private final Stage stage;
    private final LogSketch logSketch;
    private final User user;
    private final int width;
    private final int height;
    private final StageBounds stageBounds;
    private final Stage.CenterZone centerZone;
    private final Stage.LeftFrontZone leftFrontZone;
    private final Stage.RightFrontZone rightFrontZone;
    private final Stage.LeftBackZone leftBackZone;
    private final Stage.RightBackZone rightBackZone;

    PVector position;
    float left;
    float right;
    float front;
    float back;
    float realWorldWidth;
    float realWorldDepth;
    float centerRadius;
    PVector center;

    public StageMonitor(Stage stage,
                        User user,
                        LogSketch logSketch,
                        int width,
                        int height) {
        this.width = width;
        this.height = height;
        this.logSketch = logSketch;
        this.user = user;
        this.stage = stage;

        centerZone = (Stage.CenterZone) stage.getStageZoneById(Stage.CenterZone.ID);
        leftFrontZone = (Stage.LeftFrontZone) stage.getStageZoneById(Stage.LeftFrontZone.ID);
        rightFrontZone = (Stage.RightFrontZone) stage.getStageZoneById(Stage.RightFrontZone.ID);
        leftBackZone = (Stage.LeftBackZone) stage.getStageZoneById(Stage.LeftBackZone.ID);
        rightBackZone = (Stage.RightBackZone) stage.getStageZoneById(Stage.RightBackZone.ID);
        stageBounds = stage.getStageBounds();
    }

    public StageMonitor(Stage stage,
                        LogSketch logSketch,
                        User user) {
        this(stage, user, logSketch, 400, 400);
    }

    @Override
    public void setup() {
        size(width, height);
    }

    @Override
    public void draw() {
        // real-life values:
        position = user.centerOfMass;
        stage.updatePosition(position);

        left = stageBounds.getLeft();
        right = stageBounds.getRight();
        front = stageBounds.getFront();
        back = stageBounds.getBack();
        realWorldWidth = stageBounds.getWidth();
        realWorldDepth = stageBounds.getDepth();
        centerRadius = centerZone.getCenterRadius();
        center = stageBounds.getCenter();

        logSketch.logVector("Stage Position", position);
        /*
        logSketch.log("Within Center", centerZone.isWithinBounds(position));
        logSketch.log("Within Left Front", leftFrontZone.isWithinBounds(position));
        logSketch.log("Within Right Front", rightFrontZone.isWithinBounds(position));
        logSketch.log("Within Left Back", leftBackZone.isWithinBounds(position));
        logSketch.log("Within Right Back", rightBackZone.isWithinBounds(position));
        */

        background(100);
        stroke(255, 255, 255);
        strokeWeight(2);

        drawMappedZone(leftFrontZone, position);
        drawMappedZone(rightFrontZone, position);
        drawMappedZone(leftBackZone, position);
        drawMappedZone(rightBackZone, position);
        drawCenterZone();

        drawPosition(position);
    }

    void drawPosition(PVector position) {
        float mappedPositionX = map(position.x, left, right, 0, width);
        float mappedPositionZ = map(position.z, front, back, 0, height);
        fill(100);
        rect(mappedPositionX, mappedPositionZ, 10, 10);
    }

    private void drawCenterZone() {
        float mappedVerticalCenterRadius = map(centerRadius, 0, realWorldDepth, 0, height) * 2;
        float mappedHorizontalCenterRadius = map(centerRadius, 0, realWorldWidth, 0, width) * 2;
        float mappedCenterX = map(center.x, left, right, 0, width);
        float mappedCenterZ = map(center.z, front, back, 0, height);
        setCenterFill(position);
        ellipse(mappedCenterX, mappedCenterZ, mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
    }

    void setCenterFill(PVector position) {
        if (centerZone.isWithinBounds(position)) {
            fill(0, 255, 0);
        } else {
            fill(100);
        }
    }

    void setRectFill(Stage.RectStageZone stageZone, PVector position) {
        if (!centerZone.isWithinBounds(position) && stageZone.isWithinBounds(position) ) {
            fill(0, 255, 0);
        } else {
            fill(100);
        }
    }

    public void drawMappedZone(Stage.RectStageZone stageZone, PVector position) {
        PVector leftBottomFront = stageZone.getLeftBottomFront();

        float mappedX = map(leftBottomFront.x, stageBounds.getLeft(), stageBounds.getRight(), 0, width - 2); // leave room for the bounds.
        float mappedY = map(leftBottomFront.z, stageBounds.getFront(), stageBounds.getBack(), 0, height - 2);

        float mappedWidth = map(stageZone.getWidth(), 0, realWorldWidth, 0, width);
        float mappedDepth = map(stageZone.getDepth(), 0, realWorldDepth, 0, height);

        setRectFill(stageZone, position);
        rect(mappedX, mappedY, mappedWidth, mappedDepth);
    }
}
