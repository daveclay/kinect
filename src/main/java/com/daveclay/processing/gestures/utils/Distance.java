package com.daveclay.processing.gestures.utils;

import processing.core.PVector;

import java.util.List;

public class Distance {

    public static float findDistanceSquared(PVector p1, PVector p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        return (dx * dx) + (dy * dy);
    }

    public static float findDistance(PVector p1, PVector p2) {
        float distanceSquared = findDistanceSquared(p1, p2);
        return (float) Math.sqrt(distanceSquared);
    }

    public static float pathDistance(List<PVector> pts1, List<PVector> pts2) {
        // assumes pts1.size == pts2.size

        float distance = 0f;
        for (int i = 0; i < pts1.size(); i++) {
            distance += findDistance(pts1.get(i), pts2.get(i));
        }

        // average distance for all points
        return (distance / pts1.size());
    }
}
