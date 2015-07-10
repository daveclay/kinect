package com.daveclay.processing.kinect.bodylocator;

import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;
import processing.core.PVector;

public class HandBox {

    PVector center = new PVector();
    int color;
    int size = 20;
    int alpha = 255;
    Drawing drawGestureRecognized;

    public HandBox(PApplet canvas) {
        drawGestureRecognized = new Drawing(canvas) {
            public void draw() {
                pushStyle();
                strokeWeight(3);
                fill(red(color), blue(color), green(color), (int) (alpha * .5));
                stroke(red(color), blue(color), green(color), alpha);
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
