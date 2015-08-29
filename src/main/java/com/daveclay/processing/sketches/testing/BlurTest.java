package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.ColorUtils;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;

public class BlurTest extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new BlurTest());
    }

    public void setup() {
        size(1920, 1080, P2D);
    }

    public void draw() {
        fill(color(255, 255, 255));
        rect(0, 0, getWidth(), getHeight());
        fill(ColorUtils.addAlpha(color(0, 0, 0), .5f));
        rect(10, 10, 400, 400);

        fill(ColorUtils.addAlpha(color(0, 0, 0), 128));
        rect(400, 10, 400, 400);
    }
}
