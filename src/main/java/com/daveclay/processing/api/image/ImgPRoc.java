package com.daveclay.processing.api.image;

import processing.core.PApplet;

public class ImgProc {

    private PApplet pApplet;

    public ImgProc(PApplet pApplet) {
        this.pApplet = pApplet;
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
}
