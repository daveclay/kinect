package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.Drawing;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AfterRebelBellyMultiBody extends UserTrackingSketch implements BodyLocator {

    public static void main(String[] args) {
        AfterRebelBellyMultiBody bodyLocator = new AfterRebelBellyMultiBody();
        SketchRunner.runSketchFullScreen(bodyLocator, 0);
        bodyLocator.frame.setLocation(0, 0);
    }

    private Map<Integer, Body> bodiesById = new HashMap<>();
    PShader shader;

    public AfterRebelBellyMultiBody() {
        hud = new HUD();

        setSketchCallback(new SketchCallback() {
            @Override
            public void draw() {
                drawBodies();
            }

            @Override
            public void setup(KinectPV2 kinect) {
                kinect.enableSkeleton(true);
                kinect.enableSkeleton3dMap(true);
                kinect.enableSkeletonColorMap(true);

                File f = new File(System.getProperty("user.dir") + "/src/main/resources/shaders/blur.glsl");
                shader = loadShader(f.getAbsolutePath());
                shader.set("sigma", 60f);
                shader.set("blurSize", 30);
                shader.set("texOffset", 1f, 1f);

                background(0);
            }
        });

        registerEventListeners();
    }

    protected void registerEventListeners() {
        onUserEntered(user -> {
            hud.log("Detected Body " + user.getID(), "");
            Body body = new Body(this, user, hud);
            this.bodiesById.put(user.getID(), body);
        });

        onUserWasLost(user -> {
            hud.log("Lost Body " + user.getID(), "");
            bodiesById.remove(user.getID());
        });
    }

    private void drawBodies() {
        shader(shader);
        bodiesById.values().forEach(Body::draw);
        //drawHUD();
    }

    private void drawHUD() {
        hud.log("Frame Rate", frameRate);
        hud.draw(this);
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

    @Override
    public void setListener(BodyLocatorListener listener) {
    }

    public class Body extends Drawing {
        private User user;
        private HUD hud;

        public Body(PApplet canvas,
                    User user,
                    HUD hud) {
            super(canvas);
            this.user = user;
            this.hud = hud;
        }

        public void draw() {
            pushMatrix();
            PVector leftHandPosition2d = user.getLeftHandPosition2D();
            PVector rightHandPosition2d = user.getRightHandPosition2D();

            drawHand(leftHandPosition2d, color(255, 0, 0, 40));
            drawHand(rightHandPosition2d, color(255, 128, 0, 40));

            popMatrix();
        }

        void drawHand(PVector center, int color) {
            pushStyle();
            strokeWeight(3);
            noFill();
            stroke(color);
            rect(center.x, center.y, 100, 100);
            popStyle();

        }
    }
}


