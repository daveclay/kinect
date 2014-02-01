package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Map;
import java.util.TreeMap;

public class InfoSketch extends PApplet {

    private final int width;
    private final int height;
    private Map<String, String> lines = new TreeMap<String, String>();
    private int fontSize = 23;

    public InfoSketch(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public InfoSketch() {
        this(640, 480);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void setup() {
        size(width, height);
    }

    public void logRoundedFloat(String key, float value) {
        log(key, Integer.toString(Math.round(value)));
    }

    public void logVector(String key, PVector vector) {
        log(key, "x: " + Math.round(vector.x) + ", y: " + Math.round(vector.y) + ", z: " + Math.round(vector.z));
    }

    public void log(String key, String info) {
        lines.put(key, info);
    }

    @Override
    public void draw() {
        background(255);
        fill(0);
        textSize(fontSize);
        int yIncrement = fontSize + 3;
        int y = yIncrement;
        for (Map.Entry<String, String> entry : lines.entrySet()) {
            text(entry.getKey() + ": " + entry.getValue(), 10, y);
            y += yIncrement;
        }
    }
}
