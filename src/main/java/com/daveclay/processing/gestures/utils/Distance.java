package com.daveclay.processing.gestures.utils;

import com.daveclay.processing.gestures.Point2D;

import java.util.List;

public class Distance {

    public static float findDistanceSquared(Point2D p1, Point2D p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        return (dx * dx) + (dy * dy);
    }

    public static float findDistance(Point2D p1, Point2D p2) {
        float distanceSquared = findDistanceSquared(p1, p2);
        return (float) Math.sqrt(distanceSquared);
    }

    public static float pathDistance(List<Point2D> pts1, List<Point2D> pts2) {
        // assumes pts1.size == pts2.size

        float distance = 0f;
        for (int i = 0; i < pts1.size(); i++) {
            distance += findDistance(pts1.get(i), pts2.get(i));
        }

        // average distance for all points
        return (distance / pts1.size());
    }
}
