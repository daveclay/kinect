package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PixelTests extends PApplet {

    public static void main(String[] args) {
        PApplet.main(PixelTests.class.getName());
    }

    private SimpleOpenNI kinect;

    public void setup() {
        size(640 * 2, 480);

        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
    }

    @Override
    public void draw() {
        kinect.update();

        PImage rgb = kinect.rgbImage();
        PImage depth = kinect.depthImage();

        image(rgb, 0, 0);
        image(depth, 640, 0);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int color = get(mouseX, mouseY);
        println("r: " + red(color) + " g: " + green(color) + " b: " + blue(color));
    }
}
