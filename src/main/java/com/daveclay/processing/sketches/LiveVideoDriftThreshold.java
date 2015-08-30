package com.daveclay.processing.sketches;

import com.daveclay.processing.api.*;
import com.daveclay.processing.api.image.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.video.Capture;

import java.util.ArrayList;
import java.util.List;

public class LiveVideoDriftThreshold extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new LiveVideoDriftThreshold());
    }

    Capture video;
    NoiseColor cnoise = new NoiseColor(this, .01f);
    ImgProc imgProc;
    SimpleProcessPixels simpleProcessPixels;

    public void setup() {
        size(1024, 768);
        imgProc = new ImgProc(this);
        imgProc.setupPixelFrames();
        video = new Capture(this, width, height);
        video.start();

        simpleProcessPixels = new SimpleProcessPixels(this);
        simpleProcessPixels.addPixelsProc(new VideoDriftPixelsProc(this, video));
        //simpleProcessPixels.addPixelsProc(new BlurProc(this, 15));

        background(0);
        /*
        for (String font : PFont.list()) {
            System.out.println(font);
        }
        */

        textFont(createFont("Monospaced", 32));
    }

    public void draw() {
        if (video.available()) {
            video.read();
        }
        blendMode(SCREEN);
        simpleProcessPixels.draw();
        /*
        textSize(32);
        fill(10);
        String txt = random(1f) > .5f ? "AFTXR" : "";
        text(txt, 32, height / 2);
        stroke(10);
        line(0, height / 2, width, height / 2);
         */
    }

    public static class VideoDriftPixelsProc extends CanvasAware implements PixelsProc {

        final Capture video;

        public VideoDriftPixelsProc(PApplet applet, Capture video) {
            super(applet);
            this.video = video;
        }

        @Override
        public void process(Pixels src, Pixels dest, int i, int j) {
            int newColor = video.get(i, j);
            int threshold = 128;
            float drift = ((newColor >> 16 & 0xff) > threshold ||
                    (newColor >> 8 & 0xff) > threshold ||
                    (newColor & 0xff) > threshold) ? .1f : .02f;


            int old = src.get(i, j);

            int c = lerpColor(old, newColor, drift);

            int red = c >> 16 & 0xff;
            int green = c >> 8 & 0xff;
            int blue = c & 0xff;
            int shrug = lerpColor(red, green, .5f);
            dest.set(i, j, color(shrug, green, blue));
        }
    }

    private void renderAlphaVideo(int i, int j) {
        int newColor = video.get(i, j);
        int old = get(i, j);
        int c = lerpColor(old, newColor, .02f);
        int red = c >> 16 & 0xff;
        int green = c >> 8 & 0xff;
        int blue = c & 0xff;
        int shrug = lerpColor(red, green, .5f);
        set(i, j, color(shrug, green, blue));
    }

    void alter(int x, int y) {
        int c = get(x, y);
        c = (int)(c * random(-1.05f, -2.098f));
        int alpha = c >> 24 & 0xff;
        int red = c >> 16 & 0xff;
        int green = c >> 8 & 0xff;
        int blue = c & 0xff;
        set(x, y, c);
    }

}
