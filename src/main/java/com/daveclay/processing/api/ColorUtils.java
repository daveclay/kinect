package com.daveclay.processing.api;

import processing.core.PApplet;

import java.awt.*;

public class ColorUtils extends PApplet {

    public static void main(String[] args) {
        int color = Color.HSBtoRGB(.8f, .8f, .9f);
        int[] split = split(color);
        int red = split[0] >> 16 & 0xff;
        int green = split[1] >> 8 & 0xff;
        int blue = split[2] & 0xff;

        System.out.println(red + "\t" + green + "\t" + blue);
        System.out.println((255 - red) + "\t" + (255 - green) + "\t" + (255 - blue));
        SketchRunner.run(new ColorUtils());
    }

    public static int[] split(int color) {
        int alpha = color >> 24 & 0xff;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        return new int[] { red, green, blue, alpha };
    }

    public static String splitInfo(int color) {
        int[] split = split(color);
        return split[0] + " " + split[1] + " " + split[2];
    }

    public static int addAlpha(int color, float alpha) {
        return ((int) (alpha * 255.0f) << 24) | (color & 0x00ffffff);
    }

    public static int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    public static int invert(int color) {
        int[] split = split(color);
        return (split[3] << 24) | ((255 - split[0]) << 16) | ((255 - split[1]) << 8) | (255 - split[2]);
    }

    public static int addRed(int red, int color) {
        int alpha = color >> 24 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    int color;
    int inverse;

    public void setup() {
        size(600, 400);
        build();
    }

    public void draw() {
        noStroke();
        fill(color);
        rect(0, 0, 300, 400);
        fill(inverse);
        rect(300, 0, 300, 400);
        fill(255);
        textSize(32);
        text("TEST " + splitInfo(inverse), 30, 30);
    }

    public void build() {
        color = Color.HSBtoRGB(random(1f), random(1f), random(1f));
        inverse = invert(color);
    }

    public void mouseClicked() {
        build();
    }
}
