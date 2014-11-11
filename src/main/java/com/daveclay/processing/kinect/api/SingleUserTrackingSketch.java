package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class SingleUserTrackingSketch extends PApplet implements UserTracking {

    public static interface SketchCallback {
        void draw();
    }

    private final UserTrackingKinectConfig userTrackingKinectConfig;
    private final User user;
    private final LeftHandState leftHandState;
    private final RightHandState rightHandState;
    private final List<UserEnteredHandler> userEnteredHandlers;
    private final List<UserWasLostHandler> userWasLostHandlers;

    private SketchCallback sketchCallback;
    private SimpleOpenNI kinect;
    protected LogSketch logSketch;

    public SingleUserTrackingSketch(User user,
                                    UserTrackingKinectConfig userTrackingKinectConfig) {
        this.user = user;
        this.userTrackingKinectConfig = userTrackingKinectConfig;
        leftHandState = new LeftHandState(user);
        rightHandState = new RightHandState(user);
        userEnteredHandlers = new ArrayList<UserEnteredHandler>();
        userWasLostHandlers = new ArrayList<UserWasLostHandler>();
    }

    public final void setup() {
        kinect = new SimpleOpenNI(this);

        // required to enable user tracking
        kinect.enableDepth();
        kinect.enableUser();

        user.setKinect(kinect);
        userTrackingKinectConfig.setupUserTrackingSketch(this);
    }

    public void setKinectRGBImageAsBackground() {
        background(kinect.rgbImage());
    }

    public void setSketchCallback(SketchCallback sketchCallback) {
        this.sketchCallback = sketchCallback;
    }

    public SimpleOpenNI getKinect() {
        return kinect;
    }

    public User getUser() {
        return user;
    }

    public final void draw() {
        kinect.update();
        calculateUserData();
        if (sketchCallback != null) {
            sketchCallback.draw();
        }
    }

    public void onNewUser(SimpleOpenNI curContext, int userId) {
        println("onNewUser( " + userId + " )");
        if ( ! user.isCurrentlyTracking()) {
            user.startTrackingWithUserId(userId);
            triggerUserEnteredListeners();
        } else {
            // not bothering to track any further users.
        }
    }

    public void onLostUser(SimpleOpenNI kinect, int userId) {
        println("onLostUser( " + userId + " )");
        if (Integer.valueOf(userId).equals(user.getUserId())) {
            user.lost();
            triggerUserLostListeners();
        }
    }

    public void onLeftHandExtended(HandExtendedHandler handExtendedHandler) {
        this.leftHandState.addHandExtendedHandler(handExtendedHandler);
    }

    public void onRightHandExtended(HandExtendedHandler handExtendedHandler) {
        this.rightHandState.addHandExtendedHandler(handExtendedHandler);
    }

    public void onUserEntered(UserEnteredHandler userEnteredHandler) {
        this.userEnteredHandlers.add(userEnteredHandler);
    }

    public void onUserWasLost(UserWasLostHandler userWasLostHandler) {
        this.userWasLostHandlers.add(userWasLostHandler);
    }

    private void calculateUserData() {
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (Integer.valueOf(userId).equals(user.getUserId())) {
                user.updateData();
                triggerUserInteractionListeners();
            }
        }
    }

    private void triggerUserInteractionListeners() {
        leftHandState.triggerHandStateEvents();
        rightHandState.triggerHandStateEvents();
    }

    private void triggerUserEnteredListeners() {
        for (UserEnteredHandler userEnteredHandler : this.userEnteredHandlers) {
            userEnteredHandler.userDidEnter(user);
        }
    }

    private void triggerUserLostListeners() {
        for (UserWasLostHandler userWasLostHandler : this.userWasLostHandlers) {
            userWasLostHandler.userWasLost(user);
        }
    }

}
