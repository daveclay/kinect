package com.daveclay.processing.gestures.utils;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static com.daveclay.processing.gestures.utils.Centroid.centroid;

public class Rotate {

    public static List<PVector> rotateToZero(List<PVector> points) {
        PVector centroid = centroid(points);
        PVector point2D = points.get(0);
        float rotation = (float)Math.atan2(centroid.y - point2D.y, centroid.x - point2D.x);
        return rotateBy(points, -rotation);
    }

    public static List<PVector> rotateBy(List<PVector> points, float rotation) {
        PVector c = centroid(points);
        //--- can't name cos; creates compiler error since VC++ can't
        //---  tell the difference between the variable and function
        float cosine = (float)Math.cos(rotation);
        float sine = (float) Math.sin(rotation);

        List<PVector> newPoints = new ArrayList<PVector>();
        for (PVector point : points) {
            float qx = (point.x - c.x) * cosine - (point.y - c.y) * sine + c.x;
            float qy = (point.x - c.x) * sine + (point.y - c.y) * cosine + c.y;
            newPoints.add(new PVector(qx, qy));
        }
        return newPoints;
    }
}
