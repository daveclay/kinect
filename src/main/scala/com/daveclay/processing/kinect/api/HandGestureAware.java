package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public abstract class HandGestureAware extends PApplet implements Hand {

    private SimpleOpenNI kinect;
    private int handVecListSize = 30;
    private Map<Integer, ArrayList<PVector>> handPathList = new HashMap<Integer, ArrayList<PVector>>();

    public HandGestureAware(SimpleOpenNI simpleOpenNI) {
        kinect = simpleOpenNI;
        kinect.enableDepth();
        kinect.enableHand();
    }

    public void useWaveGesture() {
        kinect.startGesture(SimpleOpenNI.GESTURE_WAVE);
    }

    public void useRaisedHand() {
        kinect.startGesture(SimpleOpenNI.GESTURE_HAND_RAISE);
    }

    public PVector getCurrentHandPosition(int handId) {
        ArrayList<PVector> positions = getHandPositions(handId);
        if (positions != null) {
            return positions.get(0);
        }
        return null;
    }

    public ArrayList<PVector> getHandPositions(int handId) {
        return handPathList.get(handId);
    }

    public void onNewHand(SimpleOpenNI curContext, int handId, PVector pos) {
        System.out.println("onNewHand - handId: " + handId + ", pos: " + pos);
        ArrayList<PVector> vecList = new ArrayList<PVector>();
        vecList.add(pos);
        handPathList.put(handId, vecList);
    }

    public void onTrackedHand(SimpleOpenNI curContext, int handId, PVector pos) {
        //println("onTrackedHand - handId: " + handId + ", pos: " + pos );
        ArrayList<PVector> vecList = handPathList.get(handId);
        if (vecList != null) {
            vecList.add(0, pos);
            if (vecList.size() >= handVecListSize) {
                vecList.remove(vecList.size() - 1);
            }
        }
    }

    public void onLostHand(SimpleOpenNI curContext, int handId) {
        System.out.println("onLostHand - handId: " + handId);
        handPathList.remove(handId);
    }

    public void onCompletedGesture(SimpleOpenNI curContext, int gestureType, PVector pos) {
        System.out.println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
        kinect.startTrackingHand(pos);
        int handId = kinect.startTrackingHand(pos);
        println("Gesture completed for hand " + handId);
    }
}