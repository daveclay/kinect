package com.daveclay.processing.sketches;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class StageShapeTest extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new StageShapeTest());
    }

    PShape s;

    public void setup() {
        size(1400, 768, P2D);
    }

    public void draw() {
        background(128);

        int circleWidth = mouseX;
        int boxWidth = 400;

        s = createShape();
        s.beginShape();

        s.vertex(0, 0);
        s.vertex(boxWidth, 0);
        s.vertex(boxWidth, boxWidth);
        s.vertex(0, boxWidth);
        s.vertex(0, 0);

        s.beginContour();
        PShape circle = createShape(ELLIPSE, boxWidth - circleWidth / 2, boxWidth - circleWidth / 2, circleWidth, circleWidth);
        //PShape circle = createShape(RECT, 0, 0, 80, 80);
        for (int i = 1; i < circle.getVertexCount(); i++) {
            PVector vertex = circle.getVertex(i);
            float x = vertex.x;
            float y = vertex.y;
            if (x > boxWidth) {
                x = boxWidth;
            }
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (y > boxWidth) {
                y = boxWidth;
            }
            s.vertex(x, y);
        }
        s.endContour();

// Finishing off shape
        s.endShape();

        noStroke();
        fill(255, 0, 0);
        shape(s, 100, 100);
    }
}
