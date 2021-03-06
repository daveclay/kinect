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
    private Translation translation;
    protected HUD hud;
    Rectangle monitor;

    public final void setup() {
        //size(displayWidth, displayHeight, P2D);
        //frame.setSize(displayWidth, displayHeight);
        //frame.setBackground(Color.black);
        // TODO: this only applies (along with the scaling) to full screen display
        // TODO: resize up and down, with or without aspect ratio. Art doesn't use
        // TODO: the background image, so can take up the full screen rather than
        // TODO: be limited to just the background image size. For now, just always
        // TODO: go full screen, then optionally translation to the background image.

        /*
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        GraphicsDevice window;
        if (devices.length > 1) {
            window = devices[1];
        } else {
            window = devices[0];
        }

        window.setFullScreenWindow(this.frame);
        Rectangle bounds = window.getFullScreenWindow().getBounds();
        size(bounds.width, bounds.height, P2D);
        */

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        // gs[1] gets the *second* screen. gs[0] would get the primary screen
        GraphicsDevice gd = gs[1];
        GraphicsConfiguration[] gc = gd.getConfigurations();
        monitor = gc[0].getBounds();

        println(monitor.x + " " + monitor.y + " " + monitor.width + " " + monitor.height);
        size(monitor.width, monitor.height, P2D);

        // size(displayWidth, displayHeight, P2D);

        //size(1920, 1080, P2D);
        kinect = new KinectPV2(this);
        sketchCallback.setup(kinect);
        kinect.init();

        PImage kinectImage = getKinectImage();
        int kinectImageX = (monitor.width - kinectImage.width) / 2;
        int kinectImageY = (monitor.height - kinectImage.height) / 2;
        translation = new Translation(kinectImageX, kinectImageY);
        System.out.println(translation);
    }

    @Override
    public void destroy() {
        kinect.dispose();
        System.out.println("Disposed of Kinect");
        super.destroy();
    }

    public Translation getKinectImageTranslation() {
        return translation;
    }

    public PImage getKinectImage() {
        return kinect.getColorImage();
    }

    public void setKinectRGBImageAsBackground() {
        PImage colorImage = getKinectImage();
        image(colorImage, translation.x, translation.y);
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
        frame.setLocation(monitor.x, monitor.y);
        frame.setAlwaysOnTop(true);
        //long start = System.currentTimeMillis();
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
