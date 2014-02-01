package com.daveclay.processing.kinect.api;

import processing.core.PVector;

public class StageBounds {
    private float nearest = Integer.MAX_VALUE; // positive
    private float furthest = 0f; // also positive, but bigger.
    private float rightmost = Integer.MAX_VALUE; // right goes negative
    private float leftmost = 0; // left goes positive

    private boolean ignoreZeros = true;

    public void track(PVector location) {
        if (ignoreZeros && isZero(location)) {
            return;
        }
        if (location.z < nearest) {
            nearest = location.z;
        }
        if (location.z > furthest) {
            furthest = location.z;
        }
        if (location.x < rightmost) {
            rightmost = location.x;
        }
        if (location.x > leftmost) {
            leftmost = location.x;
        }
    }

    public boolean isZero(PVector vector) {
        return vector.mag() == 0;
    }

    public PVector getCenter() {
        return getCenter(null);
    }

    public PVector getCenter(PVector result) {
        float z = (furthest + nearest) / 2;
        float x = (leftmost + rightmost) / 2;

        if (result != null) {
            result.set(x, 0, z);
            return result;
        } else {
            return new PVector(x, 0, z);
        }
    }

    public float getNearest() {
        return nearest;
    }

    public float getFurthest() {
        return furthest;
    }

    public float getRightmost() {
        return rightmost;
    }

    public float getLeftmost() {
        return leftmost;
    }
}
