package com.daveclay.processing.sketches;

import com.daveclay.processing.api.*;
import com.daveclay.processing.api.image.ImgProc;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.video.Capture;

public class DepthFromVideo extends PApplet {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch(300, 300);
        SketchRunner.run(new DepthFromVideo(logSketch));
    }

    LogSketch logSketch;
    Capture video;
    ImgProc imgProc;
    PeasyCam cam;
    FloatValueMeasurement measurement = new FloatValueMeasurement();
    int pixelSize = 20;

    public DepthFromVideo(LogSketch logSketch) {
        this.logSketch = logSketch;
        logSketch.getHud().setFontSize(11);
    }

    public void setup() {
        size(1024, 768, OPENGL);
        imgProc = new ImgProc(this);
        imgProc.setupPixelFrames();
        video = new Capture(this, width, height);
        video.start();
        cam = new PeasyCam(this, 200);
        cam.setFreeRotationMode();
        background(0);
        textFont(createFont("Monospaced", 32));
    }

    public void draw() {
        //background(0);
        pushMatrix();
        translate(-1 * width / 2, -1 * height / 2, 0);
        if (video.available()) {
            video.read();
        }
        for(int i = 0; i < width; i += pixelSize) {
            for (int j = 0; j < height; j += pixelSize) {
                int c = video.get(i, j);
                int r = c >> 16 & 0xff;
                int g = c >> 8 & 0xff;
                int b = c & 0xff;
                float ave = r + g + b;
                float height = map(ave, 0, 768f, 0, 1400);
                pushMatrix();
                translate(i, j, -700 + height / 2);
                //fill(color(255, 255, 255, map(ave, 0, 768f, 0, 255)));
                stroke(ColorUtils.addAlpha(c, map(ave,0, 1268f, 0, 1)));
                //stroke(color(r, g, r ^ b, map(ave,0, 768f, 0, 255)));
                noFill();
                box(pixelSize, pixelSize, height);
                popMatrix();
            }
        }
        popMatrix();
        imgProc.simpleBlur();
    }
}
