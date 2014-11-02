package com.daveclay.processing.gestures.utils;

import com.daveclay.processing.gestures.Point2D;

import java.util.ArrayList;
import java.util.List;

import static com.daveclay.processing.gestures.utils.Centroid.centroid;

public class Rotate {

    public static List<Point2D> rotateToZero(List<Point2D> points) {
        Point2D centroid = centroid(points);
        Point2D point2D = points.get(0);
        float rotation = (float)Math.atan2(centroid.y - point2D.y, centroid.x - point2D.x);
        return rotateBy(points, -rotation);
    }

    public static List<Point2D> rotateBy(List<Point2D> points, float rotation) {
        Point2D c = centroid(points);
        //--- can't name cos; creates compiler error since VC++ can't
        //---  tell the difference between the variable and function
        float cosine = (float)Math.cos(rotation);
        float sine = (float) Math.sin(rotation);

        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            float qx = (point.x - c.x) * cosine - (point.y - c.y) * sine + c.x;
            float qy = (point.x - c.x) * sine + (point.y - c.y) * cosine + c.y;
            newPoints.add(new Point2D(qx, qy));
        }
        return newPoints;
    }
}
