package com.daveclay.processing.kinect.bodylocator;

import com.daveclay.processing.api.ColorUtils;
import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;
import processing.core.PVector;

public class HandBox {

    PVector center = new PVector();
    int size = 20;
    int fillColor;
    Drawing drawGestureRecognized;

    public HandBox(PApplet canvas, int color) {
        fillColor = ColorUtils.addAlpha(color, .5f);
        drawGestureRecognized = new Drawing(canvas) {
            public void draw() {
                pushStyle();
                strokeWeight(3);
                fill(fillColor);
                stroke(color);
                rect(center.x, center.y, size, size);
                popStyle();
            }
        };
    }

    public void drawAt(PVector position) {
        center.set(position);
        drawGestureRecognized.draw();
    }
}
