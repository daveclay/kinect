package com.daveclay.processing.kinect.bodylocator;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.*;
import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.kinect.api.SingleUserTrackingSketch;
import com.daveclay.processing.kinect.api.Stage;
import com.daveclay.processing.kinect.api.StageMonitor;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.server.presentation.PresentationServer;
import com.daveclay.server.presentation.PresentationWebSocketListener;
import processing.core.PVector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class BodyLocator extends SingleUserTrackingSketch {

    /**
     * To run this, the SimpleOpenNI.jar must be in the same distribution folder with the "osx" .so
     * native library (libraries?)
     */
    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();
        GestureDataStore gestureDataStore = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        gestureDataStore.load();

        User user = new User();
        Stage stage = new Stage();
        BodyLocator bodyLocator = new BodyLocator(
                user,
                gestureDataStore,
                stage,
                logSketch);

        StageMonitor stageMonitor = new StageMonitor(
                stage,
                logSketch,
                user);

        try {
            PresentationServer presentationServer = new PresentationServer(12345);
            presentationServer.start();
            PresentationWebSocketListener listener = new PresentationWebSocketListener(
                    presentationServer,
                    gestureDataStore);

            bodyLocator.setListener(listener);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        SketchRunner.run(logSketch, bodyLocator, stageMonitor);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
        stageMonitor.frame.setLocation(100, logSketch.getHeight() + 10);
    }

    Listener listener;
    HandBox leftHandBox;
    HandBox rightHandBox;
    Stage stage;
    AggregateGestureRecognizer gestureRecognizer = new AggregateGestureRecognizer();
    GestureRecorder gestureRecorder = new GestureRecorder(gestureRecognizer);

    List<PVector> drawingPoints = new ArrayList<PVector>();
    boolean drawing;

    public BodyLocator(User user, GestureDataStore gestureDataStore, Stage stage, LogSketch logSketch) {
        super(user);

        GeometricRecognizer geometricRecognizer = new GeometricRecognizer();
        geometricRecognizer.addTemplate("Circle", gestureDataStore.getPointsByName("Circle"));

        LineGestureRecognizer lineGestureRecognizer = new LineGestureRecognizer();
        lineGestureRecognizer.addRecognizerAlgorithm("LeftToRightLine", LineGestureRecognizer.LEFT_TO_RIGHT_LINE_RECOGNIZER);
        lineGestureRecognizer.addRecognizerAlgorithm("RightToLeftLine", LineGestureRecognizer.RIGHT_TO_LEFT_LINE_RECOGNIZER);

        gestureRecognizer.addRecognizer(geometricRecognizer);
        gestureRecognizer.addRecognizer(lineGestureRecognizer);

        this.stage = stage;
        stage.setupDefaultStageZones();
        this.logSketch = logSketch;
        user.setLogSketch(logSketch);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        this.stage.addListener(listener);
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
                logSketch.log("Left Hand Gesture", "Extended.");
                gestureRecorder.startRecording();
                drawing = true;
                drawingPoints.clear();
            }

            @Override
            public void onHandRetracted() {
                logSketch.log("Left Hand Gesture", "Retracted.");
                gestureRecorder.stopRecording();
                drawing = false;
            }
        });

        gestureRecorder.onGestureRecognized(new GestureRecognizedHandler() {
            @Override
            public void gestureRecognized(RecognitionResult gesture) {
                listener.gestureWasRecognized(gesture);
                logSketch.logRounded("Gesture", gesture.name, gesture.score * 100d);
            }

            @Override
            public void gestureWasNotRecognized(String message) {
                logSketch.log("Gesture", message);
            }
        });
    }

    @Override
    protected void drawUserTrackingSketch() {
        setKinectRGBImageAsBackground();

        PVector position = user.centerOfMass;
        stage.updatePosition(position);

        logSketch.logRounded("FPS", frameRate);
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

        if (drawing) {
            drawingPoints.add(leftHandPosition2d);
            stroke(2);
            fill(255, 100, 0);
            for (PVector point : drawingPoints) {
                ellipse(point.x, point.y, 10, 10);
            }
        }

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

    public static interface Listener {

        void gestureWasRecognized(RecognitionResult gesture);

        void userDidEnteredZone(Stage.StageZone stageZone);
    }

    public static class NoopListener implements Listener {

        @Override
        public void gestureWasRecognized(RecognitionResult gesture) {
        }

        @Override
        public void userDidEnteredZone(Stage.StageZone stageZone) {
        }
    }
}


