package com.daveclay.processing.openprocessing;

import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * http://www.xradiograph.com/Processing/Glitch
 */
public class PureGlitchMouse extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new PureGlitchMouse());
    }

    PImage image;
    int x,y;
    int w=900,h=450;
    Noise2D xnoise = new Noise2D(this, .0001f);
    Noise2D ynoise = new Noise2D(this, .0001f);

    public void setup() {
        size(w,h);
        image =loadImage("/Users/daveclay/work/kinect/glitch.jpg");
    }

    int i = 0;
    public void draw() {
        int x = 0, y = 0;
        background(0);
        for(int i = 0; i < w*h; i++) {
            x = (this.x = i % w) ^ (int)(xnoise.next() * w);
            y = (this.y = i / h) ^ (int)(ynoise.next() * h);
            x = min(w - 1, x);
            y = min(h - 1, y);
            set(x, y, image.get(this.x, this.y));
        }
        text("" + x, 20, 20);
    }

    public void testNoiseValues() {
        background(0);
        textSize(32);
        int x = (this.x = i % w) ^ (int)(xnoise.next() * w);
        int y = (this.y = i % h) ^ (int)(ynoise.next() * h);
        text(x + " " + y, 100, 100);
        i++;
        if (i > w*h) {
            i = 0;
        }
    }

    public void noiseGlitch() {
        for(int i = 0; i < w*h; i++) {
            int x = (this.x = i % w) ^ (int)(xnoise.next() * w);
            int y = (this.y = i / w) ^ (int)(ynoise.next() * h);
            x = min(w, x);
            y = min(h, y);
            set(x, y, image.get(this.x, this.y));
        }
    }

    public void original() {
        for(int i = 0; i < w*h; i++) {
            int x = (this.x = i % w) ^ mouseX;
            int y = (this.y = i / w) ^ mouseY;
            set(x, y, image.get(this.x, this.y));
        }
    }
}
