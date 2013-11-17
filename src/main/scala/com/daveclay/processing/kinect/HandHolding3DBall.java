package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.kinect.api.SimpleHandGestureAware;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class HandHolding3DBall extends SimpleHandGestureAware {

    public static void main(String[] args) {
        PApplet.main(HandHolding3DBall.class.getName());
    }

    private SimpleOpenNI kinect;

    private float max = 66;
    private float min = 14;

    private int count = 0;

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.setMirror(true);

        kinect.enableRGB();
        size(kinect.rgbWidth(), kinect.rgbHeight(), OPENGL);

        super.init(kinect);
        useWaveGesture();
    }

    public void draw() {
        kinect.update();

        background(0);
        PImage image = kinect.rgbImage();
        background(image);

        for (PVector hand : getAllCurrentHandPositions()) {
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

        String shit = count + "";
        while (shit.length() < 3) {
            shit = "0" + shit;
        }
        saveFrame("/Users/daveclay/Desktop/out/ball " + shit + ".tif");
        count++;
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
