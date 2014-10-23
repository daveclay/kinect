package com.daveclay.processing.gestures;

import java.util.List;

public class GestureTemplate {
    public String name;
    public List<Point2D> points;

    public GestureTemplate(String name, List<Point2D> points) {
        this.name = name;
        this.points = points;
    }
}
