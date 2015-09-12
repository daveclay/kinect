package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

public interface CanvasProxy {

    PApplet getCanvas();

    default void with(Drawing drawing) {
        drawing.draw();
    }

    default void shape(PShape shape) {
        getCanvas().shape(shape);
    }

    default void shape(PShape shape, float a, float b, float c, float d) {
        getCanvas().shape(shape, a, b, c, d);
    }

    default void shape(PShape shape, float x, float y) {
        getCanvas().shape(shape, x, y);
    }

    default PShape createShape() {
        return getCanvas().createShape();
    }

    default PShape createShape(PShape source) {
        return getCanvas().createShape(source);
    }

    default PShape createShape(int type) {
        return getCanvas().createShape(type);
    }

    default PShape createShape(int kind, float... p) {
        return getCanvas().createShape(kind, p);
    }

    default int getWidth() {
        return getCanvas().getWidth();
    }

    default int getHeight() {
        return getCanvas().getHeight();
    }

    default void noStroke() {
        getCanvas().noStroke();
    }

    default void pushMatrix() {
        getCanvas().pushMatrix();
    }

    default void popMatrix() {
        getCanvas().popMatrix();
    }

    default void pushStyle() {
        getCanvas().pushStyle();
    }

    default void popStyle() {
        getCanvas().popStyle();
    }

    default void strokeWeight(float weight) {
        getCanvas().strokeWeight(weight);
    }

    default void rect(float a, float b, float c, float d) {
        getCanvas().rect(a, b, c, d);
    }

    default void stroke(float v1) {
        getCanvas().stroke(v1);
    }

    default void stroke(float v1, float v2, float v3, float alpha) {
        getCanvas().stroke(v1, v2, v3, alpha);
    }

    default void fill(int color) {
        getCanvas().fill(color);
    }

    default void fill(float v1, float v2, float v3) {
        getCanvas().fill(v1, v2, v3);
    }

    default void fill(float v1, float v2, float v3, float alpha) {
        getCanvas().fill(v1, v2, v3, alpha);
    }

    default void ellipse(float x, float y, int i, int i1) {
        getCanvas().ellipse(x, y, i, i1);
    }

    default void line(float x, float y, float x1, float y1) {
        getCanvas().line(x, y, x1, y1);
    }

    default float alpha(int rgb) {
        return getCanvas().alpha(rgb);
    }

    default float red(int rgb) {
        return getCanvas().red(rgb);
    }

    default float green(int rgb) {
        return getCanvas().green(rgb);
    }

    default float blue(int rgb) {
        return getCanvas().blue(rgb);
    }

    default float hue(int rgb) {
        return getCanvas().hue(rgb);
    }

    default float saturation(int rgb) {
        return getCanvas().saturation(rgb);
    }

    default float brightness(int rgb) {
        return getCanvas().brightness(rgb);
    }

    default int lerpColor(int c1, int c2, float amt) {
        return getCanvas().lerpColor(c1, c2, amt);
    }

    static int lerpColor(int c1, int c2, float amt, int mode) {
        return PApplet.lerpColor(c1, c2, amt, mode);
    }

    default int color(int gray) {
        return getCanvas().color(gray);
    }

    default int color(float fgray) {
        return getCanvas().color(fgray);
    }

    default int color(int gray, int alpha) {
        return getCanvas().color(gray, alpha);
    }

    default int color(float fgray, float falpha) {
        return getCanvas().color(fgray, falpha);
    }

    default int color(int v1, int v2, int v3) {
        return getCanvas().color(v1, v2, v3);
    }

    default int color(int v1, int v2, int v3, int alpha) {
        return getCanvas().color(v1, v2, v3, alpha);
    }

    default int color(float v1, float v2, float v3) {
        return getCanvas().color(v1, v2, v3);
    }

    default int color(float v1, float v2, float v3, float alpha) {
        return getCanvas().color(v1, v2, v3, alpha);
    }

    static int blendColor(int c1, int c2, int mode) {
        return PApplet.blendColor(c1, c2, mode);
    }

    default void beginShape() {
        getCanvas().beginShape();
    }

    default void curveVertex(float x, float y) {
        getCanvas().curveVertex(x, y);
    }

    default void endShape() {
        getCanvas().endShape();
    }

    default void roundrect(int x, int y, int w, int h, int r) {
        float corner = w/20f;
        float midDisp = w/1000f;

        beginShape();
        curveVertex(x+corner,y);
        curveVertex(x+w-corner,y);
        curveVertex(x+w+midDisp,y+h/2f);
        curveVertex(x+w-corner,y+h);
        curveVertex(x+corner,y+h);
        curveVertex(x-midDisp,y+h/2f);

        curveVertex(x+corner,y);
        curveVertex(x+w-corner,y);
        curveVertex(x+w+midDisp,y+h/2f);
        endShape();
    }

    default void textFont(PFont font) {
        getCanvas().textFont(font);
    }

    default void text(String s, float x, float y) {
        getCanvas().text(s, x, y);
    }

    default float random(float high) {
        return getCanvas().random(high);
    }
}


