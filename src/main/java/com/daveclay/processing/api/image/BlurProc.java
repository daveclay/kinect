package com.daveclay.processing.api.image;

import processing.core.PApplet;

public class BlurProc implements PixelsProc {

    int amount = 3;
    int width;
    int height;

    public BlurProc(PApplet canvas, int amount) {
        this.height = canvas.height;
        this.width = canvas.width;
        this.amount = amount;
    }

    public void process(Pixels src, Pixels dest, int x, int y) {
        int right = (int) Math.floor(amount / 2);
        int left = -1 * right;
        int divisor = amount * amount;
        int c, a, r, g, b;
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
