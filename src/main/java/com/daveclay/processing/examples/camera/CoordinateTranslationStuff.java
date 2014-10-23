package com.daveclay.processing.examples.camera;

import processing.core.PApplet;

public class CoordinateTranslationStuff extends PApplet {

    public static void main(String[] args) {
        PApplet.main(CoordinateTranslationStuff.class.getName());
    }

    public void setup()
    {
        size(200,200);
        background(255);

        stroke(0x77ff00ff);
        rect(20, 20, 40, 40);

        pushMatrix();
        scale(2.0f);
        rect(20, 20, 40, 40);
        popMatrix();
    }}
