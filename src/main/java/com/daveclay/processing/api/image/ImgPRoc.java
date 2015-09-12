package com.daveclay.processing.api.image;

import processing.core.PApplet;
import processing.core.PImage;

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

    public BlurResult simpleBlur() {
        pApplet.loadPixels();
        BlurResult result = simpleBlur(pApplet.pixels, pApplet.pixels, pApplet.width, pApplet.height);
        pApplet.updatePixels();
        return result;
    }

    public static BlurResult simpleBlur(PImage image, PImage dest) {
        image.loadPixels();
        BlurResult result = simpleBlur(image.pixels, dest.pixels, image.width, image.height);
        dest.updatePixels();
        return result;
    }

    public static BlurResult simpleBlur(PImage image) {
        return simpleBlur(image, image);
    }

    public static BlurResult simpleBlur(int[] src, int[] dest, int width, int height) {
        int[] tempFrame = new int[width * height];
        BlurResult result = blur3x3(src, tempFrame, width, height);
        PApplet.arraycopy(tempFrame, dest);
        return result;
    }

    public void simpleBrightness(float scale) {
        pApplet.loadPixels();
        int[] tempFrame = new int[width * height];
        scaleBrightness(pApplet.pixels, tempFrame, width, height, scale);
        PApplet.arraycopy(tempFrame, pApplet.pixels);
        pApplet.updatePixels();
    }

    public BlurResult blur() {
        return blur(prevFrame, tempFrame, width, height);
        //imgProc.scaleBrightness(tempFrame, tempFrame, width, height, 0.99f);
    }

    public static void grayFill(PImage image, int value) {
        image.loadPixels();
        grayFill(image.pixels, image.width, image.height, value);
        image.updatePixels();
    }

    public static void grayFill(int[] dest, int width, int height, int value) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                dest[x + y * width] = (255 << 24) | (value << 16) | (value << 8) | value;
            }
        }
    }

    public static BlurResult checkImage(PImage image) {
        BlurResult blurResult = new BlurResult();
        int a, r, g, b;
        for (int y = 1; y < image.height; y++) {
            for (int x = 1; x < image.width; x++) {
                int c = image.get(x, y);
                a = (c >> 24) & 0xFF;
                r = (c >> 16) & 0xFF;
                g = (c >> 8) & 0xFF;
                b = (c) & 0xFF;
                blurResult.check(r, g, b);
            }
        }
        return blurResult;
    }

    public static class BlurResult {
        public boolean allBlack = true;
        public boolean allWhite = true;
        public int count;

        void check(int r, int g, int b) {
            int value = (r + g + b) / 3;
            if (value > 20) {
                count++;
            }

            if (allBlack && r > 0 && g > 0 && b > 0) {
                allBlack = false;
            } else if (allWhite && r < 255 && g < 255 && b < 255) {
                allWhite = false;
            }
        }
    }

    public static void desaturate(PImage image) {
        desaturate(image, image);
    }

    public static void desaturate(PImage image, PImage dest) {
        image.loadPixels();
        desaturate(image.pixels, dest.pixels, image.width, image.height);
        dest.updatePixels();
    }

    public static void desaturate(int[] src, int [] dest, int width, int height) {
        int c, a, r, g, b;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                c = src[x + y * width];
                a = (c >> 24) & 0xFF;
                r = (c >> 16) & 0xFF;
                g = (c >> 8) & 0xFF;
                b = (c) & 0xFF;
                r = g = b = (r + b + g) / 3;
                dest[x + y * width] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    public static BlurResult blur3x3(int[] src, int[] dst, int w, int h) {
        BlurResult blurResult = new BlurResult();
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

                blurResult.check(r, g, b);
                dst[x + y * w] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return blurResult;
    }

    public static BlurResult blur(int[] src, int[] dst, int w, int h) {
        BlurResult blurResult = new BlurResult();
        int size = 9;
        int right = (int) Math.floor(size / 2);
        int left = -1 * right;
        int divisor = size * size;
        int c, a, r, g, b;

        for (int y = right; y < h - right; y++) {
            for (int x = right; x < w - right; x++) {
                a = 0;
                r = 0;
                g = 0;
                b = 0;
                for (int yb = left; yb <= right; yb++) { // 3 iters: -1, 0, 1
                    for (int xb = left; xb <= right; xb++) { // 3 iters: -1, 0, 1
                        c = src[(x + xb) + (y - yb) * w];
                        a += (c >> 24) & 0xFF;
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += (c) & 0xFF;
                    }
                }
                a /= divisor;
                r /= divisor;
                g /= divisor;
                b /= divisor;

                blurResult.check(r, g, b);

                dst[x + y * w] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return blurResult;
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

    public static PImage copy(PImage img) {
        PImage copy = new PImage(img.width, img.height);
        PApplet.arraycopy(img.pixels, copy.pixels);
        return copy;
    }

    public static PImage loadImageByName(PApplet canvas, String name) {
        return canvas.loadImage("/Users/daveclay/work/rebel belly after video/" + name);
    }
}
