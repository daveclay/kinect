package com.daveclay.processing.kinect.examples;

import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 _newUserMethod          = getMethodRef(obj,"onNewUser",new Class[] { SimpleOpenNI.class,int.class });
 _lostUserMethod         = getMethodRef(obj,"onLostUser",new Class[] { SimpleOpenNI.class,int.class });
 _outOfSceneUserMethod   = getMethodRef(obj,"onOutOfSceneUser",new Class[] { SimpleOpenNI.class,int.class });
 _visibleUserMethod      = getMethodRef(obj,"onVisibleUser",new Class[] { SimpleOpenNI.class,int.class });
 */
public class JointDistance extends PApplet {

    public static void main(String[] args) {
        PApplet.main(JointDistance.class.getName());
    }

    SimpleOpenNI  kinect;

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
        kinect.enableUser();

        size(640, 480);
        stroke(255, 0, 0);
        strokeWeight(5);
    }

    public void draw() {
        kinect.update();
        background(kinect.rgbImage());

        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (kinect.isTrackingSkeleton(userId)) {
                PVector leftHand = new PVector();
                PVector rightHand = new PVector();

                kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
                kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);

                // calculate difference by subtracting one vector from another
                PVector differenceVector = PVector.sub(leftHand, rightHand);
                // calculate the distance and direction
                // of the difference vector
                float magnitude = differenceVector.mag();
                differenceVector.normalize();
                // draw a line between the two handsst
                kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_RIGHT_HAND);
                // display
                pushMatrix();
                scale(4);
                fill(differenceVector.x * 255, differenceVector.y * 255, differenceVector.z * 255);
                text("m: " + magnitude, 5, 10);
                popMatrix();
            }
        }
    }

    public void onNewUser(SimpleOpenNI curContext, int userId)
    {
        println("onNewUser - userId: " + userId);
        println("\tstart tracking skeleton");

        kinect.startTrackingSkeleton(userId);
    }
}


