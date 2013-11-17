package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class HandGestureAware extends PApplet {

    private SimpleOpenNI context;
    private int handVecListSize = 30;
    private Map<Integer, ArrayList<PVector>> handPathList = new HashMap<Integer, ArrayList<PVector>>();

    public HandGestureAware(SimpleOpenNI simpleOpenNI) {
        context = simpleOpenNI;

        context.enableHand();
        context.startGesture(SimpleOpenNI.GESTURE_WAVE);
    }

    public void onNewHand(SimpleOpenNI curContext, int handId, PVector pos) {
        Hands.println("onNewHand - handId: " + handId + ", pos: " + pos);

        ArrayList<PVector> vecList = new ArrayList<PVector>();
        vecList.add(pos);

        handPathList.put(handId, vecList);
    }

    public void onTrackedHand(SimpleOpenNI curContext, int handId, PVector pos) {
        //println("onTrackedHand - handId: " + handId + ", pos: " + pos );

        ArrayList<PVector> vecList = handPathList.get(handId);
        if (vecList != null) {
            vecList.add(0, pos);
            if (vecList.size() >= handVecListSize)
            // remove the last point
            {
                vecList.remove(vecList.size() - 1);
            }
        }
    }

    public void onLostHand(SimpleOpenNI curContext, int handId) {
        Hands.println("onLostHand - handId: " + handId);

        handPathList.remove(handId);
    }

    public void onCompletedGesture(SimpleOpenNI curContext, int gestureType, PVector pos) {
        Hands.println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);

        context.startTrackingHand(pos);

        int handId = context.startTrackingHand(pos);
        Hands.println("hand stracked: " + handId);
    }
}