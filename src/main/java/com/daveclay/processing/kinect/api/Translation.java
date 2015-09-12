package com.daveclay.processing.kinect.api;

public class Translation {
    public final float x;
    public final float y;

    public Translation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return this.x + "," + this.y;
    }
}
