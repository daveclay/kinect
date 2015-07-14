package com.daveclay.processing.sketches;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;

import java.awt.*;

public class SplitColorAcrossIndexes extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new SplitColorAcrossIndexes());
    }

    public void setup() {
        size(1920, 1080, P2D);
    }

    public void draw() {
        int numberOfUsers = 8;
        float colorPlaces = 1f / numberOfUsers;

        int size = width / numberOfUsers;

        for (int i = 0; i < numberOfUsers; i++) {
            int color = Color.HSBtoRGB(colorPlaces * i, 1, .9f);
            textSize(24);
            text(Integer.toString(i), i * size, size + 30);
            noStroke();
            fill(color);
            rect(i * size, 0, size, size);
        }
    }
}
