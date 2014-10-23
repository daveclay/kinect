package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.opengl.Sphere;
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

    private float rotateY = 96;
    private float rotateX = 28;
    private float rotateZ = 180;

    private Sphere sphere;

    public void setup() {
        frameExporter = new FrameExporter(this, "/Users/daveclay/Desktop/out/ball%s.tif");

        PImage image = loadImage("/Users/daveclay/Dropbox/joeface.jpg");
        sphere = new Sphere(this, image);

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
            translate(hand.x, hand.y, 0);
            // fill(color(255, 0, 0));
            // lights();

            float size = map(hand.z, 480, 3200, 66, 14);
            if (size < 1) {
                size = 1;
            }
            rotateX(radians(rotateX));
            rotateY(radians(rotateY));
            rotateZ(radians(rotateZ));
            sphere.radius(size);
            sphere.drawSphere(this);

            popMatrix();
        }

        frameExporter.writeFrame();
    }

    public void keyPressed() {
        System.out.println("keyCode: " + keyCode);
        switch(keyCode)
        {
            case LEFT:
                rotateY -= 2;
                println("rotateY: " + rotateY);
                break;
            case RIGHT:
                rotateY += 2;
                println("rotateY: " + rotateY);
                break;
            case UP:
                rotateX += 2;
                println("rotateX: " + rotateX);
                break;
            case DOWN:
                rotateX -= 2;
                println("rotateX : " + rotateX);
                break;
        }
        if (key == ',') {
            rotateZ -= 2;
            println("rotateZ : " + rotateZ);
        } else if (key == '.') {
            rotateZ += 2;
            println("rotateZ : " + rotateZ);
        }

        if (key == 'r') {
            frameExporter.start();
        } else if (key == 's') {
            frameExporter.stop();
        }
    }
}
