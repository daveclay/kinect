package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.Drawing;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.opengl.PShader;

import java.util.HashMap;
import java.util.Map;

public class AfterRebelBellyMultiBody extends UserTrackingSketch implements BodyLocator {

    public static void main(String[] args) {
        AfterRebelBellyMultiBody bodyLocator = new AfterRebelBellyMultiBody();
        SketchRunner.runSketchFullScreen(bodyLocator, 0);
        bodyLocator.frame.setLocation(0, 0);
    }

    private Map<Integer, Body> bodiesById = new HashMap<>();
    PShader colorSeparator;
    PShader blur;
    public PFont orator9;
    public PFont orator23;

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
                blur = ImgProc.shader(AfterRebelBellyMultiBody.this, "gaussianBlur");
                blur.set("kernelSize", 12); // How big is the sampling kernel?
                blur.set("strength", 8f); // How strong is the gaussianBlur?

                colorSeparator = ImgProc.shader(AfterRebelBellyMultiBody.this, "colorSeparation");

                orator9 = createFont("OratorStd", 9);
                orator23 = createFont("OratorStd", 23);
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
        background(0);
        bodiesById.values().forEach(Body::draw);
        blur();
        colorSeparator.set("time", (float) millis() / 1000f);
        filter(colorSeparator);
        drawHUD();
    }

    void blur() {
        blur.set("horizontalPass", 0);
        filter(blur);

        // Horizontal pass
        blur.set("horizontalPass", 1);
        filter(blur);
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

            drawHand(leftHandPosition2d, color(255, 150, 200));
            drawHand(rightHandPosition2d, color(165, 228, 255));

            pushStyle();
            stroke(255);
            line(leftHandPosition2d.x, leftHandPosition2d.y, rightHandPosition2d.x, rightHandPosition2d.y);
            endShape();
            popStyle();

            popMatrix();
        }

        void drawHand(PVector center, int color) {
            pushStyle();
            strokeWeight(3);
            noFill();
            stroke(color);
            rect(center.x - 50, center.y - 50, 100, 100);
            textFont(orator23);
            text("x"+center.x, center.x + 110, center.y + 20);
            textFont(orator9);
            text("SEND::"+center.y, center.x + 110, center.y + 40);
            popStyle();

        }
    }
}


