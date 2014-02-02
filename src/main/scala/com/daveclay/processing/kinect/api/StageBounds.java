package com.daveclay.processing.kinect.api;

import processing.core.PVector;

public class StageBounds {

    private float front = Integer.MAX_VALUE; // positive
    private float back = 0; // also positive, but bigger.
    private float right = Integer.MAX_VALUE; // right goes negative
    private float left = 0; // left goes positive
    private float bottom = Integer.MAX_VALUE;
    private float top = 0;

    private boolean ignoreZeros = true;

    public void updatePosition(PVector position) {
        if (ignoreZeros && isZero(position)) {
            return;
        }
        if (position.z < front) {
            front = position.z;
        }
        if (position.z > back) {
            back = position.z;
        }
        if (position.x < right) {
            right = position.x;
        }
        if (position.x > left) {
            left = position.x;
        }
        if (position.y < bottom) {
            bottom = position.y;
        }
        if (position.y > top) {
            top = position.y;
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
        float y = (top + bottom) / 2;

        if (result != null) {
            result.set(x, y, z);
            return result;
        } else {
            return new PVector(x, y, z);
        }
    }

    public float getBottom() {
        return bottom;
    }

    public float getTop() {
        return top;
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

    public float getDepth() {
        return back - front;
    }

    public float getWidth() {
        return Math.abs(left) + Math.abs(right);
    }
}
