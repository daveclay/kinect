package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.*;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StageMonitor;
import com.daveclay.server.presentation.PresentationServer;

import java.util.HashMap;
import java.util.Map;

public class MultipleBodyLocator extends UserTrackingSketch implements BodyLocator {


    public static void main(String[] args) {
        GestureDataStore gestureDataStore = GestureDataStore.getDefaultInstance();
        MultipleBodyLocator bodyLocator = new MultipleBodyLocator(gestureDataStore);
        PresentationServer.register(gestureDataStore, bodyLocator);
        SketchRunner.run(bodyLocator);
        bodyLocator.frame.setLocation(0, 0);
    }

    private Stage stage;
    private GestureRecognizer gestureRecognizer;
    private BodyLocatorListener listener;

    private Map<Integer, UserData> userDataById = new HashMap<>();
    private StageMonitor stageMonitor;

    public MultipleBodyLocator(GestureDataStore gestureDataStore) {
        hud = new HUD();
        stage = new Stage();
        stage.setupDefaultStageZones();
        stageMonitor = new StageMonitor(stage, hud);
        gestureRecognizer = GestureRecognizer.Factory.defaultInstance(gestureDataStore);

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
        this.listener = listener;
        this.stage.addListener(listener);
    }

    public Stage getStage() {
        return stage;
    }

    protected void registerEventListeners() {
        onUserEntered(user -> {
            UserData userData = new UserData(this,
                    user,
                    this.listener,
                    hud,
                    stage,
                    gestureRecognizer);
            this.userDataById.put(user.getID(), userData);
        });

        onUserWasLost(user -> {
            userDataById.remove(user.getID());
        });
    }

    private void drawBodyLocator() {
        setKinectRGBImageAsBackground();
        for (UserData userData : userDataById.values()) {
            userData.update();
        }
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


