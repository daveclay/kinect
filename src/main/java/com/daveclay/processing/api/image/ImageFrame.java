package com.daveclay.processing.api.image;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageFrame implements Pixels {

    final PImage img;
    public final PApplet canvas;
    int x;
    int y;
    int width;
    int height;

    public ImageFrame(PApplet canvas,
                      PImage img,
                      int x,
                      int y) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = img.width;
        this.height = img.height;
        this.canvas = canvas;
    }

    public void draw() {
        canvas.image(img, x, y);
    }

    @Override
    public int get(int x, int y) {
        return img.get(x, y);
    }

    @Override
    public void set(int x, int y, int color) {
        img.set(x, y, color);
    }
}
