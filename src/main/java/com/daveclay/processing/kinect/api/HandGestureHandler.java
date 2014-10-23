package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandGestureHandler implements HandAware {

    private SimpleOpenNI kinect;
    private HandGesturesImpl handDataImpl;

    public static HandGestures init(SimpleOpenNI kinect) {
        HandGestureHandler handGestures = new HandGestureHandler(kinect);
        return handGestures.handDataImpl;
    }

    public HandGestureHandler(SimpleOpenNI simpleOpenNI) {
        kinect = simpleOpenNI;
        kinect.enableDepth();
        kinect.enableHand(this);
        handDataImpl = new HandGesturesImpl(simpleOpenNI);
    }

    public void setHandPositionsToTrack(int handPositionsToTrack) {
        handDataImpl.handPositionsToTrack = handPositionsToTrack;
    }

    public void useWaveGesture() {
        kinect.startGesture(SimpleOpenNI.GESTURE_WAVE);
    }

    public void onNewHand(SimpleOpenNI curContext, int handId, PVector pos) {
        System.out.println("onNewHand - handId: " + handId + ", pos: " + pos);
        kinect.convertRealWorldToProjective(pos, pos);
        ArrayList<PVector> vecList = new ArrayList<PVector>();
        vecList.add(pos);
        handDataImpl.handPathList.put(handId, vecList);
    }

    public void onTrackedHand(SimpleOpenNI curContext, int handId, PVector pos) {
        //println("onTrackedHand - handId: " + handId + ", pos: " + pos );
        kinect.convertRealWorldToProjective(pos, pos);
        ArrayList<PVector> vecList = handDataImpl.handPathList.get(handId);
        if (vecList != null) {
            vecList.add(0, pos);
            if (vecList.size() >= handDataImpl.handPositionsToTrack) {
                vecList.remove(vecList.size() - 1);
            }
        }
    }

    public void onLostHand(SimpleOpenNI curContext, int handId) {
        System.out.println("onLostHand - handId: " + handId);
        handDataImpl.handPathList.remove(handId);
    }

    public void onCompletedGesture(SimpleOpenNI curContext, int gestureType, PVector pos) {
        System.out.println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
        kinect.startTrackingHand(pos);
        int handId = kinect.startTrackingHand(pos);
        System.out.println("Gesture completed for hand " + handId);
    }

    public static class HandGesturesImpl implements HandGestures {

        private final SimpleOpenNI kinect;
        private int handPositionsToTrack = 30;
        private Map<Integer, ArrayList<PVector>> handPathList = new HashMap<Integer, ArrayList<PVector>>();

        public HandGesturesImpl(SimpleOpenNI kinect) {
            this.kinect = kinect;
        }

        public void setHandPositionsToTrack(int handPositionsToTrack) {
            this.handPositionsToTrack = handPositionsToTrack;
        }

        public void useWaveGesture() {
            kinect.startGesture(SimpleOpenNI.GESTURE_WAVE);
        }

        public List<PVector> getAllCurrentHandPositions() {
            List<PVector> positions = new ArrayList<PVector>();
            for (int hand : handPathList.keySet()) {
                PVector currentHandPosition = getCurrentHandPosition(hand);
                positions.add(currentHandPosition);
            }
            return positions;
        }

        public Collection<ArrayList<PVector>> getAllHands() {
            return handPathList.values();
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
    }
}