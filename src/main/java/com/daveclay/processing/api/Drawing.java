package com.daveclay.processing.api;

import processing.core.PApplet;

public abstract class Drawing extends CanvasAware implements CanvasProxy {

    public Drawing(PApplet canvas) {
        super(canvas);
    }

    public static Runnable drawable(Drawing drawing) {
        return drawing::draw;
    }

    public abstract void draw();
}
