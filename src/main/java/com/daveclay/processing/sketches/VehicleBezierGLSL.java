package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Field;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.Vehicle;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PShader;

public class VehicleBezierGLSL extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new VehicleBezierGLSL());
    }

    ImgProc imgProc;
    PShader blur;
    PShader gaussianBlur;

    Field fieldA;
    Field fieldB;
    Vehicle controlPoint1;
    Vehicle controlPoint2;
    Vehicle anchorPoint1;
    Vehicle anchorPoint2;
    Vehicle target1;
    Vehicle target2;

    float noiseR = random(7536451);
    float noiseG = random(16432);
    float noiseB = random(64311);

    boolean blurOn = false;

    public void mouseClicked() {
        blurOn = !blurOn;
    }

    public void setup() {
        size(displayWidth, displayHeight, P2D);
        background(0);
        imgProc = new ImgProc(this);

        blur = ImgProc.shader(this, "badBlur");
        gaussianBlur = ImgProc.shader(this, "gaussianBlur");
        gaussianBlur.set("kernelSize", 12); // How big is the sampling kernel?
        gaussianBlur.set("strength", 8f); // How strong is the gaussianBlur?

        fieldA = new Field(50, this);
        fieldB = new Field(50, this);
        controlPoint1 = new Vehicle(this, 100, 100);
        controlPoint2 = new Vehicle(this, 500, 300);

        controlPoint1.maxspeed = controlPoint2.maxspeed = 16;
        controlPoint1.maxforce = controlPoint2.maxforce = 1f;

        anchorPoint1 = new Vehicle(this, 0, height/2);
        anchorPoint2 = new Vehicle(this, width, height/2);
        target1 = new Vehicle(this, random(0, width), random(0, height));
        target2 = new Vehicle(this, random(0, width), random(0, height));
        frameRate(30);
    }

    void incrementFade() {
        if (frameCount % 10 == 0) {
            fill(color(0, 0, 0, 8));
            rect(0, 0, width, height);
            noFill();
        }
    }

    public void draw() {
        blendMode(SCREEN);
        PVector mouse = new PVector(mouseX, mouseY);
        target1.seek(mouse);
        target2.seek(mouse);

        controlPoint1.flow(fieldA.lookup(controlPoint1.location));
        controlPoint2.flow(fieldB.lookup(controlPoint2.location));
        anchorPoint1.flow(fieldA.lookup(anchorPoint1.location));
        anchorPoint2.flow(fieldB.lookup(anchorPoint2.location));

        controlPoint1.update();
        controlPoint2.update();
        anchorPoint1.update();
        anchorPoint2.update();
        target1.update();
        target2.update();

        int color = color(
                noise(noiseR += 1) * 255,
                noise(noiseR += 1) * 255,
                noise(noiseR += 1) * 255
        );
        strokeWeight(2);
        stroke(color);

        int fillColor = (color & 0xffffff) | (3 << 24);
        noFill();
        // fill(fillColor);

        beginShape();
        vertex(anchorPoint1.location.x, anchorPoint1.location.y);
        bezierVertex(
                controlPoint1.location.x,
                controlPoint1.location.y,
                controlPoint2.location.x,
                controlPoint2.location.y,
                anchorPoint2.location.x,
                anchorPoint2.location.y
        );
        endShape();

        /*
        drawControls();
        fieldA.draw(color(255, 0, 0));
        fieldB.draw(color(0, 255, 0));
        */

        //imgProc.simpleBrightness(1.1f);
        blur();

        fieldA.initialize();
        fieldB.initialize();
    }

    public void drawControls() {
        drawControlPoint(controlPoint1, color(255, 0, 0));
        drawControlPoint(controlPoint2, color(0, 255, 0));
    }

    void drawControlPoint(Vehicle controlPoint, int color) {
        stroke(color);
        rect(
                controlPoint.location.x,
                controlPoint.location.y,
                3,
                3);

    }

    PVector randomTarget(float offset) {
        return new PVector(random(0, width - offset) + offset, random(0, height - offset) + offset);
    }

    void blur() {
        gaussianBlur.set("horizontalPass", 0);
        filter(gaussianBlur);

        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        filter(gaussianBlur);

        filter(blur);
    }
}
