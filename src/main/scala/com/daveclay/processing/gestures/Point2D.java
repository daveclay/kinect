package com.daveclay.processing.gestures;

public class Point2D {
    //--- Wobbrock used doubles for these, not ints
    //int x, y;
    double x, y;

    public Point2D() {
        this.x = 0;
        this.y = 0;
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
