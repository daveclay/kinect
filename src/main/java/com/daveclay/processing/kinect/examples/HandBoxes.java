package com.daveclay.processing.kinect.examples;

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
public class HandBoxes extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandBoxes.class.getName());
    }

    SimpleOpenNI  kinect;
    Box leftHandBox;
    Box rightHandBox;

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
        kinect.enableUser();

        kinect.alternativeViewPointDepthToImage();

        size(640, 480, OPENGL);
        stroke(255, 0, 0);
        strokeWeight(5);

        leftHandBox = new Box();
        leftHandBox.color = color(255, 0, 0);

        rightHandBox = new Box();
        rightHandBox.color = color(0, 0, 255);
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
            textSize(11);
            fill(0, 255, 0);
            text("Z:" + rightPosition.z, 10, 10);
            popMatrix();
        }
    }

    public void drawBoxes() {
        pushMatrix();
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (kinect.isTrackingSkeleton(userId)) {
                drawLineBetweenHands(userId, SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_RIGHT_HAND);
            }
        }
        popMatrix();
    }

    void drawLineBetweenHands(int userId, int joint1, int joint2) {
        PVector joint1Pos = new PVector();
        PVector joint2Pos = new PVector();

        kinect.getJointPositionSkeleton(userId, joint1, joint1Pos);
        kinect.getJointPositionSkeleton(userId, joint2, joint2Pos);

        PVector joint1Pos2d = new PVector();
        PVector joint2Pos2d = new PVector();

        // switch from 3D "real world" to 2D "projection"
        kinect.convertRealWorldToProjective(joint1Pos, joint1Pos2d);
        kinect.convertRealWorldToProjective(joint2Pos, joint2Pos2d);

        pushMatrix();
        stroke(255, 0, 0);
        strokeWeight(5);
        line(joint1Pos2d.x, joint1Pos2d.y,
                joint2Pos2d.x, joint2Pos2d.y);
        leftHandBox.drawAt(joint1Pos2d);
        rightHandBox.drawAt(joint2Pos2d);
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


