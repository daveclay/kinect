package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;

public abstract class HandAwareBaseImpl implements HandAware {

    @Override
    public void onNewHand(SimpleOpenNI kinect, int handId, PVector pos) {
    }

    @Override
    public void onCompletedGesture(SimpleOpenNI kinect, int gestureType, PVector pos) {
    }

    @Override
    public void onLostHand(SimpleOpenNI kinect, int handId) {
    }

    @Override
    public void onTrackedHand(SimpleOpenNI kinect, int handId, PVector pos) {
    }
}
