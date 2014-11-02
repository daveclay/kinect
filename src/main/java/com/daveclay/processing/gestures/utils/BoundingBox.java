package com.daveclay.processing.gestures.utils;

import com.daveclay.processing.gestures.Point2D;

import java.util.List;

public class BoundingBox {

    public static BoundingBox find(List<Point2D> points) {
        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (Point2D point : points) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }

        return new BoundingBox(minX, minY, (maxX - minX), (maxY - minY));
    }

    public float x;
    public float y;
    public float width;
    public float height;

    BoundingBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
