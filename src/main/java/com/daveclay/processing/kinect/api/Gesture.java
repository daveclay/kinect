package com.daveclay.processing.kinect.api;

import SimpleOpenNI.*;
import processing.core.PVector;

public interface Gesture {
    public void onNewGesture(SimpleOpenNI kinect, int gestureType);
    public void onProgressGesture(SimpleOpenNI kinect, int gestureType);
    public void onAbortedGesture(SimpleOpenNI kinect, int gestureType);
    public void onCompletedGesture(SimpleOpenNI kinect, int gestureType, PVector pos);
}
