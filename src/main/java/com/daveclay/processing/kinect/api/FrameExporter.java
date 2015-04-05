package com.daveclay.processing.kinect.api;

import processing.core.PApplet;

public class FrameExporter {

    private final String outputFileTemplate;
    private final PApplet pApplet;
    private int count = 0;
    private boolean recording = false;

    /**
     *
     * @param applet
     * @param outputFileTemplate "/Users/daveclay/Desktop/out/ball%s.tif";
     */
    public FrameExporter(PApplet applet, String outputFileTemplate) {
        this.pApplet = applet;
        this.outputFileTemplate = outputFileTemplate;
    }

    public void start() {
        System.out.println("Starting frame capture");
        recording = true;
    }

    public void stop() {
        System.out.println("Stopping frame capture");
        recording = false;
    }

    public void writeFrame() {
        if (recording) {
            String paddedCount = count + "";
            while (paddedCount.length() < 5) {
                paddedCount = "0" + paddedCount;
            }
            String path = String.format(outputFileTemplate, paddedCount);
            pApplet.saveFrame(path);
            count++;
        }
    }
}
