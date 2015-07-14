package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Drawing;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

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
        StageRect frontLeft = new StageRect(this, StageRect.FRONT_LEFT, size);
        StageRect frontRight = new StageRect(this, StageRect.FRONT_RIGHT, size);
        StageRect backLeft = new StageRect(this, StageRect.BACK_LEFT, size);
        StageRect backRight = new StageRect(this, StageRect.BACK_RIGHT, size);

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

    static class StageRect extends Drawing {
        public static final int FRONT_LEFT = 1;
        public static final int FRONT_RIGHT = 2;
        public static final int BACK_LEFT = 3;
        public static final int BACK_RIGHT = 4;

        private float size;
        private float circleWidth;
        private int corner = FRONT_LEFT;
        private PShape shape;

        public StageRect(PApplet canvas, int corner, float size) {
            super(canvas);
            this.corner = corner;
            this.size = size;
        }

        public void intersect(float circleWidth) {
            this.circleWidth = circleWidth;
        }

        public void draw() {
            shape = createShape();
            shape.beginShape();

            shape.vertex(0, 0);
            shape.vertex(size, 0);
            shape.vertex(size, size);
            shape.vertex(0, size);
            shape.vertex(0, 0);

            contour();

            shape.endShape();

            shape(shape, 0, 0);
        }

        private void contour() {
            shape.beginContour();

            float circleX, circleY;
            if (corner == FRONT_LEFT) {
                circleX = size - circleWidth / 2;
                circleY = circleX;
            } else if (corner == BACK_RIGHT) {
                circleX = -1 * (circleWidth / 2);
                circleY = circleX;
            } else if (corner == BACK_LEFT) {
                circleX = size - circleWidth / 2;
                circleY = -1 * (circleWidth / 2);
            } else {
                circleY = size - circleWidth / 2;
                circleX = -1 * (circleWidth / 2);
            }
            PShape circle = createShape(ELLIPSE, circleX, circleY, circleWidth, circleWidth);
            for (int i = 1; i < circle.getVertexCount(); i++) {
                PVector vertex = circle.getVertex(i);
                float x = vertex.x;
                float y = vertex.y;
                if (x > size) {
                    x = size;
                }
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
                if (y > size) {
                    y = size;
                }
                shape.vertex(x, y);
            }
            shape.endContour();
        }
    }
}
