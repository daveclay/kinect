package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


import java.util.HashMap;
import java.util.Map;

public class HandHolding3DBall extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandHolding3DBall.class.getName());
    }

    private SimpleOpenNI kinect;
    private Map<Integer, PVector> hands = new HashMap<Integer, PVector>();

    private float max = 66;
    private float min = 14;

    private int count = 0;

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

        background(0);
        PImage image = kinect.rgbImage();
        background(image);

        for (PVector hand : hands.values()) {
            pushMatrix();
            fill(color(0, 255, 0));
            rect(hand.x, hand.y, 10, 10);
            popMatrix();

            pushMatrix();
            noStroke();
            smooth();
            translate(hand.x, hand.y, 0);
            fill(color(255, 0, 0));
            lights();

            float size = map(hand.z, 480, 3200, max, min);
            if (size < 1) {
                size = 1;
            }
            sphere(size);
            popMatrix();
        }

        String shit = count + "";
        while (shit.length() < 3) {
            shit = "0" + shit;
        }
        saveFrame("/Users/daveclay/Desktop/out/ball " + shit + ".tif");
        count++;
    }


    public void onNewHand(SimpleOpenNI curContext, int handId, PVector pos) {
        println("onNewHand - handId: " + handId + ", pos: " + pos);
        kinect.convertRealWorldToProjective(pos, pos);
        hands.put(handId, pos);
    }

    public void onTrackedHand(SimpleOpenNI curContext, int handId, PVector pos) {
        // println("onTrackedHand - handId: " + handId + ", pos: " + pos );
        kinect.convertRealWorldToProjective(pos, pos);
        hands.put(handId, pos);
    }

    public void onLostHand(SimpleOpenNI curContext, int handId) {
        println("onLostHand - handId: " + handId);
        hands.remove(handId);
    }


    public void onCompletedGesture(SimpleOpenNI curContext, int gestureType, PVector pos) {
        println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);

        kinect.startTrackingHand(pos);

        int handId = kinect.startTrackingHand(pos);
        println("Gesture completed for hand " + handId);
    }

    public void keyPressed() {
        switch(keyCode)
        {
            case LEFT:
                max -= 2;
                println("max: " + max);
                break;
            case RIGHT:
                max += 2;
                println("max: " + max);
                break;
            case UP:
                min += 1;
                println("min: " + min);
                break;
            case DOWN:
                min -= 1;
                println("min: " + min);
                break;
        }
    }
}
