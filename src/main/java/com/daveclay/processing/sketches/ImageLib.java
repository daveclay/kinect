package com.daveclay.processing.sketches;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageLib {

    public PImage[] images;
    public List<String> files;
    private PApplet canvas;

    public ImageLib(PApplet canvas, List<String> files) {
        this.files = files;
        this.canvas = canvas;
        this.images = new PImage[files.size()];
    }

    public ImageLib(PApplet canvas, String[] files) {
        this(canvas, Arrays.asList(files));
    }

    PImage[] pickRandomImages() {
        int num = (int) canvas.random(5);
        PImage[] picked = new PImage[num];
        for (int i = 0; i < num; i++) {
            picked[i] = pickRandomImage();
        }
        return picked;
    }

    PImage pickRandomImage() {
        int index = (int) canvas.random(images.length);
        return images[index];
    }

    ImageLib loadImages() {
        for (int i = 0; i < files.size(); i++) {
            images[i] = loadImageByName(canvas, files.get(i));
            images[i].loadPixels();
        }
        return this;
    }

    public static PImage loadImageByName(PApplet canvas, String name) {
        return canvas.loadImage("/Users/daveclay/work/rebel belly after video/" + name);
    }

    public static ImageLib components(PApplet canvas) {
        List<String> imageFiles = new ArrayList<>();
        File dir = new File("/Users/daveclay/work/rebel belly after video/components");
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".png")) {
                imageFiles.add(file.getAbsolutePath());
            }
        }
        return new ImageLib(canvas, imageFiles);
    }

    public static ImageLib face(PApplet canvas) {
        return new ImageLib(canvas, new String[] {
                "face-2.png",
                "face-5.png",
                "face-6.png",
                "face-7.png",
                "face-8.png",
                "face-9.png",
                "face-10.png",
                "face-11.png",
                "face-12.png",
                "face-13.png",
                "face-14.png"
        });
    }

    public static ImageLib geneticsOverlay(PApplet canvas) {
        return new ImageLib(canvas, new String[] {
                "screen-lines.png"
        });
    }

    public static ImageLib genetics(PApplet canvas) {
        return new ImageLib(canvas, new String[] {
                "genetics-circle-1.png",
                "genetics-circle-2.png",
                "genetics-circle-3.png",
                "genetics-circle-4.png",
                "genetics-circle-5.png",
                "genetics-circle-6.png",
                "genetics-circle-7.png",
                "genetics-chart.png",
                "genetics-column-glow.png"
        });
    }

    public static ImageLib art(PApplet canvas) {
        return new ImageLib(canvas, new String[] {
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
                "within.png" });
    }

    public static ImageLib glitches(PApplet canvas) {
        return new ImageLib(canvas, new String[] {
                "genetics-circle-glitch-1.png",
                "genetics-circle-glitch-2.png",
                "genetics-circle-glitch-3.png",
                "genetics-circle-glitch-4.png",
                "genetics-circle-glitch-6.png",
                "genetics-circle-glitch-5.png"
        });
    }
}
