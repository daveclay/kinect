package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.GeometricRecognizer;
import com.daveclay.processing.gestures.GestureData;
import com.daveclay.processing.gestures.GestureRecognizedHandler;
import com.daveclay.processing.gestures.GestureRecorder;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.SingleUserTrackingSketch;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.api.StageMonitor;
import processing.core.PVector;

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
        geometricRecognizer.addTemplate("Circle", GestureData.getGestureCircle());
        geometricRecognizer.addTemplate("Caret", GestureData.getGestureCaret());
    }
    GestureRecorder gestureRecorder = new GestureRecorder(geometricRecognizer);
    boolean drawing;

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
                drawing = true;
            }

            @Override
            public void onHandRetracted() {
                // logSketch.log("Left Hand Gesture", "Retracted.");
                gestureRecorder.stopRecording();
                drawing = false;
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
            gestureRecorder.addPoint(user.leftHand.position);
            drawLineBetweenHands();
        }
    }

    void drawLineBetweenHands() {
        pushMatrix();
        translate(width, 0); // we mirrored the view, so the 2d coordinates need a new origin.
        PVector leftHandPosition2d = user.convertRealWorldToProjectiveMirrored(user.leftHand);
        PVector rightHandPosition2d = user.convertRealWorldToProjectiveMirrored(user.rightHand);

        logSketch.logVector("CoM", user.centerOfMass);
        logSketch.logVector("Left Hand", leftHandPosition2d);
        logSketch.logVector("Right Hand", rightHandPosition2d);

        /*
        if (drawing) {
            stroke(2);
            fill(255, 100, 0);
            ellipse(leftHandPosition2d.x, leftHandPosition2d.y, 10, 10);
        }
        */

        stroke(120);
        strokeWeight(2);
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


