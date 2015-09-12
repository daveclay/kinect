package com.daveclay.processing.examples.opencv;

import gab.opencv.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.Rectangle;

public class FaceRecognition extends PApplet {
    public static void main(String[] args) {
        PApplet.main(FaceRecognition.class.getName());
    }

    OpenCV opencv;
    Rectangle[] faces;

    public void setup() {
        opencv = new OpenCV(this, "test.jpg");
        size(opencv.width, opencv.height);

        opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
        faces = opencv.detect();
    }

    public void draw() {
        image(opencv.getInput(), 0, 0);

        noFill();
        stroke(0, 255, 0);
        strokeWeight(3);
        for (int i = 0; i < faces.length; i++) {
            rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
        }
    }}
