package com.daveclay.processing.api.image;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageFrame implements Pixels {

    public final PImage img;
    public PImage blurImg;
    public PImage desaturatedImg;
    public final PApplet canvas;
    ImgProc imgProc;
    public int x;
    public int y;
    int width;
    int height;

    public ImageFrame(PApplet canvas,
                      PImage img,
                      int x,
                      int y) {
        this.img = img;
        this.blurImg = ImgProc.copy(img);
        this.desaturatedImg = ImgProc.copy(img);
        this.x = x;
        this.y = y;
        this.width = img.width;
        this.height = img.height;
        this.canvas = canvas;
        this.imgProc = new ImgProc(canvas);
    }

    public ImgProc.BlurResult blur() {
        return ImgProc.simpleBlur(blurImg);
    }

    public void desaturate() {
        ImgProc.desaturate(desaturatedImg);
    }

    public void drawOriginal() {
        canvas.image(img, x, y);
    }

    public void drawDesaturated() {
        canvas.image(desaturatedImg, x, y);
    }

    public void draw() {
        canvas.image(blurImg, x, y);
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
