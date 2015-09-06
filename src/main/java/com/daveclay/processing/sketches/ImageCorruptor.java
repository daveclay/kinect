package com.daveclay.processing.sketches;


import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Map;

public class ImageCorruptor extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new ImageCorruptor());
    }

    ImgProc imgProc;

    public void setup() {
        size(800, 800, P3D);
        imgProc = new ImgProc(this);
        loadImages();
        background(0);
    }

    public void draw() {
        fill(0, 3);
        rect(0, 0, width, height);
        PImage[] imgs = pickRandomImages();
        if (frameCount % 20 == 0) {
            for (int i = 0; i < imgs.length; i++) {
                PImage image = imgs[i];
                int x = 0; // (int) random(image.width);
                int y = (int) random(image.height);
                int width = image.width;
                int height = Math.abs(((int) random(image.height)) - y - 1);
                blend(image, x, y, width, height,
                        (int)random(width), (int)random(height),
                        width, height, ADD);
            }
        }
        //imgProc.simpleBlur();
    }

    PImage[] pickRandomImages() {
        int num = (int) random(5);
        PImage[] picked = new PImage[num];
        for (int i = 0; i < num; i++) {
            picked[i] = pickRandomImage();
        }
        return picked;
    }

    PImage pickRandomImage() {
        return images[(int) random(images.length)];
    }

    void loadImages() {
        for (int i = 0; i < files.length; i++) {
            images[i] = loadImage("/Users/daveclay/work/rebel belly after video/" + files[i]);
            images[i].loadPixels();
        }
    }

    String[] files = new String[] {
            "2aF.png",
            "call III.png",
            "dup rejesus process.png",
            "identify.png",
            "insect I.png",
            "insect V red.png",
            "light, movement II.png",
            "medic.png",
            "messianic.png",
            "pump six.png",
            "untitled body I.png",
            "untitled body IV.png",
            "untitled connection II.png",
            "untitled connection III.png",
            "untitled connection V.png",
            "untitled figure III.png",
            "untitled figure IV.png",
            "untitled form I.png",
            "untitled form III.png",
            "untitled machine I.png",
            "untitled machine II.png",
            "untitled machine III.png",
            "untitled motion I.png",
            "untitled motion II.png",
            "untitled recline III.png",
            "untitled texture IV.png",
            "within.png" };

    PImage[] images = new PImage[files.length];
}
