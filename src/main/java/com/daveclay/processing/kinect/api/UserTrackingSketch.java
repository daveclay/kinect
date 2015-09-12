package com.daveclay.processing.kinect.api;

import KinectPV2.KinectPV2;
import KinectPV2.*;

import com.daveclay.processing.api.HUD;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTrackingSketch extends PApplet {

    public static abstract class SketchCallback {
        public void setup(KinectPV2 kinect) {
        }

        abstract public void draw();
    }

    private final Map<Integer, UserTrackingState> userTrackingStateByIndex = new HashMap<>();
    private final List<UserEnteredHandler> userEnteredHandlers =  new ArrayList<>();
    private final List<UserWasLostHandler> userWasLostHandlers = new ArrayList<>();

    private UserEventsConfig perUserEventsConfig = new UserEventsConfig();

    private SketchCallback sketchCallback;
    private KinectPV2 kinect;
    protected HUD hud;

    public final void setup() {
        //size(displayWidth, displayHeight, P2D);
        //frame.setSize(displayWidth, displayHeight);
        //frame.setBackground(Color.black);
        size(displayWidth, displayHeight, P2D);
        //size(1920, 1080, P2D);
        kinect = new KinectPV2(this);
        sketchCallback.setup(kinect);
        kinect.init();
    }

    @Override
    public void destroy() {
        kinect.dispose();
        System.out.println("Disposed of Kinect");
        super.destroy();
    }

    public void setKinectRGBImageAsBackground() {
        PImage colorImage = kinect.getColorImage();
        image(colorImage, 0, 0);
        // background(colorImage);
    }

    public void setSketchCallback(SketchCallback sketchCallback) {
        this.sketchCallback = sketchCallback;
    }

    public KinectPV2 getKinect() {
        return kinect;
    }

    private long max;
    private long averagePerf = 0;
    private long perfCount = 0;

    public final void draw() {
        long start = System.currentTimeMillis();
        calculateAndTriggerUserEvents();
        if (sketchCallback != null) {
            sketchCallback.draw();
        }
        //logPerformance(start);
    }

    private void logPerformance(long start) {
        long time = System.currentTimeMillis() - start;
        if (time > max) {
            max = time;
        }
        perfCount++;
        averagePerf = (averagePerf + time) / perfCount;
        hud.log("average time", time + "ms");
        hud.log("max time", max + "ms");
    }

    public void onUserEntered(UserEnteredHandler userEnteredHandler) {
        this.userEnteredHandlers.add(userEnteredHandler);
    }

    public void onUserWasLost(UserWasLostHandler userWasLostHandler) {
        this.userWasLostHandlers.add(userWasLostHandler);
    }

    private void calculateAndTriggerUserEvents() {
        Skeleton[] skeleton3Ds = kinect.getSkeleton3d();
        Skeleton[] colorSkeletons = kinect.getSkeletonColorMap();
        for (int i = 0; i < skeleton3Ds.length; i++) {
            Skeleton skeleton3D = skeleton3Ds[i];
            Skeleton colorSkeleton = colorSkeletons[i];
            updateUserSkeleton(i, skeleton3D, colorSkeleton);
        }
    }

    private void updateUserSkeleton(int index,
                                    Skeleton skeleton3D,
                                    Skeleton colorSkeleton) {
        UserTrackingState userTrackingState;
        if ( ! userTrackingStateByIndex.containsKey(index)) {
            // TODO: perUserEventsConfig here contains the handlers... which should be set on the thing that
            // listens for users themselves?
            User user = new User(skeleton3D, colorSkeleton, perUserEventsConfig, index);
            userTrackingState = new UserTrackingState(user);
            userTrackingStateByIndex.put(index, userTrackingState);
        } else {
            userTrackingState = userTrackingStateByIndex.get(index);
        }

        userTrackingState.updateTrackingStatus(skeleton3D);
    }

    private void triggerUserEnteredListeners(User user) {
        for (UserEnteredHandler userEnteredHandler : this.userEnteredHandlers) {
            userEnteredHandler.userDidEnter(user);
        }
    }

    private void triggerUserLostListeners(User user) {
        for (UserWasLostHandler userWasLostHandler : this.userWasLostHandlers) {
            userWasLostHandler.userWasLost(user);
        }
    }

    private class UserTrackingState {
        private final User user;
        private boolean currentlyTracked;

        public UserTrackingState(User user) {
            this.user = user;
        }

        public void updateTrackingStatus(Skeleton skeleton) {
            if (skeleton.isTracked()) {
                if ( ! currentlyTracked) {
                    triggerUserEnteredListeners(user);
                    currentlyTracked = true;
                }
                user.update();
            } else {
                if (currentlyTracked) {
                    triggerUserLostListeners(user);
                    currentlyTracked = false;
                }
            }

        }
    }
}
