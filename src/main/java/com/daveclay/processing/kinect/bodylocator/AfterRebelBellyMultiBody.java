package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.*;
import com.daveclay.processing.api.image.ImgProc;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AfterRebelBellyMultiBody extends UserTrackingSketch implements BodyLocator {

    public static void main(String[] args) {
        AfterRebelBellyMultiBody bodyLocator = new AfterRebelBellyMultiBody();
        SketchRunner.runSketchFullScreen(bodyLocator, 0);
        bodyLocator.frame.setLocation(0, 0);
    }

    Map<Integer, Body> bodiesById = new HashMap<>();

    PShader colorSeparator;
    PShader badBlur;
    PShader gaussianBlur;

    NoiseColor noiseColor;

    PFont orator9;
    PFont orator23;

    PGraphics screenBlur;
    PGraphics multiplyMe;

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
                gaussianBlur = ImgProc.shader(AfterRebelBellyMultiBody.this, "gaussianBlur");
                gaussianBlur.set("kernelSize", 12); // How big is the sampling kernel?
                gaussianBlur.set("strength", 8f); // How strong is the gaussianBlur?

                badBlur = ImgProc.shader(AfterRebelBellyMultiBody.this, "badBlur");

                colorSeparator = ImgProc.shader(AfterRebelBellyMultiBody.this, "colorSeparation");

                orator9 = createFont("OratorStd", 9);
                orator23 = createFont("OratorStd", 23);

                noiseColor = new NoiseColor(AfterRebelBellyMultiBody.this, .01f);

                screenBlur = createGraphics(width, height, P2D);
                multiplyMe = createGraphics(width, height, P2D);
                bg(screenBlur);
                background(255);
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

        bodiesById.values().forEach(Body::updatePosition);

        // draw onto the ScreenBlur
        bodiesById.values().forEach(Body::drawScreenBlur);

        screenBlur.beginDraw();
        Body previous = null;
        screenBlur.pushStyle();
        screenBlur.strokeWeight(1);
        for (Body body : bodiesById.values()) {
            lineTo(body.leftHandPosition2d, body.rightHandPosition2d);
            if (previous != null) {
                lineTo(body.rightHandPosition2d, previous.leftHandPosition2d);
            }
            previous = body;
        }
        screenBlur.popStyle();

        blur(screenBlur, 9, 2f);
        screenBlur.filter(badBlur);
        screenBlur.endDraw();

        image(screenBlur, 0, 0);

        bodiesById.values().forEach(Body::draw);

        //blur();
        //colorSeparator.set("time", (float) millis() / 1000f);
        //filter(colorSeparator);
        drawHUD();
    }

    void lineTo(PVector locationA, PVector locationB) {
        screenBlur.stroke(noiseColor.nextColor(200));
        screenBlur.line(locationA.x, locationA.y, locationB.x, locationB.y);
    }

    void blur() {
        gaussianBlur.set("horizontalPass", 0);
        filter(gaussianBlur);

        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        filter(gaussianBlur);
    }

    void blur(PGraphics graphics, int size, float strength) {
        gaussianBlur.set("kernelSize", size); // How big is the sampling kernel?
        gaussianBlur.set("strength", strength); // How strong is the gaussianBlur?

        gaussianBlur.set("horizontalPass", 0);
        graphics.filter(gaussianBlur);
        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        graphics.filter(gaussianBlur);
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

    void bg(PGraphics graphics) {
        graphics.beginDraw();
        graphics.background(0);
        graphics.endDraw();
    }

    @Override
    public void setListener(BodyLocatorListener listener) {
    }

    public class Body extends Drawing {
        private User user;
        private HUD hud;
        PGraphics sprite;
        NoiseColor noiseColor;

        PVector leftHandPosition2d;
        PVector rightHandPosition2d;
        Dimension size;
        int offset = 10;

        public Body(PApplet canvas,
                    User user,
                    HUD hud) {
            super(canvas);
            this.size = new Dimension(60, 60);
            this.offset = 10;
            this.user = user;
            this.hud = hud;
            this.sprite = canvas.createGraphics(400, 400, P2D);
            bg(this.sprite);
            noiseColor = new NoiseColor(canvas, .01f);
        }

        public void drawBasicBoxes() {
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
            text("x" + center.x, center.x + 110, center.y + 20);
            textFont(orator9);
            text("SEND::" + center.y, center.x + 110, center.y + 40);
            popStyle();

        }

        public void updatePosition() {
            leftHandPosition2d = scale(user.getLeftHandPosition2D());
            rightHandPosition2d = scale(user.getRightHandPosition2D());
        }

        PVector scale(PVector location) {
            int x = (int) map(location.x, 0, getKinectImage().width, 0, width);
            int y = (int) map(location.y, 0, getKinectImage().height, 0, height);
            return new PVector(x, y);
        }

        public void drawScreenBlur() {
            drawScreenBlur(leftHandPosition2d);
            drawScreenBlur(rightHandPosition2d);
        }

        public void drawScreenBlur(PVector location) {
            sprite.beginDraw();
            sprite.background(0);
            int color = noiseColor.nextColor(255);
            // add red! blend it in? How? reduce others by some amount?
            int red = (int) map(leftHandPosition2d.y, 0, height - 400, 0, 255);
            color = ColorUtils.addRed(red, color);
            drawRect(sprite, offset, size, color);
            sprite.endDraw();

            screenBlur.beginDraw();
            screenBlur.blendMode(SCREEN);
            screenBlur.image(sprite, location.x, location.y);
            screenBlur.endDraw();
        }

        public void draw() {
            drawRect(leftHandPosition2d);
            drawRect(rightHandPosition2d);
        }

        void drawRect(PVector location) {
            int x = (int) location.x;
            int y = (int) location.y;
            pushStyle();
            strokeWeight(1);
            noFill();
            stroke(color(255, 100));
            rect(x + offset, y + offset, size.width, size.height);
            line(x + offset, y + offset, x + size.width + offset, y + size.height + offset);
            line(x + offset, y + size.height + offset, x + size.width + offset, y + offset);

            textFont(orator9);
            fill(color(255, 255, 255, 120));
            text("0x" + Integer.toHexString(frameCount).toUpperCase(), x + size.height + offset, y + offset + 23);
            text("[" + x + "," + y + "]", x + size.height + offset, y + 46);
            popStyle();
        }

        void drawRect(PGraphics graphics, int offset, Dimension size, int color) {
            graphics.pushStyle();
            graphics.strokeWeight(1);
            graphics.noFill();
            graphics.stroke(color);
            // gaussianBlur size...
            graphics.rect(offset, offset, size.width, size.height);
            graphics.popStyle();
            //gaussianBlur(graphics, 12, 8f);
        }
    }
}


