package com.daveclay.processing.gestures;

public class Point2D {
    //--- Wobbrock used doubles for these, not ints
    //int x, y;
    double x, y;
    float fx, fy;

    public Point2D() {
        this(0, 0);
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.fx = (float) x;
        this.fy = (float) y;
    }
}
