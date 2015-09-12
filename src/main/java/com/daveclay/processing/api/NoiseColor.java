package com.daveclay.processing.api;

import processing.core.PApplet;

import static processing.core.PApplet.map;

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
    int range;
    int rangeTop;

    public NoiseColor(PApplet pApplet, float rate) {
        this(pApplet, rate, 255, 255);
    }

    public NoiseColor(PApplet pApplet, float rate, int range, int rangeTop) {
        this.range = range;
        this.rangeTop = rangeTop;
        rNoise = new Noise2D(pApplet, rate);
        gNoise = rNoise.newRelated(pApplet.random(2));
        bNoise = rNoise.newRelated(pApplet.random(2) + 2);
    }

    public int nextColor(int alpha) {
        return alpha << 24 | r() << 16 | g() << 8 | b();
    }

    private int range(float value) {
        return (int)map(value, 0f, 1f, rangeTop - range, range);
    }

    public int r() {
        return range(rNoise.next());
    }

    public int g() {
        return range(gNoise.next());
    }

    public int b() {
        return range(bNoise.next());
    }

}
