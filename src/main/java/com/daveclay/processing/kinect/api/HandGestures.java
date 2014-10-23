package com.daveclay.processing.kinect.api;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface HandGestures {

    void useWaveGesture();

    List<PVector> getAllCurrentHandPositions();

    Collection<ArrayList<PVector>> getAllHands();

    void useRaisedHand();

    PVector getCurrentHandPosition(int handId);

    ArrayList<PVector> getHandPositions(int handId);
}
