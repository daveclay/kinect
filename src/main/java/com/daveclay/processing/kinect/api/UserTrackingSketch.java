package com.daveclay.processing.kinect.api;

import KinectPV2.KinectPV2;
import KinectPV2.*;

import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTrackingSketch extends PApplet {

    public static interface SketchCallback {
        void draw();
    }

    private final Map<Integer, UserTrackingState> userTrackingStateByIndex = new HashMap<Integer, UserTrackingState>();
    private final List<UserEnteredHandler> userEnteredHandlers =  new ArrayList<UserEnteredHandler>();
    private final List<UserWasLostHandler> userWasLostHandlers = new ArrayList<UserWasLostHandler>();

    private UserEventsConfig perUserEventsConfig = new UserEventsConfig();

    private SketchCallback sketchCallback;
    private KinectPV2 kinect;
    protected LogSketch logSketch;

    public final void setup() {
        size(640, 480, OPENGL);

        kinect = new KinectPV2(this);

        // required to enable user tracking
        kinect.enableColorImg(true);
        kinect.enableBodyTrackImg(true);
        kinect.enableSkeleton3dMap(true);
    }

    public void setKinectRGBImageAsBackground() {
        background(kinect.getColorImage());
    }

    public void setSketchCallback(SketchCallback sketchCallback) {
        this.sketchCallback = sketchCallback;
    }

    public KinectPV2 getKinect() {
        return kinect;
    }

    public User getFirstCurrentlyActiveUser() {
        int lowestActiveIndex = Integer.MAX_VALUE;
        User user = null;
        for (Map.Entry<Integer, UserTrackingState> entry : userTrackingStateByIndex.entrySet()) {
            int index = entry.getKey();
            UserTrackingState userTrackingState = entry.getValue();
            if (userTrackingState.currentlyTracked && index < lowestActiveIndex) {
                user = userTrackingState.user;
            }
        }
        return user;
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

        long time = System.currentTimeMillis() - start;
        if (time > max) {
            max = time;
        }
        perfCount++;
        averagePerf = (averagePerf + time) / perfCount;
        logSketch.log("average time", time + "ms");
        logSketch.log("max time", max + "ms");
    }

    public void onLeftHandExtended(HandExtendedHandler handExtendedHandler) {
        perUserEventsConfig.addLeftHandExtendedHandler(handExtendedHandler);
    }

    public void onRightHandExtended(HandExtendedHandler handExtendedHandler) {
        perUserEventsConfig.addRightHandExtendedHandler(handExtendedHandler);
    }

    public void onUserEntered(UserEnteredHandler userEnteredHandler) {
        this.userEnteredHandlers.add(userEnteredHandler);
    }

    public void onUserWasLost(UserWasLostHandler userWasLostHandler) {
        this.userWasLostHandlers.add(userWasLostHandler);
    }

    private void calculateAndTriggerUserEvents() {
        Skeleton[] skeletons = kinect.getSkeleton3d();
        for (int i = 0; i < skeletons.length; i++) {
            Skeleton skeleton = skeletons[i];
            updateUserSkeleton(i, skeleton);
        }
    }

    private void updateUserSkeleton(int index, Skeleton skeleton) {
        UserTrackingState userTrackingState;
        if ( ! userTrackingStateByIndex.containsKey(index)) {
            User user = new User(kinect, skeleton, perUserEventsConfig);
            userTrackingState = new UserTrackingState(user);
            userTrackingStateByIndex.put(index, userTrackingState);
        } else {
            userTrackingState = userTrackingStateByIndex.get(index);
        }

        userTrackingState.updateTrackingStatus(skeleton);
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
                user.triggerUserInteractionListeners();
            } else {
                if (currentlyTracked) {
                    triggerUserLostListeners(user);
                    currentlyTracked = false;
                }
            }

        }
    }
}
