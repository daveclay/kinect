package com.daveclay.processing.api;

import processing.core.PApplet;

public class Noise2D {

    private final PApplet pApplet;
    float baseCoordinate;
    float tick;
    float rate = .00001f;

    public Noise2D(PApplet pApplet, float rate) {
        this(pApplet, rate, pApplet.random(10), pApplet.random(10));
    }

    public Noise2D(PApplet pApplet, float rate, float baseCoordinate, float tick) {
        this.pApplet = pApplet;
        this.baseCoordinate = baseCoordinate;
        this.rate = rate;
        this.tick = tick;
    }

    public Noise2D newRelated(float relatedAmount) {
        return new Noise2D(pApplet, rate, baseCoordinate + relatedAmount, tick);
    }

    public float next() {
        tick += rate;
        return pApplet.noise(baseCoordinate, tick);
    }
}
