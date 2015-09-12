package com.daveclay.processing.examples.vector;

import processing.core.PApplet;
import processing.core.PVector;

public class VectorSubtraction extends PApplet {
    public static void main(String[] args) {
        PApplet.main(VectorSubtraction.class.getName());
    }

    public void setup() {
        size(800,200);
        smooth();
    }

    public void draw() {
        background(255);

        PVector mouse = new PVector(mouseX,mouseY);
        PVector center = new PVector(width/2,height/2);
        mouse.sub(center);

        translate(width/2,height/2);
        strokeWeight(2);
        stroke(0);
        line(0,0,mouse.x,mouse.y);

    }

}
