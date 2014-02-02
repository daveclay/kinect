package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.VectorMath;
import com.daveclay.processing.kinect.api.StageBounds;
import com.daveclay.processing.kinect.api.UserListener;
import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 _newUserMethod          = getMethodRef(obj,"onNewUser",new Class[] { SimpleOpenNI.class,int.class });
 _lostUserMethod         = getMethodRef(obj,"onLostUser",new Class[] { SimpleOpenNI.class,int.class });
 _outOfSceneUserMethod   = getMethodRef(obj,"onOutOfSceneUser",new Class[] { SimpleOpenNI.class,int.class });
 _visibleUserMethod      = getMethodRef(obj,"onVisibleUser",new Class[] { SimpleOpenNI.class,int.class });
 */
public class BodyLocator extends PApplet implements UserListener {

    public static void main(String[] args) {

        LogSketch logSketch = new LogSketch();
        BodyLocator bodyLocator = new BodyLocator(logSketch);
        StageMonitor stageMonitor = new StageMonitor(
                bodyLocator.getStageBounds(),
                logSketch,
                bodyLocator.getPosition());

        SketchRunner.run(logSketch, bodyLocator, stageMonitor);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
        stageMonitor.frame.setLocation(0, logSketch.getHeight() + 10);
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

    StageBounds stageBounds = new StageBounds();

    LogSketch logSketch;

    public BodyLocator(LogSketch logSketch) {
        this.logSketch = logSketch;
    }

    public StageBounds getStageBounds() {
        return stageBounds;
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
            logSketch.logVector("Center", stageBounds.getCenter());
            logSketch.logRoundedFloat("Left", stageBounds.getLeft());
            logSketch.logRoundedFloat("Right", stageBounds.getRight());
            logSketch.logRoundedFloat("Nearest", stageBounds.getFront());
            logSketch.logRoundedFloat("Furthest", stageBounds.getBack());
            logSketch.logVector("Left Hand", leftHandPosition2d);
            logSketch.logVector("Right Hand", rightHandPosition2d);
        }
    }

    public void drawBoxes() {
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (kinect.isTrackingSkeleton(userId) && userId == currentlyTrackingUserId) {
                determineVectorsForUser(userId);
                stageBounds.track(centerOfMass);
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
        private final StageBounds stageBounds;
        private final LogSketch logSketch;
        private final PVector position;
        private final int width;
        private final int height;

        public StageMonitor(StageBounds stageBounds,
                            PVector position,
                            LogSketch logSketch,
                            int width,
                            int height) {
            this.width = width;
            this.height = height;
            this.logSketch = logSketch;
            this.position = position;
            this.stageBounds = stageBounds;
        }

        public StageMonitor(StageBounds stageBounds,
                            LogSketch logSketch,
                            PVector position) {
            this(stageBounds, position, logSketch, 400, 400);
        }

        @Override
        public void setup() {
            size(width, height);
        }

        @Override
        public void draw() {
            background(100);

            float left = stageBounds.getLeft();
            float right = stageBounds.getRight();
            float front = stageBounds.getFront();
            float back = stageBounds.getBack();
            PVector center = stageBounds.getCenter();

            // TODO: draw the box proportionally?

            float mappedX = map(position.x, left, right, 0, width);
            float mappedZ = map(position.z, front, back, 0, height);

            logSketch.logRoundedFloat("mappedX", mappedX);
            logSketch.logRoundedFloat("mappedZ", mappedZ);

            float centerRadius = 200f;

            float mappedCenterX = map(center.x, left, right, 0, width);
            float mappedCenterZ = map(center.z, front, back, 0, height);

            float mappedCenterRadius = map(centerRadius, left, right, 0, width);

            stroke(255, 255, 255);
            ellipse(mappedCenterX, mappedCenterZ, mappedCenterRadius, mappedCenterRadius);

            if (VectorMath.isWithin(center, position, 200)) {
                fill(0, 255, 0);
            } else {
                fill(255, 0, 0);
            }

            strokeWeight(2);
            rect(mappedX, mappedZ, 10, 10);
        }
    }
}


