package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
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
        PApplet.main(BodyLocator.class.getName());
    }

    SimpleOpenNI  kinect;
    Box leftHandBox;
    Box rightHandBox;

    PVector centerOfMass = new PVector();
    PVector leftHandPosition3d = new PVector();
    PVector rightHandPosition3d = new PVector();

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
        kinect.enableUser();
        // kinect.setMirror(true);

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
        PVector rightPosition = rightHandBox.center;
        if (rightPosition != null) {
            pushMatrix();
            textSize(15);
            fill(0, 255, 0);
            text("3D y: " + rightHandPosition3d.y + "\n2D y:" + rightPosition.y, 450, 10);
            if (rightHandPosition3d.x > 20) {
                text("Previous Frame", 10, 10);
            } else if (rightHandPosition3d.x < -20) {
                text("Next Frame", 10, 10);
            }

            popMatrix();
        }
    }

    public void drawBoxes() {
        pushMatrix();
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (kinect.isTrackingSkeleton(userId)) {
                drawLineBetweenHands(userId);
            }
        }
        popMatrix();
    }

    void determineCenterOfMass(int userId) {
        kinect.getCoM(userId, centerOfMass);
    }

    void drawLineBetweenHands(int userId) {


        kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHandPosition3d);
        kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandPosition3d);

        PVector leftHandPosition2d = new PVector();
        PVector rightHandPosition2d = new PVector();

        // switch from 3D "real world" to 2D "projection"
        kinect.convertRealWorldToProjective(leftHandPosition3d, leftHandPosition2d);
        kinect.convertRealWorldToProjective(rightHandPosition3d, rightHandPosition2d);

        pushMatrix();
        stroke(120);
        strokeWeight(5);
        line(leftHandPosition2d.x, leftHandPosition2d.y,
                rightHandPosition2d.x, rightHandPosition2d.y);
        leftHandBox.drawAt(leftHandPosition2d);
        rightHandBox.drawAt(rightHandPosition2d);
        popMatrix();

    }

    class Box {

        PVector center;
        int color;
        int size = 100;

        void drawAt(PVector position) {
            center = position;
            fill(red(color), blue(color), green(color), 150);
            stroke(red(color), blue(color), green(color), 255);
            rect(center.x, center.y, 20, 20);
        }
    }

    public void onNewUser(SimpleOpenNI curContext, int userId)
    {
        println("onNewUser - userId: " + userId);
        println("\tstart tracking skeleton");

        kinect.startTrackingSkeleton(userId);
    }
}


