package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class MultipleGraphicsWithPShaders extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new MultipleGraphicsWithPShaders());
    }

    PShader badBlur;
    PShader gaussianBlur;
    PShader chromaticAbberation;
    PShader pixellate;
    PShader barrelBlurChroma;

    PGraphics sprite;
    PGraphics screenBlur;
    PGraphics multiplyMe;

    PImage previous;

    ImgProc imgProc;

    public void setup() {
        size(1600, 800, P2D);

        imgProc = new ImgProc(this);

        badBlur = ImgProc.shader(this, "badBlur");
        badBlur.set("sketchSize", (float) width, (float) height);

        gaussianBlur = ImgProc.shader(this, "gaussianBlur");

        chromaticAbberation = ImgProc.shader(this, "colorSeparation");
        pixellate = ImgProc.shader(this, "pixellate");
        barrelBlurChroma = ImgProc.shader(this, "barrelBlurChroma");
        barrelBlurChroma.set("sketchSize", (float) width, (float) height);

        sprite = createGraphics(400, 400, P2D);
        screenBlur = createGraphics(width, height, P2D);
        multiplyMe = createGraphics(width, height, P2D);

        bg(sprite);
        bg(screenBlur);
        background(255);
    }

    public void draw() {
        sprite.beginDraw();
        sprite.background(0);
        drawRect(sprite, color(255, 20, 0));
        sprite.endDraw();

        screenBlur.beginDraw();
        screenBlur.blendMode(SCREEN);
        screenBlur.image(sprite, mouseX - 250, mouseY - 250);
        blur(screenBlur, 12, 8f);
        screenBlur.filter(badBlur);
        screenBlur.endDraw();

        image(screenBlur, 0, 0);
        filter(badBlur);
    }

    void drawRect(PGraphics graphics, int color) {
        graphics.pushStyle();
        graphics.strokeWeight(6);
        graphics.noFill();
        graphics.stroke(color);
        graphics.rect(50, 50, 300, 300);
        graphics.popStyle();
        //blur(graphics, 12, 8f);
    }

    void bg(PGraphics graphics) {
        graphics.beginDraw();
        graphics.background(0);
        graphics.endDraw();
    }

    void chroma(PGraphics graphics) {
        if (random(1f) > .7f) {
            chromaticAbberation.set("time", (float) millis() / 1000f);
            graphics.filter(chromaticAbberation);
        }
    }

    void pixellate(PGraphics graphics) {
        pixellate.set("cellSize", ((float)mouseY / (float)height) * .1f);
        graphics.filter(pixellate);
    }

    void barrelBlurChroma(PGraphics graphics) {
        graphics.filter(barrelBlurChroma);
    }

    void blur(int size, float strength) {
        gaussianBlur.set("kernelSize", size); // How big is the sampling kernel?
        gaussianBlur.set("strength", strength); // How strong is the gaussianBlur?

        gaussianBlur.set("horizontalPass", 0);
        filter(gaussianBlur);
        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        filter(gaussianBlur);
    }

    void blur(PGraphics graphics, int size, float strength) {
        gaussianBlur.set("kernelSize", size); // How big is the sampling kernel?
        gaussianBlur.set("strength", strength); // How strong is the gaussianBlur?

        gaussianBlur.set("horizontalPass", 0);
        graphics.filter(gaussianBlur);
        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        graphics.filter(gaussianBlur);
    }
}
