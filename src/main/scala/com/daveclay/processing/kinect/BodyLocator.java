package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.GeometricRecognizer;
import com.daveclay.processing.gestures.GestureData;
import com.daveclay.processing.gestures.Point2D;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.SingleUserTrackingSketch;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.api.StageMonitor;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class BodyLocator extends SingleUserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();
        BodyLocator bodyLocator = new BodyLocator(logSketch);
        StageMonitor stageMonitor = new StageMonitor(
                bodyLocator.getStage(),
                logSketch,
                bodyLocator.getUser());

        SketchRunner.run(logSketch, bodyLocator, stageMonitor);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
        stageMonitor.frame.setLocation(100, logSketch.getHeight() + 10);
    }

    HandBox leftHandBox;
    HandBox rightHandBox;
    Stage stage;
    GeometricRecognizer geometricRecognizer = new GeometricRecognizer();
    {
        geometricRecognizer.addTemplate("Rectangle", GestureData.getGestureRectangle());
        geometricRecognizer.addTemplate("V", GestureData.getGestureV());
        geometricRecognizer.addTemplate("Circle", GestureData.getGestureCircle());
        geometricRecognizer.addTemplate("Caret", GestureData.getGestureCaret());
    }
    GestureRecorder gestureRecorder = new GestureRecorder(geometricRecognizer);

    public static class GestureRecorder {
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

    public static interface GestureRecognizedHandler {
        public void gestureRecognized(RecognitionResult gesture);
    }

    public BodyLocator(LogSketch logSketch) {
        this.stage = new Stage();
        stage.setupDefaultStageZones();
        this.logSketch = logSketch;
        user.setLogSketch(logSketch);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    protected void configureKinect(SimpleOpenNI kinect) {
        kinect.enableRGB();
        kinect.setMirror(true);
        kinect.alternativeViewPointDepthToImage();
    }

    @Override
    protected void setupUserTrackingSketch() {
        leftHandBox = new HandBox();
        leftHandBox.color = color(255, 120, 0);

        rightHandBox = new HandBox();
        rightHandBox.color = color(0, 80, 255);
        size(640, 480, OPENGL);

        onLeftHandExtended(new HandExtendedHandler() {
            @Override
            public void onHandExtended() {
                //logSketch.log("Left Hand Gesture", "Extended.");
                gestureRecorder.startRecording();
            }

            @Override
            public void onHandRetracted() {
                // logSketch.log("Left Hand Gesture", "Retracted.");
                gestureRecorder.stopRecording();
            }
        });

        gestureRecorder.onGestureRecognized(new GestureRecognizedHandler() {
            @Override
            public void gestureRecognized(RecognitionResult gesture) {
                logSketch.log("Gesture", gesture.name + " " + gesture.score);
            }
        });
    }

    @Override
    protected void drawUserTrackingSketch() {
        setKinectRGBImageAsBackground();
        if (user.isCurrentlyTracking()) {
            user.convertRealWorld3DToProjective2D();
            gestureRecorder.addPoint(user.getLeftHandPosition3d());
            drawLineBetweenHands();
            drawDebugInfo();
        }
    }

    public void drawDebugInfo() {
        logSketch.logVector("CoM", user.getCenterOfMass());
        logSketch.logVector("Left Hand", user.getLeftHandPositionMirrored2D());
        logSketch.logVector("Right Hand", user.getRightHandPositionMirrored2D());
    }

    void drawLineBetweenHands() {
        pushMatrix();
        translate(width, 0); // we mirrored the view, so the 2d coordinates need a new origin.
        stroke(120);
        strokeWeight(2);
        PVector leftHandPosition2d = user.getLeftHandPositionMirrored2D();
        PVector rightHandPosition2d = user.getRightHandPositionMirrored2D();
        line(leftHandPosition2d.x, leftHandPosition2d.y,
                rightHandPosition2d.x, rightHandPosition2d.y);
        leftHandBox.drawAt(leftHandPosition2d);
        rightHandBox.drawAt(rightHandPosition2d);
        popMatrix();
    }

    class HandBox {

        PVector center;
        int color;
        int size = 20;
        int alpha = 255;

        void drawAt(PVector position) {
            center = position;
            strokeWeight(2);
            fill(red(color), blue(color), green(color), (int) (alpha * .03));
            stroke(red(color), blue(color), green(color), alpha);
            rect(center.x, center.y, size, size);
        }
    }
}


