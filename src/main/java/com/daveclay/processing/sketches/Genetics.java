package com.daveclay.processing.sketches;


import com.daveclay.processing.api.CanvasAware;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImageFrame;
import com.daveclay.processing.api.image.ImgProc;
import org.apache.commons.lang3.StringUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import static com.daveclay.processing.api.image.ImgProc.loadImageByName;

public class Genetics extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new Genetics());
    }

    NoiseColor noiseColor;
    TerminalText[] terminalTexts;
    ImageLib artLib;
    BlurGeneticGraphic blurGeneticGraphic;
    GeneticID geneticID;
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
                info(10, 60)
        };
        artLib = ImageLib.art(this);
        geneticID = new GeneticID();
        blurGeneticGraphic = new BlurGeneticGraphic(this);
        screenOverlay = new ImageFrame(this, loadImageByName(this, "screen-lines.png"), 0, 0);
        glitchImages = new GlitchImages(this, ImageLib.glitches(this));
        imageFrame = new ImageFrame(this, loadImageByName(this, artLib.files.get(3)), 10, 10);
    }

    public void draw() {
        background(0);
        blendMode(SCREEN);
        // tint(noiseColor.nextColor(255));
        //blurGeneticGraphic.draw();
        noTint();
        geneticID.draw();
        for (TerminalText terminalText : terminalTexts) {
            terminalText.draw();
        }
        //screenOverlay.draw();
        // glitchImages.draw();
    }

    class GeneticID {

        String s;

        void draw() {
            if (s == null || random(1) > .78f) {
                next();
            }

            pushStyle();
            textFont(orator23);
            text(s, 10, 50);
            popStyle();
        }

        void next() {
            s = Integer.toHexString(frameCount).toUpperCase() + "::" + Float.toHexString(frameRate);
            /*
            s = random(1) > .5f ? "TX" : "RX";
            s += StringUtils.rightPad(Integer.toHexString(64 + (int) random(670)).toUpperCase(), 4, "X") + "/";
            s += random(1) > .9f ? "!ERR" : Integer.toHexString(frameCount);
            */
        }
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
            imageLib = ImageLib.art(canvas);
            imageLib.loadImages();
            next();
        }

        void draw() {
            if (imageFrame.blur().allBlack) {
                next();
            }
            imageFrame.draw();
            drawFadeOriginal();
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

    static class GeneticSeries extends CanvasAware {
        GeneticBand[] geneticBands = new GeneticBand[50];

        public GeneticSeries(PApplet canvas) {
            super(canvas);
            int prevX = 0;
            for (int i = 0; i < geneticBands.length; i++) {
                geneticBands[i] = new GeneticBand(canvas, prevX, 175);
                prevX += geneticBands[i].width + 2;
            }
        }

        void draw() {
            for (GeneticBand geneticBand : geneticBands) {
                geneticBand.draw();
            }
        }
    }

    static class GeneticBand extends CanvasAware {
        final PApplet canvas;
        NoiseColor noiseColor;
        int width;
        int height = 40;
        int x = 0;
        int y = 0;

        public GeneticBand(PApplet canvas, int x, int y) {
            super(canvas);
            this.canvas = canvas;
            this.x = x;
            this.y = y;
            noiseColor = new NoiseColor(canvas, .01f);
            this.width = (int) canvas.random(12);
        }

        void draw() {
            pushStyle();
            noStroke();
            fill(noiseColor.nextColor(255));
            roundrect(x, y, width, height, 10);
            popStyle();
        }
    }

    TerminalText info(int x, int y) {
        return new TerminalText(this, orator9, x, y);
    }
}
