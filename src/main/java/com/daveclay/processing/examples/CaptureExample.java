package com.daveclay.processing.examples;

import processing.core.PApplet;
import processing.video.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaptureExample extends PApplet {

    public static void main(String[] args) {
        PApplet.main(CaptureExample.class.getName());
    }

    Capture cam;

    public void setup() {
        size(640, 360);

        String[] cameras = Capture.list();

        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(i + " " + cameras[i]);
            }

            // The camera can be initialized directly using an
            // element from the array returned by list():
            cam = new Capture(this, cameras[3]);
            cam.start();
        }
    }

    public void draw() {
        if (cam.available()) {
            cam.read();
        }
        image(cam, 0, 0);
        // The following does the same, and is faster when just drawing the image
        // without any additional resizing, transformations, or tint.
        //set(0, 0, cam);
    }
}
