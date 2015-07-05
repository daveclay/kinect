package com.daveclay.processing.gestures.utils;

import com.daveclay.processing.gestures.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Centroid {

    public static List<Point2D> translateToOrigin(List<Point2D> points, Point2D c) {
        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            float qx = point.x - c.x;
            float qy = point.y - c.y;
            newPoints.add(new Point2D(qx, qy));
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
    public static List<Point2D> translateToOrigin(List<Point2D> points) {
        Point2D c = centroid(points);
        return translateToOrigin(points, c);
    }

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
