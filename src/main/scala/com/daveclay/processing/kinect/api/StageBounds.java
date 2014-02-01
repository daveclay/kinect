package com.daveclay.processing.kinect.api;

import processing.core.PVector;

public class StageBounds {
    private float front = Integer.MAX_VALUE; // positive
    private float back = 0f; // also positive, but bigger.
    private float right = Integer.MAX_VALUE; // right goes negative
    private float left = 0; // left goes positive

    private boolean ignoreZeros = true;

    public void track(PVector location) {
        if (ignoreZeros && isZero(location)) {
            return;
        }
        if (location.z < front) {
            front = location.z;
        }
        if (location.z > back) {
            back = location.z;
        }
        if (location.x < right) {
            right = location.x;
        }
        if (location.x > left) {
            left = location.x;
        }
    }

    public boolean isZero(PVector vector) {
        return vector.mag() == 0;
    }

    public PVector getCenter() {
        return getCenter(null);
    }

    public PVector getCenter(PVector result) {
        float z = (back + front) / 2;
        float x = (left + right) / 2;

        if (result != null) {
            result.set(x, 0, z);
            return result;
        } else {
            return new PVector(x, 0, z);
        }
    }

    public float getFront() {
        return front;
    }

    public float getBack() {
        return back;
    }

    public float getRight() {
        return right;
    }

    public float getLeft() {
        return left;
    }
}
