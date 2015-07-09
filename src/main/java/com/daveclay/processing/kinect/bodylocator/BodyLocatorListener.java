package com.daveclay.processing.kinect.bodylocator;

import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StagePosition;

public interface BodyLocatorListener {

    void gestureWasRecognized(User user, RecognitionResult gesture);

    void userDidEnteredZone(User user,Stage.StageZone stageZone);

    void userDidMove(User user, StagePosition stagePosition);
}
