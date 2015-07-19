package com.daveclay.processing.openprocessing;

import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * http://www.xradiograph.com/Processing/Glitch
 */
public class PureGlitchMouse extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new PureGlitchMouse());
    }

    ImgProc imgProc;
    PImage image;
    int x,y;
    int w=900,h=450;
    Noise2D xnoise = new Noise2D(this, .0001f);
    Noise2D ynoise = new Noise2D(this, .0001f);
    int patch = 2;

    public void setup() {
        size(w, h);
        image = loadImage("/Users/daveclay/work/kinect/glitch.jpg");
        imgProc = new ImgProc(this);
        background(0);
    }

    int i = 0;
    public void draw() {
        textSize(48);
        text("AFT" + ((int)random(9)) + "R", 20, 60);
        int x = 0, y = 0;
        for(int i = 0; i < w*h; i += patch) {
            x = (this.x = i / w) ^ (int)(xnoise.next() * w);
            y = (this.y = i % h) ^ (int)(ynoise.next() * w);

            for (int j = 0; j < patch; j++) {
                for (int k = 0; k < patch; k++) {
                    int originalPixel = image.get(this.x + j, this.y + k);
                    int lastPixel = get(this.x + j, this.y + k);
                    int newPixel = blendColor(originalPixel, lastPixel, SOFT_LIGHT);
                    set(x + j, y + k, newPixel);
                }
            }
        }
        //imgProc.simpleBlur();
        // textSize(48);
        // text("AFT" + random(9) + "R", 20, 60);
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
