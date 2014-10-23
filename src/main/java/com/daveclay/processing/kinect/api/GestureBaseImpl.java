package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;

public abstract class GestureBaseImpl implements Gesture {

    @Override
    public void onNewGesture(SimpleOpenNI kinect, int gestureType) {
    }

    @Override
    public void onCompletedGesture(SimpleOpenNI kinect, int gestureType, PVector pos) {
    }

    @Override
    public void onAbortedGesture(SimpleOpenNI kinect, int gestureType) {
    }

    @Override
    public void onProgressGesture(SimpleOpenNI kinect, int gestureType) {
    }
}
