package com.daveclay.processing.gestures.utils;

import com.daveclay.processing.gestures.Point2D;

import java.util.List;

public class Centroid {

    public static Point2D centroid(List<Point2D> points) {
        float x = 0f, y = 0f;
        for (Point2D point : points) {
            x += point.x;
            y += point.y;
        }
        x /= points.size();
        y /= points.size();
        return new Point2D(x, y);
    }
}
