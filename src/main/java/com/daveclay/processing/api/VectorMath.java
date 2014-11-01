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

    public static double getZDistanceSquared(PVector center, PVector location) {
        return Math.pow(location.z - center.z, 2);
    }

    public static double getDistanceSquared(PVector center, PVector location) {
        return Math.pow(location.x - center.x, 2)
                + Math.pow(location.y - center.y, 2)
                + Math.pow(location.z - center.z, 2);
    }

    public static boolean isWithinZ(PVector center, PVector location, float radius) {
        return getZDistanceSquared(center, location) <= Math.pow(radius, 2);
    }

    public static boolean isWithin(PVector center, PVector location, float radius) {
        return getDistanceSquared(center, location) <= Math.pow(radius, 2);
    }
}
