package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class MultipleGraphicsWithPShaders extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new MultipleGraphicsWithPShaders());
    }

    PShader gaussianBlur;
    PShader chromaticAbberation;
    PShader pixellate;
    PShader barrelBlurChroma;

    PGraphics graphicsA;
    PGraphics graphicsB;

    public void setup() {
        size(1600, 800, P2D);

        gaussianBlur = ImgProc.shader(this, "gaussianBlur");
        gaussianBlur.set("kernelSize", 12); // How big is the sampling kernel?
        gaussianBlur.set("strength", 8f); // How strong is the gaussianBlur?

        chromaticAbberation = ImgProc.shader(this, "colorSeparation");
        pixellate = ImgProc.shader(this, "pixellate");
        barrelBlurChroma = ImgProc.shader(this, "barrelBlurChroma");
        barrelBlurChroma.set("sketchSize", (float) width, (float) height);

        graphicsA = createGraphics(width, height);
        graphicsB = createGraphics(width, height);

        graphicsA.background(0);
        graphicsB.background(0);
        background(0);
    }

    public void draw() {
        blendMode(SCREEN);

        drawRect(graphicsA, 70, color(55, 20, 0));
        drawRect(graphicsB, 600,color(5, 20, 40));

        image(graphicsA, 0, 0);
        image(graphicsB, 0, 0);
    }

    void drawRect(PGraphics graphics, int y, int color) {
        graphics.pushStyle();
        graphics.strokeWeight(6);
        graphics.noFill();
        graphics.stroke(color);
        graphics.rect(90, y, 500, 500);
        graphics.popStyle();
        blur(graphics);
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

    void blur(PGraphics graphics) {
        gaussianBlur.set("horizontalPass", 0);
        graphics.filter(gaussianBlur);

        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        graphics.filter(gaussianBlur);
    }
}
