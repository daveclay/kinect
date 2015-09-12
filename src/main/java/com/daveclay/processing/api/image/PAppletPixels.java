package com.daveclay.processing.api.image;

import processing.core.PApplet;

public class PAppletPixels implements Pixels {

    private final PApplet canvas;

    public PAppletPixels(PApplet canvas) {
        this.canvas = canvas;
    }

    @Override
    public int get(int x, int y) {
        return canvas.get(x, y);
    }

    @Override
    public void set(int x, int y, int color) {
        canvas.set(x, y, color);
    }
}
