package com.daveclay.processing.examples.peasy;
import peasy.*;
import processing.core.PApplet;

public class PeasyTest extends PApplet {

    PeasyCam cam;

    public void setup() {
        size(200,200,P3D);
        cam = new PeasyCam(this, 100);
        cam.setMinimumDistance(50);
        cam.setMaximumDistance(500);
    }
    public void draw() {
        rotateX(-.5f);
        rotateY(-.5f);
        background(0);
        fill(255,0,0);
        box(30);
        pushMatrix();
        translate(0,0,20);
        fill(0,0,255);
        box(5);
        popMatrix();
    }
}
