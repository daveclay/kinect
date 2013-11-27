package com.daveclay.processing.examples.opencv;

import gab.opencv.OpenCV;
import gab.opencv.OpenCVProcessingUtils;
import processing.core.PApplet;

import java.awt.*;

public class FaceRecognitionVideo extends PApplet {
    public static void main(String[] args) {
        PApplet.main(FaceRecognitionVideo.class.getName());
    }

    OpenCVProcessingUtils opencv;
    Rectangle[] faces;

    public void setup() {
        opencv = new OpenCVProcessingUtils(this, 640, 320);
        size(opencv.width, opencv.height);

        opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
        faces = opencv.detect();
    }

    public void draw() {

        noFill();
        stroke(0, 255, 0);
        strokeWeight(3);
        for (int i = 0; i < faces.length; i++) {
            rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
        }
    }}
