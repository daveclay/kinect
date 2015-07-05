package com.daveclay.processing.kinect.api.stage;

import com.daveclay.processing.api.HUD;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import processing.core.PApplet;
import processing.core.PVector;

public class StageMonitor {

    private final Stage stage;
    private final HUD hud;
    private final int width;
    private final int height;
    private final float halfWidth;
    private final float halfHeight;
    private final int alpha = 100;
    private final StageBounds stageBounds;
    private final Stage.CenterZone centerZone;
    private final Stage.LeftFrontZone leftFrontZone;
    private final Stage.RightFrontZone rightFrontZone;
    private final Stage.LeftBackZone leftBackZone;
    private final Stage.RightBackZone rightBackZone;

    private PApplet currentCanvas;

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
                        HUD hud,
                        int width,
                        int height) {
        this.width = width;
        this.height = height;
        this.hud = hud;
        this.stage = stage;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;

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
                        HUD hud) {
        this(stage, hud, 400, 400);
    }

    public synchronized void draw(PApplet canvas) {
        this.currentCanvas = canvas;

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

        hud.logVector("Stage Position", position);
        hud.log("Zone", currentStageZone.getID());

        /*
        hud.log("Within Center", centerZone.isWithinBounds(position));
        hud.log("Within Left Front", leftFrontZone.isWithinBounds(position));
        hud.log("Within Right Front", rightFrontZone.isWithinBounds(position));
        hud.log("Within Left Back", leftBackZone.isWithinBounds(position));
        hud.log("Within Right Back", rightBackZone.isWithinBounds(position));
        */

        canvas.pushMatrix();
        canvas.pushStyle();
        canvas.translate(0, canvas.getHeight() - height);

        canvas.fill(100, alpha);
        canvas.noStroke();
        canvas.rect(0, 0, width, height);
        canvas.stroke(255, 255, 255);
        canvas.strokeWeight(2);

        drawMappedZone(leftFrontZone);
        drawMappedZone(rightFrontZone);
        drawMappedZone(leftBackZone);
        drawMappedZone(rightBackZone);
        drawCenterZone();

        drawPosition(position);
        canvas.popMatrix();
        canvas.popStyle();
    }

    void drawPosition(PVector position) {
        float mappedPositionX = map(position.x, left, right, 0, width);
        float mappedPositionZ = map(position.z, front, back, 0, height);
        currentCanvas.noStroke();
        currentCanvas.fill(250, 185, 0);
        currentCanvas.ellipse(mappedPositionX, mappedPositionZ, 25, 25);
    }

    private void drawCenterZone() {
        float mappedVerticalCenterRadius = currentCanvas.min(
                height,
                map(centerRadius, 0, realWorldDepth, 0, height) * 2);

        float mappedHorizontalCenterRadius = currentCanvas.min(
                width,
                map(centerRadius, 0, realWorldWidth, 0, width) * 2);

        float mappedCenterX = map(center.x, left, right, 0, width);
        float mappedCenterZ = map(center.z, front, back, 0, height);
        setFill(centerZone);
        currentCanvas.ellipse(mappedCenterX, mappedCenterZ, mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
    }

    public void drawMappedZone(Stage.RectStageZone stageZone) {
        PVector leftBottomFront = stageZone.getLeftBottomFront();

        float mappedX = currentCanvas.min(halfWidth,
                map(leftBottomFront.x, stageBounds.getLeft(), stageBounds.getRight(), 0, width - 2)); // leave room for the bounds.
        float mappedY = currentCanvas.min(halfHeight,
                map(leftBottomFront.z, stageBounds.getFront(), stageBounds.getBack(), 0, height - 2));

        float mappedWidth = currentCanvas.min(halfWidth,
                map(stageZone.getWidth(), 0, realWorldWidth, 0, width));
        float mappedDepth = currentCanvas.min(halfHeight,
                map(stageZone.getDepth(), 0, realWorldDepth, 0, height));

        setFill(stageZone);
        currentCanvas.rect(
                mappedX,
                mappedY,
                currentCanvas.max(mappedWidth, halfWidth),
                currentCanvas.max(mappedDepth, halfHeight));
    }

    void setFill(Stage.StageZone stageZone) {
        if (stageZone == this.currentStageZone) {
            currentCanvas.fill(0, 255, 0, alpha);
        } else {
            currentCanvas.fill(100, alpha);
        }
    }

    public static final float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }
}
