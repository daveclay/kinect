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
    NoiseColor cnoise = new NoiseColor(this, .000001f);
    PImage canvas;

    public void setup() {
        size(900, 450);
        image = loadImage("/Users/daveclay/work/kinect/glitch.jpg");
        xnoise.setScale(width * height);
        ynoise.setScale(height * width);
        canvas = new PImage(width, height);
        background(0);
    }

    public void draw() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // int x = (int) xnoise.next() / width;
                // int y = (int) ynoise.next() % height;
                set(i, j, cnoise.nextColor(255));
            }
        }
        //image(canvas, 0, 0);
        text(frameRate, 20, 20);
    }
}
