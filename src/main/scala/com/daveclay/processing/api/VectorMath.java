package com.daveclay.processing.api;

import processing.core.PVector;

public class VectorMath {

    public static PVector reflectVertically(PVector vector) {
        PVector verticalNormal = new PVector(1, 0, 0);
        PVector v = vector.get();
        verticalNormal.mult(2f * v.dot(verticalNormal));
        v.sub(verticalNormal);
        return v;
    }
}
