package com.daveclay.processing.kinect.api.stage;

import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import processing.core.PApplet;
import processing.core.PVector;

public class StageMonitor extends PApplet {

    private final Stage stage;
    private final LogSketch logSketch;
    private final int width;
    private final int height;
    private final StageBounds stageBounds;
    private final Stage.CenterZone centerZone;
    private final Stage.LeftFrontZone leftFrontZone;
    private final Stage.RightFrontZone rightFrontZone;
    private final Stage.LeftBackZone leftBackZone;
    private final Stage.RightBackZone rightBackZone;

    private Stage.StageZone currentStageZone;

    private PVector position;
    private float left;
    private float right;
    private float front;
    private float back;
    private float realWorldWidth;
    private float realWorldDepth;
    private float centerRadius;
    private PVector center;

    public StageMonitor(Stage stage,
                        LogSketch logSketch,
                        int width,
                        int height) {
        this.width = width;
        this.height = height;
        this.logSketch = logSketch;
        this.stage = stage;

        this.currentStageZone = null;

        stage.addListener(new BodyLocatorListener() {
            @Override
            public void gestureWasRecognized(RecognitionResult gesture) {
            }

            @Override
            public void userDidEnteredZone(Stage.StageZone stageZone) {
                StageMonitor.this.currentStageZone = stageZone;
            }

            @Override
            public void userDidMove(StagePosition stagePosition) {
            }
        });

        centerZone = (Stage.CenterZone) stage.getStageZoneById(Stage.CenterZone.ID);
        leftFrontZone = (Stage.LeftFrontZone) stage.getStageZoneById(Stage.LeftFrontZone.ID);
        rightFrontZone = (Stage.RightFrontZone) stage.getStageZoneById(Stage.RightFrontZone.ID);
        leftBackZone = (Stage.LeftBackZone) stage.getStageZoneById(Stage.LeftBackZone.ID);
        rightBackZone = (Stage.RightBackZone) stage.getStageZoneById(Stage.RightBackZone.ID);
        stageBounds = stage.getStageBounds();
    }

    public StageMonitor(Stage stage,
                        LogSketch logSketch) {
        this(stage, logSketch, 400, 400);
    }

    @Override
    public void setup() {
        size(width, height);
    }

    @Override
    public void draw() {
        if ( ! stageBounds.initialized()) {
            return;
        }

        left = stageBounds.getLeft();
        right = stageBounds.getRight();
        front = stageBounds.getFront();
        back = stageBounds.getBack();
        realWorldWidth = stageBounds.getWidth();
        realWorldDepth = stageBounds.getDepth();
        centerRadius = centerZone.getCenterRadius();
        center = stageBounds.getCenter();
        position = stage.getPosition();

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

        drawMappedZone(leftFrontZone);
        drawMappedZone(rightFrontZone);
        drawMappedZone(leftBackZone);
        drawMappedZone(rightBackZone);
        drawCenterZone();

        drawPosition(position);
    }

    void drawPosition(PVector position) {
        float mappedPositionX = map(position.x, left, right, 0, width);
        float mappedPositionZ = map(position.z, front, back, 0, height);
        fill(150, 125, 0);
        rect(mappedPositionX, mappedPositionZ, 50, 50);
    }

    private void drawCenterZone() {
        float mappedVerticalCenterRadius = map(centerRadius, 0, realWorldDepth, 0, height) * 2;
        float mappedHorizontalCenterRadius = map(centerRadius, 0, realWorldWidth, 0, width) * 2;
        float mappedCenterX = map(center.x, left, right, 0, width);
        float mappedCenterZ = map(center.z, front, back, 0, height);
        setFill(centerZone);
        ellipse(mappedCenterX, mappedCenterZ, mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
    }

    public void drawMappedZone(Stage.RectStageZone stageZone) {
        PVector leftBottomFront = stageZone.getLeftBottomFront();

        float mappedX = map(leftBottomFront.x, stageBounds.getLeft(), stageBounds.getRight(), 0, width - 2); // leave room for the bounds.
        float mappedY = map(leftBottomFront.z, stageBounds.getFront(), stageBounds.getBack(), 0, height - 2);

        float mappedWidth = map(stageZone.getWidth(), 0, realWorldWidth, 0, width);
        float mappedDepth = map(stageZone.getDepth(), 0, realWorldDepth, 0, height);

        setFill(stageZone);
        rect(mappedX, mappedY, mappedWidth, mappedDepth);
    }

    void setFill(Stage.StageZone stageZone) {
        if (stageZone == this.currentStageZone) {
            fill(0, 255, 0);
        } else {
            fill(100);
        }
    }
}
