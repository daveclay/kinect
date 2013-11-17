package com.daveclay.processing.kinect.api;

import SimpleOpenNI.*;
import processing.core.PVector;

public interface HandAware {
    public void onNewHand(SimpleOpenNI kinect, int handId, PVector pos);
    public void onTrackedHand(SimpleOpenNI kinect, int handId, PVector pos);
    public void onLostHand(SimpleOpenNI kinect, int handId);
    public void onCompletedGesture(SimpleOpenNI kinect, int gestureType, PVector pos);
}
