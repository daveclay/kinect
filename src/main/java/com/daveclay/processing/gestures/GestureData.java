package com.daveclay.processing.gestures;

import processing.core.PVector;

import java.util.List;

public class GestureData {
    public String name;
    public List<PVector> points;

    public GestureData() {
    }

    public GestureData(String name, List<PVector> points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PVector> getPoints() {
        return points;
    }

    public void setPoints(List<PVector> points) {
        this.points = points;
    }
}
