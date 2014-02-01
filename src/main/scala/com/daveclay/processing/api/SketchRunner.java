package com.daveclay.processing.api;

import processing.core.PApplet;

public class SketchRunner {

    public static void run(PApplet... pApplets) {
        for (PApplet pApplet : pApplets) {
            PApplet.runSketch(new String[] { pApplet.getClass().getName() }, pApplet);
        }
    }
}
