package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class KernelTest extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new KernelTest());
    }

    private PImage out;
    Capture video;
    NoiseColor cnoise = new NoiseColor(this, .01f);
    ImgProc imgProc;
    float[][] sharpenKernel = {{ -1, -1, -1},
            { -1,  13, -1},
            { -1, -1, -1}};

    float[][] kernel = {{ .1f, .1f, .1f},
            { .1f,  2f, .1f},
            { .1f, .1f, .1f}};

    public void setup() {
        size(640, 480);
        imgProc = new ImgProc(this);
        imgProc.setupPixelFrames();
        video = new Capture(this, width, height);
        video.start();
        out = createImage(width, height, RGB);
    }

    public void draw() {
        int r, g, b = 0;
        if (video.available()) {
            video.read();
        }
        blendMode(LIGHTEST);
        loadPixels();

        processKernel(null, video, kernel);
        tint(color(255, 0, 0), .5f);

        //imgProc.simpleBlur();
    }

    public void processKernel(PImage out, PImage img, float[][] kernel) {
        for (int y = 1; y < img.height-1; y++) { // Skip top and bottom edges
            for (int x = 1; x < img.width-1; x++) { // Skip left and right edges
                float sum = 0; // Kernel sum for this pixel
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        // Calculate the adjacent pixel for this kernel point
                        int pos = (y + ky)*img.width + (x + kx);
                        // Image is grayscale, red/green/blue are identical
                        float val = red(img.pixels[pos]);
                        // Multiply adjacent pixels based on the kernel values
                        sum += kernel[ky+1][kx+1] * val;
                    }
                }
                // For this pixel in the new image, set the gray value
                // based on the sum from the kernel
                set(x, y, color(sum, sum, sum));
                //out.pixels[y*img.width + x] = color(sum, sum, sum);
            }
        }
    }
}
