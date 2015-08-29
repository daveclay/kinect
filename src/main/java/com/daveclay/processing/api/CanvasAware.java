package com.daveclay.processing.api;

import processing.core.PApplet;

public abstract class CanvasAware implements CanvasProxy {
    protected PApplet canvas;

    public CanvasAware(PApplet canvas) {
        this.canvas = canvas;
    }

    @Override
    public PApplet getCanvas() {
        return canvas;
    }
}
