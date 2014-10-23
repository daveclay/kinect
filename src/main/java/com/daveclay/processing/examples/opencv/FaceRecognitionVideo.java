package com.daveclay.processing.examples.opencv;

import com.daveclay.processing.video.capture.CaptureUtils;
import gab.opencv.OpenCV;
import gab.opencv.OpenCVProcessingUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;
import static com.daveclay.opencv.OpenCVConstants.*;

import java.awt.*;

public class FaceRecognitionVideo extends PApplet {

    public static void main(String[] args) {
        PApplet.main(FaceRecognitionVideo.class.getName());
    }

    private Capture capture;
    OpenCVProcessingUtils opencv;
    Rectangle[] faces;

    public void setup() {
        opencv = new OpenCVProcessingUtils(this, 640, 360);
        capture = new CaptureUtils().openByName(this, "name=FaceTime HD Camera (Built-in),size=640x360,fps=30");
        size(opencv.width, opencv.height, P2D);

        opencv.loadCascade(LBP_CASCADES_FRONTAL_FACE_PATH);
    }

    public void draw() {
        if (capture.available()) {
            capture.read();

            capture.loadPixels();
            PImage img = capture.get();

            opencv.loadImage(img);
            faces = opencv.detect();

            image(img, 0, 0);

            noFill();
            stroke(0, 255, 0);
            strokeWeight(3);
            for (int i = 0; i < faces.length; i++) {
                rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
            }
        }
    }}
