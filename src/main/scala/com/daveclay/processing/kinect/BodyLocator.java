package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.VectorMath;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.api.StageBounds;
import com.daveclay.processing.kinect.api.UserListener;
import processing.core.PApplet;
import processing.core.PVector;

public class BodyLocator extends PApplet implements UserListener {

    public static void main(String[] args) {

        LogSketch logSketch = new LogSketch();
        BodyLocator bodyLocator = new BodyLocator(logSketch);
        StageMonitor stageMonitor = new StageMonitor(
                bodyLocator.getStage(),
                logSketch,
                bodyLocator.getPosition());

        SketchRunner.run(logSketch, bodyLocator, stageMonitor);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
        stageMonitor.frame.setLocation(100, logSketch.getHeight() + 10);
    }

    SimpleOpenNI  kinect;
    HandBox leftHandBox;
    HandBox rightHandBox;

    Integer currentlyTrackingUserId = -1;

    PVector centerOfMass = new PVector();
    PVector leftHandPosition3d = new PVector();
    PVector rightHandPosition3d = new PVector();

    PVector leftHandPosition2d = new PVector();
    PVector rightHandPosition2d = new PVector();

    LogSketch logSketch;

    Stage stage;

    public BodyLocator(LogSketch logSketch) {
        this.stage = new Stage();
        stage.setupDefaultStageZones();

        this.logSketch = logSketch;
    }

    public Stage getStage() {
        return stage;
    }

    public PVector getPosition() {
        return centerOfMass;
    }

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
        kinect.enableUser();
        kinect.setMirror(true);

        kinect.alternativeViewPointDepthToImage();

        size(640, 480, OPENGL);
        stroke(255, 0, 0);
        strokeWeight(5);

        leftHandBox = new HandBox();
        leftHandBox.color = color(255, 120, 0);

        rightHandBox = new HandBox();
        rightHandBox.color = color(0, 80, 255);
    }

    public void draw() {
        kinect.update();
        background(kinect.rgbImage());
        drawBoxes();
        drawDebugInfo();
    }

    public void drawDebugInfo() {
        if (currentlyTrackingUserId != null) {
            logSketch.logVector("CoM", centerOfMass);
            StageBounds stageBounds = stage.getStageBounds();
            /*
            logSketch.logVector("Center", stageBounds.getCenter());
            logSketch.logRoundedFloat("Left", stageBounds.getLeft());
            logSketch.logRoundedFloat("Right", stageBounds.getRight());
            logSketch.logRoundedFloat("Nearest", stageBounds.getFront());
            logSketch.logRoundedFloat("Furthest", stageBounds.getBack());
            */
            logSketch.logVector("Left Hand", leftHandPosition2d);
            logSketch.logVector("Right Hand", rightHandPosition2d);
        }
    }

    public void drawBoxes() {
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (kinect.isTrackingSkeleton(userId) && userId == currentlyTrackingUserId) {
                determineVectorsForUser(userId);
                stage.updatePosition(centerOfMass);
                drawLineBetweenHands();
            }
        }
    }

    void determineVectorsForUser(int userId) {
        kinect.getCoM(userId, centerOfMass);

        kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHandPosition3d);
        kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandPosition3d);

        // switch from 3D "real world" to 2D "projection"
        kinect.convertRealWorldToProjective(leftHandPosition3d, leftHandPosition2d);
        kinect.convertRealWorldToProjective(rightHandPosition3d, rightHandPosition2d);

        leftHandPosition2d = VectorMath.reflectVertically(leftHandPosition2d);
        rightHandPosition2d = VectorMath.reflectVertically(rightHandPosition2d);
    }

    void drawLineBetweenHands() {
        pushMatrix();
        translate(width, 0); // we mirrored the view, so the 2d coordinates need a new origin.
        stroke(120);
        strokeWeight(2);
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

    public void onNewUser(SimpleOpenNI curContext, int userId) {
        println("onNewUser - userId: " + userId);
        println("\tstart tracking skeleton");

        this.currentlyTrackingUserId = userId;
        kinect.startTrackingSkeleton(userId);
    }

    public void onLostUser(SimpleOpenNI kinect, int userId) {
        if (userId == this.currentlyTrackingUserId) {
            this.currentlyTrackingUserId = null;
        }
    }

    public static class User {

        PVector centerOfMass = new PVector();
        PVector leftHandPosition3d = new PVector();
        PVector rightHandPosition3d = new PVector();

        public PVector getCenterOfMass() {
            return centerOfMass;
        }

        public PVector getLeftHandPosition3d() {
            return leftHandPosition3d;
        }

        public PVector getRightHandPosition3d() {
            return rightHandPosition3d;
        }
    }
}


