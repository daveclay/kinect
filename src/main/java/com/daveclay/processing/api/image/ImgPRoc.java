package com.daveclay.processing.api.image;

import processing.core.PApplet;

public class ImgProc {

    private PApplet pApplet;
    int[] currFrame;
    int[] prevFrame;
    int[] tempFrame;
    int width;
    int height;

    public ImgProc(PApplet pApplet) {
        this.pApplet = pApplet;
        this.width = pApplet.width;
        this.height = pApplet.height;
    }

    public void pixel(int x, int y, int color) {
        currFrame[x + y * width] = color;
    }

    public void draw() {
        drawPixelArray(currFrame, 0, 0, width, height);
        PApplet.arraycopy(currFrame, prevFrame);
    }

    public void drawPixelArray(int[] src, int dx, int dy, int w, int h) {
        //backBuf.loadPixels();
        //arraycopy(src, backBuf.pixels);
        //backBuf.updatePixels();
        //image(backBuf, dx, dy);
        pApplet.loadPixels();
        int x;
        int y;
        for (int i = 0; i < w * h; i++) {
            x = dx + i % w;
            y = dy + i / w;
            pApplet.pixels[x + y * w] = src[i];
        }
        pApplet.updatePixels();
    }

    public void bullshitBlur() {
        pApplet.loadPixels();
        int[] tempFrame = new int[width * height];
        otherFastBlur(pApplet.pixels, tempFrame);
        PApplet.arraycopy(tempFrame, pApplet.pixels);
        pApplet.updatePixels();
    }

    public void simpleBlur() {
        pApplet.loadPixels();
        int[] tempFrame = new int[width * height];
        blur(pApplet.pixels, tempFrame, width, height);
        PApplet.arraycopy(tempFrame, pApplet.pixels);
        pApplet.updatePixels();
    }

    public void simpleBrightness(float scale) {
        pApplet.loadPixels();
        int[] tempFrame = new int[width * height];
        scaleBrightness(pApplet.pixels, tempFrame, width, height, scale);
        PApplet.arraycopy(tempFrame, pApplet.pixels);
        pApplet.updatePixels();
    }

    public void blur() {
        blur(prevFrame, tempFrame, width, height);
        //imgProc.scaleBrightness(tempFrame, tempFrame, width, height, 0.99f);
    }

    public void blur(int[] src, int[] dst, int w, int h) {
        int c;
        int a;
        int r;
        int g;
        int b;
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                a = 0;
                r = 0;
                g = 0;
                b = 0;
                for (int yb = -1; yb <= 1; yb++) {
                    for (int xb = -1; xb <= 1; xb++) {
                        c = src[(x + xb) + (y - yb) * w];
                        a += (c >> 24) & 0xFF;
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += (c) & 0xFF;
                    }
                }
                a /= 9;
                r /= 9;
                g /= 9;
                b /= 9;
                dst[x + y * w] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    //you must be in RGB colorModel
    public void scaleBrightness(int[] src, int[] dst, int w, int h, float s) {
        int a;
        int r;
        int g;
        int b;
        int c;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                c = src[x + y * w];
                a = (int) (s * ((c >> 24) & 0xFF));
                r = (int) (s * ((c >> 16) & 0xFF));
                g = (int) (s * ((c >> 8) & 0xFF));
                b = (int) (s * ((c) & 0xFF));
                dst[x + y * w] = (a << 24) | (r << 16) | (g << 8) | b;
                //ch = hue(c);
                //cs = saturation(c);
                //cb = brightness(c) * s;
                //dst[x + y*w] = color(ch, cs, cb);
                //dst[x + y*w] = src[x + y*w];
            }
        }
    }

    public void copyFrame() {
        PApplet.arraycopy(tempFrame, currFrame);
    }

    public void setupPixelFrames() {
        currFrame = new int[width * height];
        prevFrame = new int[width * height];
        tempFrame = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            currFrame[i] = pApplet.color(0, 0, 0);
            prevFrame[i] = pApplet.color(0, 0, 0);
            tempFrame[i] = pApplet.color(0, 0, 0);
        }
    }

    public void otherFastBlur(int[] source, int[] dest) {
        for (int i = 1; i < (width - 1); ++i) {
            for (int j = 1; j < (height - 1); ++j) {
                dest[i + j * width] = (((((source[i + j * width] & 0xFF) << 2) +
                        (source[i + 1 + j * width] & 0xFF) +
                        (source[i - 1 + j * width] & 0xFF) +
                        (source[i + (j + 1) * width] & 0xFF) +
                        (source[i + (j - 1) * width] & 0xFF)) >> 3) & 0xFF) +
                        (((((source[i + j * width] & 0xFF00) << 2) +
                                (source[i + 1 + j * width] & 0xFF00) +
                                (source[i - 1 + j * width] & 0xFF00) +
                                (source[i + (j + 1) * width] & 0xFF00) +
                                (source[i + (j - 1) * width] & 0xFF00)) >> 3) & 0xFF00) +
                        (((((source[i + j * width] & 0xFF0000) << 2) +
                                (source[i + 1 + j * width] & 0xFF0000) +
                                (source[i - 1 + j * width] & 0xFF0000) +
                                (source[i + (j + 1) * width] & 0xFF0000) +
                                (source[i + (j - 1) * width] & 0xFF0000)) >> 3) & 0xFF0000) +
                        0xFF000000;
            }
        }
    }
}
