package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.SingleUserTrackingSketch;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.api.StageMonitor;
import processing.core.PVector;

public class BodyLocator extends SingleUserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();
        BodyLocator bodyLocator = new BodyLocator(logSketch);
        StageMonitor stageMonitor = new StageMonitor(
                bodyLocator.getStage(),
                logSketch,
                bodyLocator.getUser());

        SketchRunner.run(logSketch, bodyLocator, stageMonitor);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
        stageMonitor.frame.setLocation(100, logSketch.getHeight() + 10);
    }

    HandBox leftHandBox;
    HandBox rightHandBox;
    Stage stage;

    public BodyLocator(LogSketch logSketch) {
        this.stage = new Stage();
        stage.setupDefaultStageZones();
        this.logSketch = logSketch;
        user.setLogSketch(logSketch);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    protected void configureKinect(SimpleOpenNI kinect) {
        kinect.enableRGB();
        kinect.setMirror(true);
        kinect.alternativeViewPointDepthToImage();
    }

    @Override
    protected void setupUserTrackingSketch() {
        leftHandBox = new HandBox();
        leftHandBox.color = color(255, 120, 0);

        rightHandBox = new HandBox();
        rightHandBox.color = color(0, 80, 255);
        size(640, 480, OPENGL);
    }

    @Override
    protected void drawUserTrackingSketch() {
        setKinectRGBImageAsBackground();
        drawLineBetweenHands();
        drawDebugInfo();
    }

    public void drawDebugInfo() {
        if (user.isCurrentlyTracking()) {
            logSketch.logVector("CoM", user.getCenterOfMass());
            logSketch.logVector("Left Hand", user.getLeftHandPositionMirrored2D());
            logSketch.logVector("Right Hand", user.getRightHandPositionMirrored2D());
            /*
            StageBounds stageBounds = stage.getStageBounds();
            logSketch.logVector("Center", stageBounds.getCenter());
            logSketch.logRoundedFloat("Left", stageBounds.getLeft());
            logSketch.logRoundedFloat("Right", stageBounds.getRight());
            logSketch.logRoundedFloat("Nearest", stageBounds.getFront());
            logSketch.logRoundedFloat("Furthest", stageBounds.getBack());
            */
        }
    }

    void drawLineBetweenHands() {
        pushMatrix();
        translate(width, 0); // we mirrored the view, so the 2d coordinates need a new origin.
        stroke(120);
        strokeWeight(2);
        PVector leftHandPosition2d = user.getLeftHandPositionMirrored2D();
        PVector rightHandPosition2d = user.getRightHandPositionMirrored2D();
        line(leftHandPosition2d.x, leftHandPosition2d.y,
                rightHandPosition2d.x, rightHandPosition2d.y);
        leftHandBox.drawAt(leftHandPosition2d);
        rightHandBox.drawAt(rightHandPosition2d);
        popMatrix();
    }

    class HandBox {

        PVector center;
        int color;
        int size = 20;
        int alpha = 255;

        void drawAt(PVector position) {
            center = position;
            strokeWeight(2);
            fill(red(color), blue(color), green(color), (int) (alpha * .03));
            stroke(red(color), blue(color), green(color), alpha);
            rect(center.x, center.y, size, size);
        }
    }
}


