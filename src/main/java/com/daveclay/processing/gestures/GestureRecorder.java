package com.daveclay.processing.gestures;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class GestureRecorder {

    private GeometricRecognizer recognizer = new GeometricRecognizer();
    private boolean recording = false;
    private List<Point2D> points = new ArrayList<Point2D>();
    private GestureRecognizedHandler gestureRecognizedHandler;
    private int gesturePointCountThreshold = 50;
    private int testNumber;

    public GestureRecorder(GeometricRecognizer geometricRecognizer) {
        this.recognizer = geometricRecognizer;
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
        if (gestureRecognizedHandler != null && points.size() > this.gesturePointCountThreshold) {
            dumpPoints(points);
            RecognitionResult result = recognizer.recognize(points);
            gestureRecognizedHandler.gestureRecognized(result);
        }
        points.clear();
    }

    public void onGestureRecognized(GestureRecognizedHandler gestureRecognizedHandler) {
        this.gestureRecognizedHandler = gestureRecognizedHandler;
    }

    private void dumpPoints(List<Point2D> points) {
        GestureData gestureData = new GestureData(GestureData.GESTURE_DIR + "../recorded/");
        testNumber++;
        gestureData.save(new GestureTemplate("ActualGesture" + testNumber, points));
    }
}
