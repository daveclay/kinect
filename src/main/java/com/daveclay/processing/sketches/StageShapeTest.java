package com.daveclay.processing.sketches;

import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.StageRect;
import processing.core.PApplet;

public class StageShapeTest extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new StageShapeTest());
    }

    public void setup() {
        size(1400, 768, P2D);
    }

    public void draw() {
        background(128);

        int size = 200;
        StageRect frontLeft = new StageRect(this, StageRect.FRONT_LEFT);
        StageRect frontRight = new StageRect(this, StageRect.FRONT_RIGHT);
        StageRect backLeft = new StageRect(this, StageRect.BACK_LEFT);
        StageRect backRight = new StageRect(this, StageRect.BACK_RIGHT);

        frontLeft.size(size);
        frontRight.size(size);
        backLeft.size(size);
        backRight.size(size);

        int circleWidth = mouseX;
        frontLeft.intersect(circleWidth);
        frontRight.intersect(circleWidth);
        backLeft.intersect(circleWidth);
        backRight.intersect(circleWidth);

        pushMatrix();
        noStroke();
        fill(255, 0, 0, 80);
        frontLeft.draw();

        fill(0, 255, 0, 80);
        translate(size, 0);
        frontRight.draw();

        fill(0, 0, 255, 80);
        translate(-size, size);
        backLeft.draw();

        fill(255, 0, 255, 80);
        translate(size, 0);
        backRight.draw();
        popMatrix();

        fill(0, 255, 255, 80);
        ellipse(size, size, circleWidth, circleWidth);
    }

}
