package com.daveclay.processing.video.capture;

import processing.core.PApplet;
import processing.video.Capture;

public class VideoCaptureDevice {

    private int index;
    private String info;

    public VideoCaptureDevice(int i, String info) {
        this.index = i;
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }

    public Capture open(PApplet pApplet) {
        Capture capture = new Capture(pApplet, info);
        capture.start();

        return capture;
    }

    @Override
    public String toString() {
        return index + " " + info + "\n";
    }
}
