package com.daveclay.processing.api;

import processing.core.PApplet;

public class FloatValueMeasurement {
    private float min = Float.MAX_VALUE;
    private float max = Float.MIN_VALUE;

    private float valuesUnderMax;

    public void add(float value) {
        if (value < Float.MIN_VALUE + 1 || value > Float.MAX_VALUE - 1) {
            return;
        }
        min = PApplet.min(min, value);
        max = PApplet.max(max, value);

        if (value < max) {
            valuesUnderMax++;
        }

        if (valuesUnderMax == 6000000) {
            valuesUnderMax = 0;
            max = Float.MIN_VALUE;
        }
    }

    public float getRange() {
        return max - min;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float mapValues(float value, float start, float stop) {

        return PApplet.map(value, min, max, start, stop);
    }
}
