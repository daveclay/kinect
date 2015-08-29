package com.daveclay.processing.sketches;

import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import javafx.scene.text.Font;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.video.Capture;

public class LiveVideoDrift extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new LiveVideoDrift());
    }

    Capture video;
    NoiseColor cnoise = new NoiseColor(this, .01f);
    ImgProc imgProc;

    public void setup() {
        size(1024, 768);
        imgProc = new ImgProc(this);
        imgProc.setupPixelFrames();
        video = new Capture(this, width, height);
        video.start();
        background(0);
        for (String font : PFont.list()) {
            System.out.println(font);
        }
        textFont(createFont("Monospaced", 32));
    }

    public void draw() {
        if (video.available()) {
            video.read();
        }
        blendMode(SCREEN);
        renderAlphaVideo();

        textSize(32);
        fill(10);
        String txt = random(1f) > .5f ? "AFTXR" : "";
        text(txt, 32, height / 2);
        stroke(10);
        line(0, height/2, width, height/2);

        imgProc.simpleBlur();
    }

    private void renderAlphaVideo() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int newColor = video.get(i, j);

                int old = get(i, j);
                int c = lerpColor(old, newColor, .02f);
                int red = c >> 16 & 0xff;
                int green = c >> 8 & 0xff;
                int blue = c & 0xff;
                int shrug = lerpColor(red, green, .5f);
                set(i, j, color(shrug, green, blue));
            }
        }
    }

    void alter() {
        float r, g, b = 0;
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // int x = (int) xnoise.next() / width;
                // int y = (int) ynoise.next() % height;
                int c = get(i, j);
                c = (int)(c * random(-1.05f, -2.098f));
                int alpha = c >> 24 & 0xff;
                int red = c >> 16 & 0xff;
                int green = c >> 8 & 0xff;
                int blue = c & 0xff;
                set(i, j, c);
            }
        }
    }
}
