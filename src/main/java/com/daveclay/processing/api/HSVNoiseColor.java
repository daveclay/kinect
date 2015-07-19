package com.daveclay.processing.api;

import processing.core.PApplet;

import java.awt.*;

public class HSVNoiseColor {
    public Noise2D hNoise;
    public Noise2D sNoise;
    public Noise2D vNoise;

    public HSVNoiseColor(PApplet pApplet, float rate) {
        hNoise = new Noise2D(pApplet, rate);
        sNoise = hNoise.newRelated(pApplet.random(2));
        vNoise = hNoise.newRelated(pApplet.random(2) + 2);
    }

    public int nextColor(int alpha) {
        int color = Color.HSBtoRGB(hNoise.next(), sNoise.next(), vNoise.next());
        return ColorUtils.addAlpha(color, alpha);
    }
}
