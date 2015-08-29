package com.daveclay.processing.api.image;

import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class SimpleProcessPixels extends Drawing {

    private List<PixelsProc> pixelsProcList = new ArrayList<>();
    private Pixels src;
    private Pixels dest;

    public SimpleProcessPixels(PApplet canvas) {
        super(canvas);
        src = dest = new PAppletPixels(canvas);
    }

    public void addPixelsProc(PixelsProc pixelsProc) {
        pixelsProcList.add(pixelsProc);
    }

    @Override
    public void draw() {
        for(int i = 0; i < canvas.width; i++) {
            for (int j = 0; j < canvas.height; j++) {
                process(i, j);
            }
        }
    }

    private void process(int x, int y) {
        pixelsProcList.forEach(pixelsProc -> pixelsProc.process(src, dest, x, y));
    }

}
