package com.daveclay.processing.kinect.api;

import KinectPV2.KinectPV2;
import KinectPV2.*;

import com.daveclay.processing.api.LogSketch;
import processing.core.PApplet;
import processing.core.PImage;

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

    private final Map<Integer, UserTrackingState> userTrackingStateByIndex = new HashMap<Integer, UserTrackingState>();
    private final List<UserEnteredHandler> userEnteredHandlers =  new ArrayList<UserEnteredHandler>();
    private final List<UserWasLostHandler> userWasLostHandlers = new ArrayList<UserWasLostHandler>();

    private UserEventsConfig perUserEventsConfig = new UserEventsConfig();

    private SketchCallback sketchCallback;
    private KinectPV2 kinect;
    protected LogSketch logSketch;

    public final void setup() {
        size(1920, 1080, OPENGL);

        kinect = new KinectPV2(this);

        /*
        // TODO:
        if (this.enableColorImg()) {
          then bother actually retriving image data or skeleton data.
         */
        // required to enable user tracking
        /*
        kinect.enableColorImg(true);
        kinect.enableSkeleton(true);
        kinect.enableSkeleton3dMap(true);
        kinect.enableSkeletonColorMap(true);
        */

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
        background(colorImage);
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
            User user = new User(kinect, skeleton3D, colorSkeleton, perUserEventsConfig, index);
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
