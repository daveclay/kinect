package com.daveclay.processing.api;

import processing.core.PApplet;

import java.awt.*;

public class NoiseColor {

    public static void main(String[] args) {
        PApplet hi = new PApplet();
        NoiseColor noiseColor = new NoiseColor(hi, .0001f);
        for (int i = 0; i < 1000; i++) {
            int argb = noiseColor.nextColor(0);

            int alpha = 0xFF & (argb >> 24);
            int red = 0xFF & ( argb >> 16);
            int green = 0xFF & (argb >> 8 );
            int blue = 0xFF & argb;

            System.out.println(alpha + "\t" + red + "\t" + green + "\t" + blue);
        }
    }

    Noise2D rNoise;
    Noise2D gNoise;
    Noise2D bNoise;

    public NoiseColor(PApplet pApplet, float rate) {
        rNoise = new Noise2D(pApplet, rate);
        gNoise = rNoise.newRelated(pApplet.random(2));
        bNoise = rNoise.newRelated(pApplet.random(2) + 2);
    }

    public int nextColor(int alpha) {
        return alpha << 24 | r() << 16 | g() << 8 | b();
    }

    public int r() {
        return (int)(255 * rNoise.next());
    }

    public int g() {
        return (int)(255 * gNoise.next());
    }

    public int b() {
        return (int)(255 * bNoise.next());
    }

}
