package com.daveclay.processing.gestures;

import java.util.List;

public class GestureData {
    public String name;
    public List<Point2D> points;

    public GestureData() {
    }

    public GestureData(String name, List<Point2D> points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public void setPoints(List<Point2D> points) {
        this.points = points;
    }
}
