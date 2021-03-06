package com.daveclay.processing.api.image;

import processing.core.PApplet;

public class BlurProc implements PixelsProc {

    public static BlurProc blurCanvas(PApplet canvas, int amount) {
        return new BlurProc(canvas.width, canvas.height, amount);
    }

    public static BlurProc blurImage(ImageFrame image, int amount) {
        return new BlurProc(image.width, image.height, amount);
    }

    int amount = 3;
    int width;
    int height;
    int right;
    int left;
    int divisor;
    int c, a, r, g, b;

    public BlurProc(int width, int height, int amount) {
        this.height = height;
        this.width = width;
        this.amount = amount;
        right = (int) Math.floor(amount / 2);
        left = -1 * right;
        divisor = amount * amount;
    }

    public void process(Pixels src, Pixels dest, int x, int y) {
        if (y < right || x < right || y > (height - right) || x > (width - right)) {
            return;
        }
        a = 0;
        r = 0;
        g = 0;
        b = 0;

        for (int yb = left; yb <= right; yb++) {
            for (int xb = left; xb <= right; xb++) {
                c = src.get(x + xb, y - yb);
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
        dest.set(x, y, (a << 24) | (r << 16) | (g << 8) | b);
    }
}
