package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.*;
import com.daveclay.processing.gestures.GestureDataStore;
import com.daveclay.processing.kinect.api.*;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StageMonitor;
import com.daveclay.server.presentation.PresentationServer;
import com.daveclay.server.presentation.PresentationWebSocketListener;
import processing.core.PVector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class BodyLocator extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();
        HUD hud = logSketch.getHud();
        GestureDataStore gestureDataStore = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        gestureDataStore.load();

        Stage stage = new Stage();
        stage.setupDefaultStageZones();

        StageMonitor stageMonitor = new StageMonitor(stage, hud);

        BodyLocator bodyLocator = new BodyLocator(
                gestureDataStore,
                stage,
                hud);

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

    BodyLocatorListener listener;
    HandBox leftHandBox;
    HandBox rightHandBox;
    Stage stage;
    AggregateGestureRecognizer gestureRecognizer = new AggregateGestureRecognizer();
    GestureRecorder gestureRecorder = new GestureRecorder(gestureRecognizer);

    List<PVector> drawingPoints = new ArrayList<PVector>();
    boolean drawGestureRecording;
    private long lastNotification;
    private int drawGestureRecognizedState;
    private User user;

    public BodyLocator(GestureDataStore gestureDataStore,
                       Stage stage,
                       HUD hud) {
        super();
        setSketchCallback(new SketchCallback() {
            @Override
            public void draw() {
                drawBodyLocator();
            }

            @Override
            public void setup(KinectPV2 kinect) {
                kinect.enableSkeleton(true);
                kinect.enableSkeleton3dMap(true);
                kinect.enableSkeletonColorMap(true);
                background(0);
            }
        });

        leftHandBox = new HandBox();
        leftHandBox.color = color(255, 120, 0);

        rightHandBox = new HandBox();
        rightHandBox.color = color(0, 80, 255);

        GeometricRecognizer geometricRecognizer = new GeometricRecognizer();
        geometricRecognizer.addTemplate("Circle", gestureDataStore.getPointsByName("Circle"));

        LineGestureRecognizer lineGestureRecognizer = new LineGestureRecognizer();
        lineGestureRecognizer.addRecognizerAlgorithm("LeftToRightLine", LineGestureRecognizer.LEFT_TO_RIGHT_LINE_RECOGNIZER);
        lineGestureRecognizer.addRecognizerAlgorithm("RightToLeftLine", LineGestureRecognizer.RIGHT_TO_LEFT_LINE_RECOGNIZER);
        lineGestureRecognizer.addRecognizerAlgorithm("BottomToTopLine", LineGestureRecognizer.BOTTOM_TO_TOP_LINE_RECOGNIZER);
        lineGestureRecognizer.addRecognizerAlgorithm("TopToBottomLine", LineGestureRecognizer.TOP_TO_BOTTOM_LINE_RECOGNIZER);

        gestureRecognizer.addRecognizer(geometricRecognizer);
        gestureRecognizer.addRecognizer(lineGestureRecognizer);

        this.stage = stage;
        this.hud = hud;

        registerEventListeners();
    }

    public void setListener(BodyLocatorListener listener) {
        this.listener = listener;
        this.stage.addListener(listener);
    }

    public Stage getStage() {
        return stage;
    }

    protected void registerEventListeners() {

        onRightHandExtended(new HandExtendedHandler() {
            @Override
            public void onHandExtended() {
                hud.log("Right Hand Gesture", "Extended.");
                gestureRecorder.startRecording();
                drawGestureRecording = true;
            }

            @Override
            public void onHandRetracted() {
                hud.log("Right Hand Gesture", "Retracted.");
                gestureRecorder.stopRecording();
                drawGestureRecording = false;
            }
        });

        onUserEntered(new UserEnteredHandler() {
            @Override
            public void userDidEnter(User user) {
                System.out.println("HELLO User " + user.getID() + "!");
                BodyLocator.this.user = user;
            }
        });

        onUserWasLost(new UserWasLostHandler() {
            @Override
            public void userWasLost(User user) {
                System.out.println("User " + user.getID() + " LOST, eh well...");
                BodyLocator.this.user = null;
            }
        });

        gestureRecorder.onGestureRecognized(new GestureRecognizedHandler() {
            @Override
            public void gestureRecognized(RecognitionResult gesture) {
                listener.gestureWasRecognized(gesture);
                drawGestureRecognizedAlert(true, gesture, null);
            }

            @Override
            public void gestureWasNotRecognized(String message) {
                drawGestureRecognizedAlert(false, null, message);
                hud.log("Gesture", message);
            }
        });
    }

    private void drawGestureRecognizedAlert(boolean recognized, RecognitionResult gesture, String message) {
        if (recognized || drawGestureRecognizedState != 1 || System.currentTimeMillis() - lastNotification > 1000) {
            drawGestureRecognizedState = recognized ? 1 : 2;
            lastNotification = System.currentTimeMillis();

            if (gesture != null) {
                hud.logRounded("Gesture", gesture.name, gesture.score * 100d);
            } else {
                hud.log("Gesture", message);
            }
        }
    }

    private void drawBodyLocator() {
        setKinectRGBImageAsBackground();

        // Note: this might be what the native kinect is getting, but it's not necessarily what we're processing...
        hud.logRounded("FPS", frameRate);

        updateUserDataAndDrawStuff();
        drawGestureRecognitionNotification();
    }

    private void updateUserDataAndDrawStuff() {
        if (user != null) {

            PVector newUserPosition = user.getJointPosition3D(KinectPV2.JointType_SpineMid);

            stage.updatePosition(newUserPosition);

            // Todo: refactor - have a gesture aware delegate doing this based on userDidEnter() callbacks.
            gestureRecorder.addPoint(user.getRightHandPosition());

            // draw user data.
            drawUserData(user);
        } else {
            // Note that this will override the gesture recognized notification. The user will likely
            // have seen the results of a recognized gesture, and wants to know that they should stop
            // expecting gestures immediately if the sensor has lost them.
            //
            // In other words, don't allow the user to look like an idiot expecting gestures to work
            // if we've lost them. Notify them immediately so they don't look like an idiot.
            drawLostUser();
        }
    }

    private void drawLostUser() {
        fill(0, 0, 0, 200);
        rect(0, 0, getWidth(), getHeight());
        fill(255);
        textSize(100);
        text("Lost User", getHeight() / 3, getWidth() / 3);
    }

    private void drawGestureRecognitionNotification() {
        if (drawGestureRecognizedState > 0) {
            if (System.currentTimeMillis() - lastNotification > 1000) {
                drawGestureRecognizedState = 0;
                drawingPoints.clear();
            } else {
                if (drawGestureRecognizedState == 1) {
                    drawGestureRecognized();
                } else {
                    drawGestureNotRecognized();
                }
            }
        }
    }

    private void drawGestureRecognized() {
        fill(0, 255, 0, 100);
        rect(0, 0, getWidth(), getHeight());
    }

    private void drawGestureNotRecognized() {
        fill(255, 0, 0, 100);
        rect(0, 0, getWidth(), getHeight());
    }

    private int gestureLineAlpha = 255;

    private void drawUserData(User user) {
        pushMatrix();

        PVector leftHandPosition2d = user.getLeftHandPosition2D();
        PVector rightHandPosition2d = user.getRightHandPosition2D();

        hud.logScreenCoords("Right Hand", rightHandPosition2d);
        hud.logScreenCoords("Left Hand", leftHandPosition2d);

        stroke(120);
        strokeWeight(2);
        line(leftHandPosition2d.x, leftHandPosition2d.y, rightHandPosition2d.x, rightHandPosition2d.y);
        leftHandBox.drawAt(leftHandPosition2d);
        rightHandBox.drawAt(rightHandPosition2d);
        popMatrix();

        if (drawGestureRecording) {
            drawingPoints.add(rightHandPosition2d);
        }

        if (drawGestureRecording || drawGestureRecognizedState > 0) {

            if (drawGestureRecording) {
                gestureLineAlpha = 255;
            } else {
                // fade it out since we're not
                gestureLineAlpha -= 5;
                if (gestureLineAlpha < 0) {
                    gestureLineAlpha = 0;
                }
            }

            PVector previousPoint = null;
            for (PVector point : drawingPoints) {
                if (previousPoint != null) {
                    stroke(0, 255, 0, gestureLineAlpha);
                    line(previousPoint.x, previousPoint.y, point.x, point.y);
                }
                fill(255, 100, 0, gestureLineAlpha);
                ellipse(point.x, point.y, 10, 10);
                previousPoint = point;
            }
        }
    }

    private class HandBox {

        PVector center;
        int color;
        int size = 20;
        int alpha = 255;

        void drawAt(PVector position) {
            center = position;
            strokeWeight(3);
            fill(red(color), blue(color), green(color), (int) (alpha * .5));
            stroke(red(color), blue(color), green(color), alpha);
            rect(center.x, center.y, size, size);
        }
    }
}


