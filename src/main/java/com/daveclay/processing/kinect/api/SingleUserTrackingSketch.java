package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;

public abstract class SingleUserTrackingSketch extends PApplet implements UserTracking {

    private SimpleOpenNI kinect;
    protected User user;
    protected LogSketch logSketch;

    private boolean wasHandExtended = false;
    private int handExtensionThresholdRadius = 350;

    private HandExtendedHandler handExtendedHandler;
    private UserEnteredHandler userEnteredHandler;
    private UserWasLostHandler userWasLostHandler;

    public SingleUserTrackingSketch(User user) {
        this.user = user;
    }

    public SingleUserTrackingSketch() {
        this(new User());
    }

    public final void setup() {
        kinect = new SimpleOpenNI(this);

        // required to enable user tracking
        kinect.enableDepth();
        kinect.enableUser();

        configureKinect(kinect);
        user.setKinect(kinect);
        setupUserTrackingSketch();
    }

    public void setHandExtensionThresholdRadius(int handExtensionThresholdRadius) {
        this.handExtensionThresholdRadius = handExtensionThresholdRadius;
    }

    public void setKinectRGBImageAsBackground() {
        background(kinect.rgbImage());
    }

    public SimpleOpenNI getKinect() {
        return kinect;
    }

    public User getUser() {
        return user;
    }

    protected void configureKinect(SimpleOpenNI kinect) {
    }

    protected abstract void setupUserTrackingSketch();

    public final void draw() {
        kinect.update();
        calculateUserData();
        drawUserTrackingSketch();
    }

    protected abstract void drawUserTrackingSketch();

    public void calculateUserData() {
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (Integer.valueOf(userId).equals(user.getUserId())) {
                user.updateData();

                if (handExtendedHandler != null) {
                    boolean rightHandCurrentlyExtended = user.isRightHandExtended(handExtensionThresholdRadius);
                    if (wasHandExtended && !rightHandCurrentlyExtended) {
                        handExtendedHandler.onHandRetracted();
                    } else if (!wasHandExtended && rightHandCurrentlyExtended) {
                        handExtendedHandler.onHandExtended();
                    }
                    wasHandExtended = rightHandCurrentlyExtended;
                }
            }
        }
    }

    public void onNewUser(SimpleOpenNI curContext, int userId) {
        println("onNewUser( " + userId + " )");
        if ( ! user.isCurrentlyTracking()) {
            user.startTrackingWithUserId(userId);
            if (this.userEnteredHandler != null) {
                this.userEnteredHandler.userDidEnter(user);
            }
        } else {
            // not bothering to track any further users.
        }
    }

    public void onLostUser(SimpleOpenNI kinect, int userId) {
        println("onLostUser( " + userId + " )");
        if (Integer.valueOf(userId).equals(user.getUserId())) {
            user.lost();
            if (this.userWasLostHandler != null) {
                this.userWasLostHandler.userWasLost(user);
            }
        }
    }

    public void onRightHandExtended(HandExtendedHandler handExtendedHandler) {
        this.handExtendedHandler = handExtendedHandler;
    }

    public void onUserEntered(UserEnteredHandler userEnteredHandler) {
        this.userEnteredHandler = userEnteredHandler;
    }

    public void onUserWasLost(UserWasLostHandler userWasLostHandler) {
        // todo: support multiple handlers; the BodyLocator logic doesn't need to know about the StageMonitor.
        this.userWasLostHandler = userWasLostHandler;
    }

}
