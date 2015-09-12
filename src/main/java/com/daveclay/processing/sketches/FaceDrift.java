package com.daveclay.processing.sketches;


import com.daveclay.processing.api.CanvasAware;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImageFrame;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class FaceDrift extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new FaceDrift());
    }

    NoiseColor noiseColor;
    TerminalText[] terminalTexts;
    ImageLib artLib;
    BlurFace blurFace;
    ImageFrame screenOverlay;
    ImageFrame imageFrame;
    PFont orator23;
    PFont orator9;
    ImageFrame test;
    int count = 0;

    public void setup() {
        size(800, 800, JAVA2D);
        noiseColor = new NoiseColor(this, .002f);
        orator9 = createFont("OratorStd", 9);
        orator23 = createFont("OratorStd", 23);
        terminalTexts = new TerminalText[] {
                info(10, 60),
                info(10, 300),
                info(10, 660),
        };
        artLib = ImageLib.art(this);
        blurFace = new BlurFace(this);
        screenOverlay = new ImageFrame(this, loadImageByName(this, "screen-lines.png"), 0, 0);
        imageFrame = new ImageFrame(this, loadImageByName(this, artLib.files[3]), 10, 10);
        test = blurFace.next();
    }

    public void draw() {
        background(0);
        blendMode(SCREEN);
        blurFace.draw();
        /*
        drawTest();
        */
    }

    void drawTest() {
        test.blur();
        ImgProc.BlurResult result = ImgProc.checkImage(test.blurImg);
        System.out.println("count: " + count + ": " + result.count);
        image(test.blurImg, 0, 0);
        count++;
    }

    public void mouseClicked() {
        blurFace.scramble();
    }

    static class BlurFace extends CanvasAware {
        private final ImageLib imageLib;

        private ImageFrame[] imageFrames = new ImageFrame[4];

        public BlurFace(PApplet canvas) {
            super(canvas);
            imageLib = ImageLib.face(canvas);
            imageLib.loadImages();
            for (int i = 0; i < imageFrames.length; i++) {
                imageFrames[i] = next();
            }
        }

        public void scramble() {
            int index = (int) random(imageFrames.length);
            imageFrames[index] = next();
        }

        void draw() {
            for (int i = 0; i < imageFrames.length; i++) {
                ImageFrame imageFrame = imageFrames[i];
                imageFrame.iterations++;
                int width = imageFrame.img.width;
                int height = imageFrame.img.height;

                ImgProc.BlurResult result = imageFrame.blur();
                PImage target = imageFrame.blurImg;

                if (imageFrame.iterations > 0) {
                    if (result.count < 20) {
                        imageFrames[i] = next();
                    }
                    if (i < imageFrames.length - 1) {
                        ImageFrame prev = imageFrames[i + 1];
                        target.blend(prev.blurImg, 0, 0, width, height, 0, 0, width, height, MULTIPLY);
                        canvas.image(target, 0, 0);
                    }
                } else {
                    PImage fill = new PImage(width, height);
                    ImgProc.grayFill(fill, min(255, (255 / 30) * imageFrame.iterations));
                    target = ImgProc.copy(target);
                    target.blend(fill, 0, 0, width, height, 0, 0, width, height, MULTIPLY);
                    canvas.image(target, 0, 0);
                }
            }
        }

        ImageFrame next() {
            PImage image = imageLib.pickRandomImage();
            image.resize(600, 800);
            ImageFrame imageFrame = new ImageFrame(canvas, image, 0, 0);
            imageFrame.desaturate();
            return imageFrame;
        }
    }

    TerminalText info(int x, int y) {
        return new TerminalText(this, orator9, x, y);
    }

    public static PImage loadImageByName(PApplet canvas, String name) {
        return canvas.loadImage("/Users/daveclay/work/rebel belly after video/" + name);
    }
}
