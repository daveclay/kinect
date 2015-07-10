package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.Drawing;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.gestures.GestureRecognizedHandler;
import com.daveclay.processing.gestures.GestureRecognizer;
import com.daveclay.processing.gestures.GestureRecorder;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.HandExtendedHandler;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.stage.Stage;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    BodyLocatorListener listener;
    HandBox leftHandBox;
    HandBox rightHandBox;
    GestureRecorder gestureRecorder;
    User user;
    Stage stage;
    HUD hud;

    Drawing userDrawing;

    List<PVector> drawingPoints = new ArrayList<>();
    boolean drawGestureRecording;
    long lastNotification;
    int drawGestureRecognizedState;
    private int gestureLineAlpha = 255;

    public UserData(PApplet canvas,
                    HUD hud,
                    Stage stage,
                    GestureRecognizer gestureRecognizer) {
        this.hud = hud;
        this.stage = stage;

        leftHandBox = new HandBox(canvas);
        leftHandBox.color = canvas.color(255, 120, 0);

        rightHandBox = new HandBox(canvas);
        rightHandBox.color = canvas.color(0, 80, 255);
        gestureRecorder = new GestureRecorder(gestureRecognizer);

        gestureRecorder.onGestureRecognized(new GestureRecognizedHandler() {
            @Override
            public void gestureRecognized(RecognitionResult gesture) {
                listener.gestureWasRecognized(user, gesture);
                setGestureRecogniionAlert(true, gesture, null);
            }

            @Override
            public void gestureWasNotRecognized(String message) {
                setGestureRecogniionAlert(false, null, message);
                // TODO: per-user hud
                // hud.log("Gesture", message);
            }
        });

        userDrawing = new Drawing(canvas) {
            public void draw() {
                PVector leftHandPosition2d = user.getLeftHandPosition2D();
                PVector rightHandPosition2d = user.getRightHandPosition2D();

                pushMatrix();
                drawGestureRecognitionNotification();

                stroke(120);
                strokeWeight(2);
                line(leftHandPosition2d.x, leftHandPosition2d.y, rightHandPosition2d.x, rightHandPosition2d.y);
                leftHandBox.drawAt(leftHandPosition2d);
                rightHandBox.drawAt(rightHandPosition2d);
                popMatrix();

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
        };
    }

    public void setListener(BodyLocatorListener listener) {
        this.listener = listener;
    }

    public void userDidEnter(User user) {
        this.user = user;
        user.onRightHandExtended(new HandExtendedHandler() {
            @Override
            public void onHandExtended(User user) {
                // TODO: per-user hud
                hud.log("Right Hand Gesture", "Extended.");
                gestureRecorder.startRecording();
                drawGestureRecording = true;
            }

            @Override
            public void onHandRetracted(User user) {
                // TODO: per-user hud
                hud.log("Right Hand Gesture", "Retracted.");
                gestureRecorder.stopRecording();
                drawGestureRecording = false;
            }
        });

    }

    public void userWasLost() {
        // tODO: make sure it's the right user (by index, I think)
        this.user = null;
    }

    public void update() {
        if (user != null) {
            PVector newUserPosition = user.getJointPosition3D(KinectPV2.JointType_SpineMid);
            stage.updatePosition(user, newUserPosition);

            PVector rightHandPosition2d = user.getRightHandPosition2D();
            gestureRecorder.addPoint(rightHandPosition2d);
            if (drawGestureRecording) {
                // TODO: these should be retrieved from the GestureRecorder so we don't have to infer which hand the points are coming from.
                drawingPoints.add(rightHandPosition2d);
            }

            // draw user data.
            userDrawing.draw();
        } else {
            // TODO: drawLostUser();
        }
    }

    private void setGestureRecogniionAlert(boolean recognized, RecognitionResult gesture, String message) {
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
}
