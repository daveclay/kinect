package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.kinect.api.FrameExporter;
import com.daveclay.processing.kinect.api.HandGestureHandler;
import com.daveclay.processing.kinect.api.HandGestures;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class HandAwareHolding3DBall extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandAwareHolding3DBall.class.getName());
    }

    private SimpleOpenNI kinect;
    private FrameExporter frameExporter;
    private HandGestures handGestures;
    private float max = 66;
    private float min = 14;

    public void setup() {
        frameExporter = new FrameExporter(this, "/Users/daveclay/Desktop/out/ball%s.tif");

        kinect = new SimpleOpenNI(this);
        kinect.setMirror(true);

        kinect.enableRGB();
        size(kinect.rgbWidth(), kinect.rgbHeight(), OPENGL);

        handGestures = HandGestureHandler.init(kinect);
        handGestures.useWaveGesture();
    }

    public void draw() {
        kinect.update();

        background(0);
        PImage image = kinect.rgbImage();
        background(image);

        for (PVector hand : handGestures.getAllCurrentHandPositions()) {
            pushMatrix();
            fill(color(0, 255, 0));
            rect(hand.x, hand.y, 10, 10);
            popMatrix();

            pushMatrix();
            noStroke();
            smooth();
            translate(hand.x, hand.y, 0);
            fill(color(255, 0, 0));
            lights();

            float size = map(hand.z, 480, 3200, max, min);
            if (size < 1) {
                size = 1;
            }
            sphere(size);
            popMatrix();
        }

        frameExporter.writeFrame();
    }

    public void keyPressed() {
        switch(keyCode)
        {
            case LEFT:
                max -= 2;
                println("max: " + max);
                break;
            case RIGHT:
                max += 2;
                println("max: " + max);
                break;
            case UP:
                min += 1;
                println("min: " + min);
                break;
            case DOWN:
                min -= 1;
                println("min: " + min);
                break;
        }
    }
}
