package com.daveclay.processing.api;

import processing.core.PApplet;

public class Noise2D {

    private final PApplet pApplet;
    double baseCoordinate;
    double tick;
    double rate = .00001f;
    float scale = 1f;

    public Noise2D(PApplet pApplet, double rate) {
        this(pApplet, rate, pApplet.random(10), pApplet.random(10));
    }

    public Noise2D(PApplet pApplet, double rate, double baseCoordinate, double tick) {
        this.pApplet = pApplet;
        this.baseCoordinate = baseCoordinate;
        this.rate = rate;
        this.tick = tick;
    }

    public Noise2D newRelated(double relatedAmount) {
        return new Noise2D(pApplet, rate, baseCoordinate + relatedAmount, tick);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float next() {
        // at 2048, adding .00001 doesn't change 2048. Because doubles.
        tick += rate;
        return pApplet.noise((float)baseCoordinate, (float)tick, .0321f) * scale;
    }
}
