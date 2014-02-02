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
            logSketch.logVector("Center", stageBounds.getCenter());
            /*
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
        translate(width, 0);


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

    public static class StageMonitor extends PApplet {

        private final Stage stage;
        private final LogSketch logSketch;
        private final PVector position;
        private final int width;
        private final int height;
        private final StageBounds stageBounds;
        private final Stage.CenterZone centerZone;
        private final Stage.LeftFrontZone leftFrontZone;
        private final Stage.RightFrontZone rightFrontZone;
        private final Stage.LeftBackZone leftBackZone;
        private final Stage.RightBackZone rightBackZone;

        public StageMonitor(Stage stage,
                            PVector position,
                            LogSketch logSketch,
                            int width,
                            int height) {
            this.width = width;
            this.height = height;
            this.logSketch = logSketch;
            this.position = position;
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
                            PVector position) {
            this(stage, position, logSketch, 400, 400);
        }

        @Override
        public void setup() {
            size(width, height);
        }

        @Override
        public void draw() {
            // real-life values:
            float left = stageBounds.getLeft();
            float right = stageBounds.getRight();
            float front = stageBounds.getFront();
            float back = stageBounds.getBack();
            float realWorldWidth = stageBounds.getWidth();
            float realWorldDepth = stageBounds.getDepth();
            float centerRadius = centerZone.getCenterRadius();
            PVector center = stageBounds.getCenter();

            logSketch.log("Within Center", centerZone.isWithinBounds(position));
            logSketch.log("Within Left Front", leftFrontZone.isWithinBounds(position));
            logSketch.log("Within Right Front", rightFrontZone.isWithinBounds(position));
            logSketch.log("Within Left Back", leftBackZone.isWithinBounds(position));
            logSketch.log("Within Right Back", rightBackZone.isWithinBounds(position));

            // mapped values:
            float mappedVerticalCenterRadius = map(centerRadius, 0, realWorldDepth, 0, height);
            float mappedHorizontalCenterRadius = map(centerRadius, 0, realWorldWidth, 0, width);
            float mappedPositionX = map(position.x, left, right, 0, width);
            float mappedPositionZ = map(position.z, front, back, 0, height);
            float mappedCenterX = map(center.x, left, right, 0, width);
            float mappedCenterZ = map(center.z, front, back, 0, height);

            background(100);
            stroke(255, 255, 255);

            if (stage.isWithinCenter(position)) {
                fill(0, 255, 0);
            } else {
                fill(255, 0, 0);
            }
            ellipse(mappedCenterX, mappedCenterZ, mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
            strokeWeight(2);
            rect(mappedPositionX, mappedPositionZ, 10, 10);
        }
    }

    public void drawMappedZone(Stage.RectStageZone stageZone) {
        PVector leftBottomFront = stageZone.getLeftBottomFront();
        PVector rightTopBack = stageZone.getRightTopBack();
    }
}


