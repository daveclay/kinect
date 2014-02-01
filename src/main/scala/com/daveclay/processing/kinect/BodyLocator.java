package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.InfoSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.StageBounds;
import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 _newUserMethod          = getMethodRef(obj,"onNewUser",new Class[] { SimpleOpenNI.class,int.class });
 _lostUserMethod         = getMethodRef(obj,"onLostUser",new Class[] { SimpleOpenNI.class,int.class });
 _outOfSceneUserMethod   = getMethodRef(obj,"onOutOfSceneUser",new Class[] { SimpleOpenNI.class,int.class });
 _visibleUserMethod      = getMethodRef(obj,"onVisibleUser",new Class[] { SimpleOpenNI.class,int.class });
 */
public class BodyLocator extends PApplet {

    public static void main(String[] args) {
        InfoSketch infoSketch = new InfoSketch();
        BodyLocator bodyLocator = new BodyLocator(infoSketch);
        SketchRunner.run(infoSketch, bodyLocator);

        infoSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(infoSketch.getWidth() + 10, 100);
    }

    SimpleOpenNI  kinect;
    Box leftHandBox;
    Box rightHandBox;

    Integer currentlyTrackingUserId = -1;

    PVector centerOfMass = new PVector();

    PVector leftHandPosition3d = new PVector();
    PVector rightHandPosition3d = new PVector();
    PVector leftHandPosition2d = new PVector();
    PVector rightHandPosition2d = new PVector();

    StageBounds stageBounds = new StageBounds();

    InfoSketch infoSketch;

    public BodyLocator(InfoSketch infoSketch) {
        this.infoSketch = infoSketch;
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

        leftHandBox = new Box();
        leftHandBox.color = color(255, 120, 0);

        rightHandBox = new Box();
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
            infoSketch.logVector("CoM", centerOfMass);
            infoSketch.logVector("Center", stageBounds.getCenter());
            infoSketch.logRoundedFloat("Left", stageBounds.getLeftmost());
            infoSketch.logRoundedFloat("Right", stageBounds.getRightmost());
            infoSketch.logRoundedFloat("Nearest", stageBounds.getNearest());
            infoSketch.logRoundedFloat("Furthest", stageBounds.getFurthest());
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
    }

    void drawLineBetweenHands() {
        pushMatrix();
        //rotateY(radians(180));
        stroke(120);
        strokeWeight(2);
        line(leftHandPosition2d.x, leftHandPosition2d.y,
                rightHandPosition2d.x, rightHandPosition2d.y);
        leftHandBox.drawAt(leftHandPosition2d);
        rightHandBox.drawAt(rightHandPosition2d);
        popMatrix();

    }

    class Box {

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
}


