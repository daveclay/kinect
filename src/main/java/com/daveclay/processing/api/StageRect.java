package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class StageRect extends Drawing {
    public static final int FRONT_LEFT = 1;
    public static final int FRONT_RIGHT = 2;
    public static final int BACK_LEFT = 3;
    public static final int BACK_RIGHT = 4;

    private float size;
    private float width;
    private float height;
    private int corner = FRONT_LEFT;
    private PShape shape;

    public StageRect(PApplet canvas, int corner) {
        super(canvas);
        shape = createShape();
        this.corner = corner;
    }

    public void size(float size) {
        this.size = size;
    }

    public void intersect(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void intersect(float circleWidth) {
        this.width = this.height = circleWidth;
    }

    public void draw() {
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
            circleX = size - width / 2;
            circleY = size - height / 2;
        } else if (corner == BACK_RIGHT) {
            circleX = -1 * (width / 2);
            circleY = -1 * (height / 2);
        } else if (corner == BACK_LEFT) {
            circleX = size - width / 2;
            circleY = -1 * (height / 2);
        } else {
            circleX = -1 * (width / 2);
            circleY = size - height / 2;
        }
        PShape circle = createShape(PConstants.ELLIPSE, circleX, circleY, width, height);
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
