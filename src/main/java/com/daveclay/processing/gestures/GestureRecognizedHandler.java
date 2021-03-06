package com.daveclay.processing.gestures;

public interface GestureRecognizedHandler {
    public void gestureRecognized(RecognitionResult gesture);

    void gestureWasNotRecognized(String message);
}
