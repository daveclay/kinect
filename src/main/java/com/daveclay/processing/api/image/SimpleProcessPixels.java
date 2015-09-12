package com.daveclay.processing.api.image;

import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class SimpleProcessPixels {

    public static SimpleProcessPixels create(ImageFrame imageFrame) {
        return new SimpleProcessPixels(imageFrame, imageFrame, imageFrame.width, imageFrame.height);
    }

    public static SimpleProcessPixels create(PApplet canvas) {
        Pixels src;
        Pixels dest;
        src = dest = new PAppletPixels(canvas);
        return new SimpleProcessPixels(src, dest, canvas.width, canvas.height);
    }

    private List<PixelsProc> pixelsProcList = new ArrayList<>();
    private Pixels src;
    private Pixels dest;
    int width;
    int height;

    public SimpleProcessPixels(Pixels src, Pixels dest, int width, int height) {
        this.src = src;
        this.dest = dest;
        this.width = width;
        this.height = height;
    }

    public void addPixelsProc(PixelsProc pixelsProc) {
        pixelsProcList.add(pixelsProc);
    }

    public void process() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                process(i, j);
            }
        }
    }

    private void process(int x, int y) {
        pixelsProcList.forEach(pixelsProc -> pixelsProc.process(src, dest, x, y));
    }

}
