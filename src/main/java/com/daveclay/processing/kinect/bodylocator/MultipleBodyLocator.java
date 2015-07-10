package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.*;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StageMonitor;
import com.daveclay.server.presentation.PresentationServer;

public class MultipleBodyLocator extends UserTrackingSketch implements BodyLocator {

    public static void main(String[] args) {
        GestureDataStore gestureDataStore = GestureDataStore.getDefaultInstance();
        MultipleBodyLocator bodyLocator = new MultipleBodyLocator(gestureDataStore);
        PresentationServer.register(gestureDataStore, bodyLocator);
        SketchRunner.run(bodyLocator);
        bodyLocator.frame.setLocation(0, 0);
    }

    Stage stage;
    GestureRecognizer gestureRecognizer;

    UserData userData;
    private StageMonitor stageMonitor;

    public MultipleBodyLocator(GestureDataStore gestureDataStore) {

        hud = new HUD();
        stage = new Stage();
        stage.setupDefaultStageZones();
        stageMonitor = new StageMonitor(stage, hud);
        gestureRecognizer = GestureRecognizer.Factory.defaultInstance(gestureDataStore);
        userData = new UserData(this, hud, stage, gestureRecognizer);

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

        registerEventListeners();
    }

    public void setListener(BodyLocatorListener listener) {
        this.userData.setListener(listener);
        this.stage.addListener(listener);
    }

    public Stage getStage() {
        return stage;
    }

    protected void registerEventListeners() {

        onUserEntered(user -> {
            // TODO: construct new UserData that only exists while the user exists?
            MultipleBodyLocator.this.userData.userDidEnter(user);
        });

        // tODO: make sure it's the right user (by index, I think)
        onUserWasLost(user -> {
            MultipleBodyLocator.this.userData.userWasLost();
        });
    }

    private void drawBodyLocator() {
        setKinectRGBImageAsBackground();
        // TODO: update all user positions, not just this one guy.
        userData.update();
        drawHUD();
    }

    private void drawHUD() {
        hud.draw(this);
        stageMonitor.draw(this);
    }

    private void screenMessage(String msg) {
        fill(0, 0, 0, 200);
        rect(0, 0, getWidth(), getHeight());
        fill(255);
        float fontSize = 100;
        textSize(fontSize);
        float textWidth = textWidth(msg);
        float x = (width - textWidth) / 2;
        float y = (height - fontSize) / 2;
        text(msg, x, y);
    }

}


