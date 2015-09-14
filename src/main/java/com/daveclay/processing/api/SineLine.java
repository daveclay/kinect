package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class SineLine extends Drawing {
    PVector p1;
    PVector p2;
    float freq;
    float amp;

    public SineLine(PApplet canvas) {
        super(canvas);
    }

    public void sineTo(PVector p1, PVector p2, float freq, float amp) {
        this.p1 = p1;
        this.p2 = p2;
        this.freq = freq;
        this.amp = amp;
    }

    public void draw() {
        if (p1.mag() > 500000 || p2.mag() > 500000) {
            // Just some arbitrary limits to prevent blowing the heap creating verticies.
            // Kinect returns something like max int when the user disappears.
            return;
        }

        float d = PVector.dist(p1,p2);
        float a = PApplet.atan2(p2.y - p1.y, p2.x - p1.x);
        noFill();
        pushMatrix();
        translate(p1.x,p1.y);
        rotate(a);
        beginShape();
        for (float i = 0; i <= d; i += 1) {
            vertex(i, PApplet.sin(i * PConstants.TWO_PI * freq / d)*amp);
        }
        endShape();
        popMatrix();
    }
}
