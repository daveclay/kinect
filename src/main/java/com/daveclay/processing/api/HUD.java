package com.daveclay.processing.api;

import com.daveclay.processing.kinect.api.Translation;
import org.apache.commons.lang3.StringUtils;
import processing.core.PApplet;
import processing.core.PVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HUD {

    private PApplet defaultCanvas;
    private Map<String, String> lines = new LinkedHashMap<>();
    private int fontSize = 40;
    private int color = 255;
    private DecimalFormat format = new DecimalFormat("#.###");

    public HUD() {
    }

    public HUD(PApplet canvas) {
        this.defaultCanvas = canvas;
    }

    public void setDefaultCanvas(PApplet defaultCanvas) {
        this.defaultCanvas = defaultCanvas;
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
        log(label, prefix + " " + round(value));
    }

    public void logRounded(String label, String prefix, float value) {
        log(label, prefix + " " + round(value));
    }

    private synchronized String round(Number value) {
        return StringUtils.rightPad(format.format(value), 5);
    }

    public void logVector(String label, PVector vector) {
        log(label, "x: " + round(vector.x) + ", y: " + round(vector.y) + ", z: " + round(vector.z));
    }

    public void log(String label, FloatValueMeasurement measurement) {
        log(label, "min: " + round(measurement.getMin()) +
                        " max: " + round(measurement.getMax()) +
                        " range: " + round(measurement.getRange())
        );
    }

    public void log(String label, float value) {
        log(label, "" + value);
    }

    public void logScreenCoords(String label, PVector vector) {
        log(label, "x: " + round(vector.x) + ", y: " + round(vector.y));
    }

    public synchronized void log(String label, String info) {
        lines.put(label, info);
    }

    public void draw() {
        draw(this.defaultCanvas);
    }

    public synchronized void draw(PApplet canvas) {
        canvas.textSize(fontSize);
        canvas.noStroke();
        int yIncrement = fontSize + 3;
        int x = 10;

        int y = yIncrement;
        List<String> texts = buildTexts();
        for (String text: texts) {
            float width = canvas.textWidth(text);
            canvas.fill(canvas.color(0, 120));
            canvas.rect(x, y - yIncrement + 5, width, yIncrement);
            y += yIncrement;
        }

        y = yIncrement;
        for (String text: texts) {
            canvas.fill(color);
            canvas.text(text, x, y);
            y += yIncrement;
        }
    }

    private List<String> buildTexts() {
        List<String> text = new ArrayList<>();
        for (Map.Entry<String, String> entry : lines.entrySet()) {
            text.add(entry.getKey() + ": " + entry.getValue());
        }
        return text;
    }

    public void log(String key, boolean value) {
        log(key, value ? "YES" : "NO");
    }

    public void log(String key, Translation translation) {
        log(key, round(translation.x) + ", " + round(translation.y));
    }
}
