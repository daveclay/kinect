package com.daveclay.processing.api;

import processing.core.PApplet;

public interface CanvasProxy {

    public PApplet getCanvas();

    default public void with(Drawing drawing) {
        drawing.draw();
    }

    default public int getWidth() {
        return getCanvas().getWidth();
    }

    default public int getHeight() {
        return getCanvas().getHeight();
    }

    default public void pushMatrix() {
        getCanvas().pushMatrix();
    }

    default public void popMatrix() {
        getCanvas().popMatrix();
    }

    default public void pushStyle() {
        getCanvas().pushStyle();
    }

    default public void popStyle() {
        getCanvas().popStyle();
    }

    default public void strokeWeight(float weight) {
        getCanvas().strokeWeight(weight);
    }

    default public void rect(float a, float b, float c, float d) {
        getCanvas().rect(a, b, c, d);
    }

    default public void stroke(float v1) {
        getCanvas().stroke(v1);
    }

    default public void stroke(float v1, float v2, float v3, float alpha) {
        getCanvas().stroke(v1, v2, v3, alpha);
    }

    default public void fill(float v1, float v2, float v3) {
        getCanvas().fill(v1, v2, v3);
    }

    default public void fill(float v1, float v2, float v3, float alpha) {
        getCanvas().fill(v1, v2, v3, alpha);
    }

    default public void ellipse(float x, float y, int i, int i1) {
        getCanvas().ellipse(x, y, i, i1);
    }

    default public void line(float x, float y, float x1, float y1) {
        getCanvas().line(x, y, x1, y1);
    }

    default public float alpha(int rgb) {
        return getCanvas().alpha(rgb);
    }

    default public float red(int rgb) {
        return getCanvas().red(rgb);
    }

    default public float green(int rgb) {
        return getCanvas().green(rgb);
    }

    default public float blue(int rgb) {
        return getCanvas().blue(rgb);
    }

    default public float hue(int rgb) {
        return getCanvas().hue(rgb);
    }

    default public float saturation(int rgb) {
        return getCanvas().saturation(rgb);
    }

    default public float brightness(int rgb) {
        return getCanvas().brightness(rgb);
    }

    default public int lerpColor(int c1, int c2, float amt) {
        return getCanvas().lerpColor(c1, c2, amt);
    }

    public static int lerpColor(int c1, int c2, float amt, int mode) {
        return PApplet.lerpColor(c1, c2, amt, mode);
    }

    default public int color(int gray) {
        return getCanvas().color(gray);
    }

    default public int color(float fgray) {
        return getCanvas().color(fgray);
    }

    default public int color(int gray, int alpha) {
        return getCanvas().color(gray, alpha);
    }

    default public int color(float fgray, float falpha) {
        return getCanvas().color(fgray, falpha);
    }

    default public int color(int v1, int v2, int v3) {
        return getCanvas().color(v1, v2, v3);
    }

    default public int color(int v1, int v2, int v3, int alpha) {
        return getCanvas().color(v1, v2, v3, alpha);
    }

    default public int color(float v1, float v2, float v3) {
        return getCanvas().color(v1, v2, v3);
    }

    default public int color(float v1, float v2, float v3, float alpha) {
        return getCanvas().color(v1, v2, v3, alpha);
    }

    public static int blendColor(int c1, int c2, int mode) {
        return PApplet.blendColor(c1, c2, mode);
    }

}


