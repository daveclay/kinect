package com.daveclay.processing.gestures;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class GestureRecorder {

    private GeometricRecognizer recognizer = new GeometricRecognizer();
    private boolean recording = false;
    private List<Point2D> points = new ArrayList<Point2D>();
    private GestureRecognizedHandler gestureRecognizedHandler;

    public GestureRecorder(GeometricRecognizer geometricRecognizer) {
        this.recognizer = geometricRecognizer;
    }

    public void addPoint(PVector position) {
        addPoint(position.x, position.y);
    }

    public void addPoint(double x, double y) {
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
            RecognitionResult result = recognizer.recognize(points);
            gestureRecognizedHandler.gestureRecognized(result);
        }
        points.clear();
    }

    public void onGestureRecognized(GestureRecognizedHandler gestureRecognizedHandler) {
        this.gestureRecognizedHandler = gestureRecognizedHandler;
    }
}
