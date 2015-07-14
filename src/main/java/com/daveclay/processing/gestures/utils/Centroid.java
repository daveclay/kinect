package com.daveclay.processing.gestures.utils;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Centroid {

    public static List<PVector> translateToOrigin(List<PVector> points, PVector c) {
        List<PVector> newPoints = new ArrayList<>();
        for (PVector point : points) {
            float qx = point.x - c.x;
            float qy = point.y - c.y;
            newPoints.add(new PVector(qx, qy));
        }
        return newPoints;
    }

    /**
     * Shift the points so that the center is at 0,0.
     * That way, if everyone centers at the same place, we can measure
     * the distance between each pair of points without worrying about
     * where each point was originally drawn
     * If we didn't do this, shapes drawn at the top of the screen
     * would have a hard time matching shapes drawn at the bottom
     * of the screen
     */
    public static List<PVector> translateToOrigin(List<PVector> points) {
        PVector c = centroid(points);
        return translateToOrigin(points, c);
    }

    public static PVector centroid(List<PVector> points) {
        float x = 0f, y = 0f;
        for (PVector point : points) {
            x += point.x;
            y += point.y;
        }
        x /= points.size();
        y /= points.size();
        return new PVector(x, y);
    }
}
