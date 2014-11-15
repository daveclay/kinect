package com.daveclay.processing.kinect.bodylocator;

import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.Stage;
import processing.core.PVector;

public interface BodyLocatorListener {

    void gestureWasRecognized(RecognitionResult gesture);

    void userDidEnteredZone(Stage.StageZone stageZone);

    void userDidMove(PVector position);
}
