package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;

public abstract class SingleUserTrackingSketch extends PApplet implements UserTracking {

    protected SimpleOpenNI kinect;
    protected User user;

    public SingleUserTrackingSketch() {
        this.user = new User();
    }

    public void setup() {
        kinect = new SimpleOpenNI(this);
        user.setKinect(kinect);
        kinect.enableUser();
    }

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
