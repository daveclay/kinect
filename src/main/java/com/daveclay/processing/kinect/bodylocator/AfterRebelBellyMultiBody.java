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
    PFont orator13;
    PFont orator18;
    PFont orator23;
    PFont orator36;

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
                orator13 = createFont("OratorStd", 13);
                orator18 = createFont("OratorStd", 18);
                orator23 = createFont("OratorStd", 23);
                orator36 = createFont("OratorStd", 36);

                noiseColor = new NoiseColor(AfterRebelBellyMultiBody.this, .01f);

                screenBlur = createGraphics(width, height, P2D);
                multiplyMe = createGraphics(width, height, P2D);
                bg(screenBlur);

                createBodies();

                background(255);
            }
        });

        registerEventListeners();
    }

    void createBodies() {
        for (int i = 0; i < 8; i++) {
            Body body = new Body(this, i, hud);
            this.bodiesById.put(i, body);
        }
    }

    public void keyPressed() {
        if (Character.isDigit(key)) {
            Body body = bodiesById.get(Integer.parseInt(String.valueOf(key)));
            if (body != null) {
                body.toggleFake();
            }
        }
    }

    protected void registerEventListeners() {
        onUserEntered(user -> {
            hud.log("Detected Body " + user.getID(), "");
            Body body = this.bodiesById.get(user.getID());
            if (body == null) {
                body = new Body(this, user, hud);
                this.bodiesById.put(user.getID(), body);
                System.out.println("No Body found for ID " + user.getID());
            } else {
                body.userActive(user);
            }
        });

        onUserWasLost(user -> {
            hud.log("Lost Body " + user.getID(), "");
            Body body = this.bodiesById.get(user.getID());
            if (body != null) {
                body.userActive(null);
            }
        });
    }

    private void drawBodies() {
        background(0);

        bodiesById.values().forEach(body -> {
            body.updatePosition();
        });

        // draw onto the ScreenBlur
        bodiesById.values().forEach(body -> {
            body.drawScreenBlur();
        });

        screenBlur.beginDraw();
        Body previous = null;
        screenBlur.pushStyle();
        screenBlur.strokeWeight(1);
        for (Body body : bodiesById.values()) {
            if (body.active) {
                blurConnect(body, previous);
                previous = body;
            }
        }
        screenBlur.popStyle();

        blur(screenBlur, 9, 2f);
        screenBlur.filter(badBlur);
        screenBlur.endDraw();

        image(screenBlur, 0, 0);

        bodiesById.values().forEach(Body::draw);
        previous = null;

        stroke(color(255, 0, 0, 200));
        strokeWeight(1);
        noFill();
        for (Body body : bodiesById.values()) {
            if (body.active) {
                if (previous != null) {
                    connect(body, previous);
                }
                previous = body;
            }
        }

        //blur();
        //colorSeparator.set("time", (float) millis() / 1000f);
        //filter(colorSeparator);
        drawHUD();
    }

    void connect(Body body, Body previous) {
        PVector left = body.leftHandPosition2d.get();
        PVector right = previous.rightHandPosition2d.get();

        PVector v = new PVector(body.size.width, body.size.height);
        v.div(2f);

        left.add(v);
        right.add(v);

        line(right.x, right.y, left.x, left.y);
    }

    void blurConnect(Body body, Body previous) {
        PVector leftHandPosition2d = body.leftHandPosition2d.get();
        PVector rightHandPosition2d = body.rightHandPosition2d.get();

        PVector v = new PVector(body.size.width, body.size.height);
        v.div(2f);

        leftHandPosition2d.add(v);
        rightHandPosition2d.add(v);

        lineTo(leftHandPosition2d, rightHandPosition2d);
        if (previous != null) {
            lineTo(body.rightHandPosition2d, previous.leftHandPosition2d);
        }
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
        int id;
        User user;
        HUD hud;
        PGraphics sprite;
        NoiseColor noiseColor;

        PVector missingLocation;
        PVector leftHandPosition2d;
        PVector rightHandPosition2d;
        Dimension size;
        int offset = 10;

        boolean active;

        boolean fake;
        FakePVectorGenerator leftPVectorGenerator;
        FakePVectorGenerator rightPVectorGenerator;

        public Body(PApplet canvas,
                    int id,
                    HUD hud) {
            this(canvas, id, null, hud);
        }

        public Body(PApplet canvas,
                    User user,
                    HUD hud) {
            this(canvas, user.getID(), user, hud);
        }

        public Body(PApplet canvas,
                    int id,
                    User user,
                    HUD hud) {
            super(canvas);
            this.id = id;
            this.size = defaultSize();
            this.offset = 10;
            this.user = user;
            this.hud = hud;
            this.sprite = canvas.createGraphics(400, 400, P2D);
            bg(this.sprite);
            noiseColor = new NoiseColor(canvas, .01f);
            missingLocation = new PVector(id * (width / 6), 213);
        }


        public void toggleFake() {
            fake = !fake;
            if (fake) {
                active = true;
            } else if (user == null) {
                active = false;
                userActive(null);
            }
            leftPVectorGenerator = new FakePVectorGenerator(canvas, random(.015f) + .005f);
            rightPVectorGenerator = new FakePVectorGenerator(canvas, random(.015f) + .005f);
        }

        private Dimension defaultSize() {
            return new Dimension(60, 60);
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
            textFont(orator13);
            text("SEND::" + center.y, center.x + 110, center.y + 40);
            popStyle();
        }

        public void updatePosition() {
            if (!active) {
                return;
            }
            if (user == null) {
                this.leftHandPosition2d = leftPVectorGenerator.next();
                this.rightHandPosition2d = rightPVectorGenerator.next();
            } else {
                leftHandPosition2d = scale(user.getLeftHandPosition2D());
                leftHandPosition2d.z = user.getLeftHandPosition().z;
                rightHandPosition2d = scale(user.getRightHandPosition2D());
                rightHandPosition2d.z = user.getLeftHandPosition().z;

                hud.log("Z", leftHandPosition2d.z);
            }
            scaleSize();
        }

        void scaleSize() {
            int s = (int) max(60, map(leftHandPosition2d.z, 0, 3, 260, 60));
            size = new Dimension(s, s);
        }

        PVector scale(PVector location) {
            int x = (int) map(location.x, 0, getKinectImage().width, 0, width);
            int y = (int) map(location.y, 0, getKinectImage().height, 0, height);
            return new PVector(x, y);
        }

        public void drawScreenBlur() {
            if (active) {
                drawScreenBlur(leftHandPosition2d);
                drawScreenBlur(rightHandPosition2d);
            } else {
                if (random(10) > 8) {
                    /*
                    screenBlur.beginDraw();
                    screenBlur.blendMode(SCREEN);
                    screenBlur.stroke(color(255, random(120), 0));
                    screenBlur.noFill();
                    screenBlur.rect(missingLocation.x, missingLocation.y, size.width, size.height);
                    screenBlur.endDraw();
                    */
                }
            }
        }

        public void drawScreenBlur(PVector location) {
            sprite.beginDraw();
            sprite.background(0);
            int color = noiseColor.nextColor(255);
            // add red! blend it in? How? reduce others by some amount?
            // This should probably be Z, because Y's going to be hard to reach.
            int red = (int) min(255, map(location.y, 0, height - 400, 0, 255));
            if (location.y > height - 300) {
                color = color(red, 0, 0);
            }
            color = ColorUtils.setRed(red, color);
            drawRect(sprite, offset, size, color);
            sprite.endDraw();

            screenBlur.beginDraw();
            screenBlur.blendMode(SCREEN);
            screenBlur.image(sprite, location.x, location.y);
            screenBlur.endDraw();
        }

        public void draw() {
            if (active) {
                drawUser();
            } else {
                drawMissing();
            }
        }

        void drawMissing() {
            pushStyle();
            int color = color(random(235), random(50), 0);
            stroke(color);
            crossRect(missingLocation);
            fill(color);
            terminalText(new String[]{
                    "BODY." + id + ".MISSING",
                    "0x" + Integer.toHexString((int) random(100)).toUpperCase(),
                    "[" + missingLocation.x + "," + missingLocation.y + "]"
            }, missingLocation);
            popStyle();

        }

        void drawUser() {
            drawUserRect(leftHandPosition2d);
            drawUserRect(rightHandPosition2d);
            connectHands();
        }

        void connectHands() {
            PVector leftHandPosition2d = this.leftHandPosition2d.get();
            PVector rightHandPosition2d = this.rightHandPosition2d.get();

            PVector v = new PVector(size.width, size.height);
            v.div(2f);

            leftHandPosition2d.add(v);
            rightHandPosition2d.add(v);

            pushStyle();
            stroke(color(180, 100));
            line(leftHandPosition2d.x, leftHandPosition2d.y, rightHandPosition2d.x, rightHandPosition2d.y);
            popStyle();;
        }

        void drawUserRect(PVector location) {
            int x = (int) location.x;
            int y = (int) location.y;
            pushStyle();
            stroke(color(180, 100));
            crossRect(location);
            fill(color(255, 255, 255, 120));
            terminalText(new String[]{
                    "BODY." + id,
                    "0x" + Integer.toHexString(frameCount).toUpperCase(),
                    "[" + x + "," + y + "]"
            }, location);
            popStyle();
        }

        void terminalText(String[] lines, PVector location) {
            int x = (int) location.x;
            int y = (int) location.y;
            textFont(orator23);
            for (int i = 0; i < lines.length; i++) {
                text(lines[i], x + size.width + offset + 6, y + 23 + (26 * i));
            }
        }

        void crossRect(PVector location) {
            int x = (int) location.x;
            int y = (int) location.y;
            pushStyle();
            strokeWeight(1);
            noFill();
            rect(x + offset, y + offset, size.width, size.height);
            line(x + offset, y + offset, x + size.width + offset, y + size.height + offset);
            line(x + offset, y + size.height + offset, x + size.width + offset, y + offset);
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

        public void userActive(User user) {
            this.user = user;
            if (user == null) {
                this.size = defaultSize();
            }
        }
    }

    class FakePVectorGenerator {
        private Noise2D xNoise;
        private Noise2D yNoise;
        private Noise2D zNoise;

        public FakePVectorGenerator(PApplet canvas, double rate) {
            this.xNoise = new Noise2D(canvas, rate);
            this.yNoise = new Noise2D(canvas, rate);
            this.zNoise = new Noise2D(canvas, .008f);

            xNoise.setScale(canvas.width + 600);
            yNoise.setScale(canvas.height + 600);
            zNoise.setScale(2);
        }

        public PVector next() {
            return new PVector(-200 + xNoise.next(), -200 + yNoise.next(), zNoise.next() + 1f);
        }
    }
}


