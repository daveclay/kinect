package com.daveclay.processing.api;

import processing.core.PApplet;

public abstract class Drawing implements CanvasProxy {

    public static Runnable drawable(Drawing drawing) {
        return drawing::draw;
    }

    private PApplet canvas;

    public Drawing(PApplet canvas) {
        this.canvas = canvas;
    }

    @Override
    public PApplet getCanvas() {
        return canvas;
    }

    public abstract void draw();
}
