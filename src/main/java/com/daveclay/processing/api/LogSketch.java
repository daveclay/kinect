package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogSketch extends PApplet {

    private final int width;
    private final int height;
    private int fontSize = 23;
    private Map<String, String> lines = new LinkedHashMap<String, String>();

    public LogSketch(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public LogSketch() {
        this(640, 480);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void setup() {
        size(width, height);
    }

    public void logRounded(String label, double value) {
        log(label, Double.toString(Math.round(value)));
    }

    public void logRounded(String label, float value) {
        log(label, Integer.toString(Math.round(value)));
    }

    public void logVector(String label, PVector vector) {
        log(label, "x: " + Math.round(vector.x) + ", y: " + Math.round(vector.y) + ", z: " + Math.round(vector.z));
    }

    public synchronized void log(String label, String info) {
        lines.put(label, info);
    }

    public void draw() {
        background(255);
        fill(0);
        textSize(fontSize);
        writeLines();
    }

    synchronized void writeLines() {
        int yIncrement = fontSize + 3;
        int y = yIncrement;
        for (Map.Entry<String, String> entry : lines.entrySet()) {
            text(entry.getKey() + ": " + entry.getValue(), 10, y);
            y += yIncrement;
        }
    }

    public void log(String key, boolean value) {
        log(key, value ? "YES": "NO");
    }
}
