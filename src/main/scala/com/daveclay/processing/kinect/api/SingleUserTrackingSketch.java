package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;

public abstract class SingleUserTrackingSketch extends PApplet implements UserTracking {

    protected SimpleOpenNI kinect;
    protected User user;
    protected LogSketch logSketch;

    public SingleUserTrackingSketch() {
        this.user = new User();
    }

    public final void setup() {
        System.out.println("setup start.");
        kinect = new SimpleOpenNI(this);
        user.setKinect(kinect);
        kinect.enableUser();
        setupUserTrackingSketch();
        System.out.println("setup complete.");
    }

    public abstract void setupUserTrackingSketch();

    public final void draw() {
        System.out.println("draw start.");
        kinect.update();
        calculateUserData();
        drawUserTrackingSketch();
        System.out.println("draw complete.");
    }

    public abstract void drawUserTrackingSketch();

    public User getUser() {
        return user;
    }

    public void calculateUserData() {
        int[] userList = kinect.getUsers();
        for (int userId : userList) {
            if (Integer.valueOf(userId).equals(user.getUserId())) {
                user.updateData();
            }
        }
    }

    public void onNewUser(SimpleOpenNI curContext, int userId) {
        println("onNewUser( " + userId + " )");
        if ( ! user.isCurrentlyTracking()) {
            user.startTrackingWithUserId(userId);
        } else {
            // not bothering to track any further users.
        }
    }

    public void onLostUser(SimpleOpenNI kinect, int userId) {
        println("onLostUser( " + userId + " )");
        if (Integer.valueOf(userId).equals(user.getUserId())) {
            user.lost();
        }
    }
}