package com.daveclay.processing.openprocessing;

import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * http://www.xradiograph.com/Processing/Glitch
 */
public class ColorDrift extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new ColorDrift());
    }

    private PImage image;
    Noise2D xnoise = new Noise2D(this, .0001f);
    Noise2D ynoise = new Noise2D(this, .0001f);
    NoiseColor cnoise = new NoiseColor(this, .03f);

    public void setup() {
        size(1600, 950);
        image = loadImage("/Users/daveclay/work/kinect/glitch.jpg");
        xnoise.setScale(width * height);
        ynoise.setScale(height * width);
        background(0);
    }

    public void draw() {
        fill(cnoise.nextColor(255));
        rect(0, 0, width, height);
        fill(0);
        text(frameRate, 20, 20);
    }
}
