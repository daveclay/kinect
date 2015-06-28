package com.daveclay.processing.api;

import processing.core.PApplet;

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

    final PApplet pApplet;
    float rNoise;
    float gNoise;
    float bNoise;
    float tick;
    int alpha = 55;
    float rate = .00001f;

    public NoiseColor(PApplet pApplet, float rate) {
        this.pApplet = pApplet;
        rNoise = pApplet.random(10);
        gNoise = rNoise + 1;
        bNoise = rNoise + 2;
        tick = pApplet.random(10);
        this.rate = rate;
    }

    public int nextColor(int alpha) {
        return alpha << 24 | r() << 16 | g() << 8 | b();
    }

    public int r() {
        rNoise += rate;
        return increment(rNoise);
    }

    public int g() {
        gNoise += rate;
        return increment(gNoise);
    }

    public int b() {
        bNoise += rate;
        return increment(bNoise);
    }

    public int increment(float noise) {
        tick += rate;
        return (int)(255 * pApplet.noise(noise, tick));
    }
}
