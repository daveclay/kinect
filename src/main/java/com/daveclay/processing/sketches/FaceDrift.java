package com.daveclay.processing.sketches;


import com.daveclay.processing.api.CanvasAware;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImageFrame;
import org.apache.commons.lang3.StringUtils;
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
    BlurGeneticGraphic blurGeneticGraphic;
    GlitchImages glitchImages;
    ImageFrame screenOverlay;
    ImageFrame imageFrame;
    PFont orator23;
    PFont orator9;

    public void setup() {
        size(800, 800, P2D);
        noiseColor = new NoiseColor(this, .002f);
        orator9 = createFont("OratorStd", 9);
        orator23 = createFont("OratorStd", 23);
        terminalTexts = new TerminalText[] {
                info(10, 60),
                info(10, 300),
                info(10, 660),
        };
        artLib = ImageLib.art(this);
        blurGeneticGraphic = new BlurGeneticGraphic(this);
        screenOverlay = new ImageFrame(this, loadImageByName(this, "screen-lines.png"), 0, 0);
        glitchImages = new GlitchImages(this, ImageLib.glitches(this));
        imageFrame = new ImageFrame(this, loadImageByName(this, artLib.files[3]), 10, 10);
    }

    public void draw() {
        background(0);
        blendMode(SCREEN);
        // tint(noiseColor.nextColor(255));
        blurGeneticGraphic.draw();
        noTint();
        for (TerminalText terminalText : terminalTexts) {
            terminalText.draw();
        }
        //screenOverlay.draw();
        //glitchImages.draw();
    }

    static class GlitchImages extends CanvasAware {
        private final ImageLib imageLib;
        private ImageFrame imageFrame;

        public GlitchImages(PApplet canvas, ImageLib imageLib) {
            super(canvas);
            this.imageLib = imageLib;
            imageLib.loadImages();
            next();
        }

        void draw() {
            if (canvas.random(1) > .6f) {
                next();
            }
            if (canvas.random(1) > .8f) {
                imageFrame.draw();
            }
        }

        void next() {
            PImage image = imageLib.pickRandomImage();
            float ratio = 1f;
            if (image.width > canvas.width) {
                ratio = (float) canvas.width / (float) image.width;
            } else if (image.height > canvas.height) {
                ratio = (float) canvas.height / (float) image.height;
            }

            if (ratio != 1f) {
                image.resize((int) (image.width * ratio), (int)(image.height * ratio));
            }
            imageFrame = new ImageFrame(canvas, image, 0, 0);
        }
    }

    static class BlurGeneticGraphic extends CanvasAware {
        private final ImageLib imageLib;
        private ImageFrame imageFrame;

        public BlurGeneticGraphic(PApplet canvas) {
            super(canvas);
            imageLib = ImageLib.face(canvas);
            imageLib.loadImages();
            next();
        }

        void draw() {
            if (imageFrame.blur().allBlack) {
                next();
            }
            imageFrame.draw();
        }

        void drawFadeOriginal() {
            canvas.pushStyle();
            canvas.tint(50, 100);
            imageFrame.drawDesaturated();
            canvas.popStyle();
        }

        void flickerDraw() {
            if (canvas.random(1f) > .93f) {
                canvas.tint(canvas.random(30), canvas.random(15), 0);
                canvas.pushMatrix();
                canvas.translate(-50 + (int) canvas.random(100), imageFrame.y);
                imageFrame.drawOriginal();
                canvas.popMatrix();
                canvas.noTint();
            }
        }

        void next() {
            PImage image = imageLib.pickRandomImage();
            float ratio = 1f;
            if (image.width > canvas.width) {
                ratio = (float) canvas.width / (float) image.width;
            } else if (image.height > canvas.height) {
                ratio = (float) canvas.height / (float) image.height;
            }

            if (ratio != 1f) {
                image.resize((int) (image.width * ratio), (int)(image.height * ratio));
            }
            imageFrame = new ImageFrame(canvas, image, 0, 0);
            imageFrame.desaturate();
        }
    }

    TerminalText info(int x, int y) {
        return new TerminalText(this, orator9, x, y);
    }

    public static PImage loadImageByName(PApplet canvas, String name) {
        return canvas.loadImage("/Users/daveclay/work/rebel belly after video/" + name);
    }
}
