package com.daveclay.processing.api;

import com.daveclay.processing.kinect.api.FloatValueMeasurement;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogSketch extends PApplet {

    private final HUD hud;
    private final int width;
    private final int height;

    public LogSketch(int width, int height) {
        this.width = width;
        this.height = height;
        this.hud = new HUD(this);
    }

    public LogSketch() {
        this(1800, 480);
    }

    @Override
    public void setup() {
        size(width, height);
    }

    public HUD getHud() {
        return hud;
    }

    public void draw() {
        background(255);
        hud.draw();
    }
}
