package com.daveclay.processing.api;

import com.daveclay.processing.kinect.api.FloatValueMeasurement;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;
import java.util.Map;

public class HUD {

    private final PApplet canvas;
    private Map<String, String> lines = new LinkedHashMap<String, String>();
    private int fontSize = 40;
    private int color = 0;

    public HUD(PApplet canvas) {
        this.canvas = canvas;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void logRounded(String label, double value) {
        log(label, Double.toString(Math.round(value)));
    }

    public void logRounded(String label, float value) {
        logRounded(label, "", value);
    }

    public void logRounded(String label, String prefix, double value) {
        log(label, prefix + " " + Long.toString(Math.round(value)));
    }

    public void logRounded(String label, String prefix, float value) {
        log(label, prefix + " " + Integer.toString(Math.round(value)));
    }

    public void logVector(String label, PVector vector) {
        log(label, "x: " + vector.x + ", y: " + vector.y + ", z: " + vector.z);
    }

    public void log(String label, FloatValueMeasurement measurement) {
        log(label, "min: " + measurement.getMin() +
                        " max: " + measurement.getMax() +
                        " range: " + measurement.getRange()
        );
    }

    public void log(String label, float value) {
        log(label, "" + value);
    }

    public void logScreenCoords(String label, PVector vector) {
        log(label, "x: " + Math.round(vector.x) + ", y: " + Math.round(vector.y));
    }

    public synchronized void log(String label, String info) {
        lines.put(label, info);
    }

    public void draw() {
        canvas.fill(color);
        canvas.textSize(fontSize);
        writeLines();
    }

    synchronized void writeLines() {
        int yIncrement = fontSize + 3;
        int y = yIncrement;
        for (Map.Entry<String, String> entry : lines.entrySet()) {
            canvas.text(entry.getKey() + ": " + entry.getValue(), 10, y);
            y += yIncrement;
        }
    }

    public void log(String key, boolean value) {
        log(key, value ? "YES" : "NO");
    }
}
