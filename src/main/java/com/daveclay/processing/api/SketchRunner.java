package com.daveclay.processing.api;

import processing.core.PApplet;

public class SketchRunner {

    public static void run(PApplet... pApplets) {
        for (PApplet pApplet : pApplets) {
            runSketch(pApplet);
        }
    }

    public static void runSketchFullScreen(PApplet pApplet, int display) {
        String[] args = {
            "--display=" + display,
            pApplet.getClass().getName()
        };
        PApplet.runSketch(args, pApplet);
    }

    public static void runSketch(PApplet pApplet) {
        String[] args = {
            pApplet.getClass().getName()
        };
        PApplet.runSketch(args, pApplet);
    }
}
