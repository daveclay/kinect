package com.daveclay.processing.gestures;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class GestureRecorder {

    private final GestureRecognizer recognizer;
    private boolean recording = false;
    private List<Point2D> points = new ArrayList<Point2D>();
    private GestureRecognizedHandler gestureRecognizedHandler;
    private int gesturePointCountThreshold = 30;
    private int testNumber;

    public GestureRecorder(GestureRecognizer gestureRecognizer) {
        this.recognizer = gestureRecognizer;
    }

    public void addPoint(PVector position) {
        addPoint(position.x, position.y);
    }

    public void addPoint(float x, float y) {
        if (recording) {
            points.add(new Point2D(x, y));
        }
    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
        if (gestureRecognizedHandler != null) {
            if (points.size() > this.gesturePointCountThreshold) {
                dumpPoints(points);
                RecognitionResult result = recognizer.recognize(points);
                gestureRecognizedHandler.gestureRecognized(result);
            } else {
                gestureRecognizedHandler.gestureWasNotRecognized("Not Enough Data Points (" + points.size() + ")");
            }
        }
        points.clear();
    }

    public void onGestureRecognized(GestureRecognizedHandler gestureRecognizedHandler) {
        this.gestureRecognizedHandler = gestureRecognizedHandler;
    }

    private void dumpPoints(List<Point2D> points) {
        GestureDataStore gestureDataStore = new GestureDataStore(GestureDataStore.GESTURE_DIR + "../recorded/");
        testNumber++;
        gestureDataStore.save(new GestureData("ActualGesture" + testNumber, points));
    }
}
