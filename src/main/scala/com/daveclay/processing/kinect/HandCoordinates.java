package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class HandCoordinates extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandCoordinates.class.getName());
    }

    private SimpleOpenNI kinect;
    private List<PVector> handPositions = new ArrayList<PVector>();

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.setMirror(true);

        kinect.enableRGB();
        size(kinect.rgbWidth(), kinect.rgbHeight(), OPENGL);

        kinect.enableDepth();
        kinect.enableHand();
        kinect.startGesture(SimpleOpenNI.GESTURE_WAVE);
    }

    public void draw() {
        kinect.update();

        image(kinect.rgbImage(), 0, 0);

        fill(color(255, 0, 0));
        for (PVector hand : handPositions) {
            rect(hand.x, hand.y, 1, 1);
        }
    }


    public void onNewHand(SimpleOpenNI curContext, int handId, PVector pos) {
        println("onNewHand - handId: " + handId + ", pos: " + pos);
        kinect.convertRealWorldToProjective(pos, pos);
        handPositions.add(pos);
    }

    public void onTrackedHand(SimpleOpenNI curContext, int handId, PVector pos) {
        // println("onTrackedHand - handId: " + handId + ", pos: " + pos );
        kinect.convertRealWorldToProjective(pos, pos);
        handPositions.add(pos);
    }

    public void onLostHand(SimpleOpenNI curContext, int handId) {
        println("onLostHand - handId: " + handId);
        handPositions.clear();
    }


    public void onCompletedGesture(SimpleOpenNI curContext, int gestureType, PVector pos) {
        println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);

        kinect.startTrackingHand(pos);

        int handId = kinect.startTrackingHand(pos);
        println("Gesture completed for hand " + handId);
    }
}
