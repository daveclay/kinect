package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.opengl.PShader;

import java.io.File;

public class SepBlur extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new SepBlur());
    }

    /**
     * Separate Blur Shader
     *
     * This gaussianBlur shader works by applying two successive passes, one horizontal
     * and the other vertical.
     *
     * Press the mouse to switch between the custom and default shader.
     */

    PShader gaussianBlur;
    PShader chromaticAbberation;
    PShader pixellate;
    PShader barrelBlurChroma;

    public void setup() {
        size(1600, 900, P2D);

        gaussianBlur = ImgProc.shader(this, "gaussianBlur");
        gaussianBlur.set("kernelSize", 12); // How big is the sampling kernel?
        gaussianBlur.set("strength", 8f); // How strong is the gaussianBlur?

        chromaticAbberation = ImgProc.shader(this, "colorSeparation");
        pixellate = ImgProc.shader(this, "pixellate");
        barrelBlurChroma = ImgProc.shader(this, "barrelBlurChroma");
        barrelBlurChroma.set("sketchSize", (float)width, (float)height);
    }

    public void draw() {
        background(0);

        int color = color(255, 220, 200);
        // Show the values on screen
        pushStyle();
        strokeWeight(6);
        noFill();
        stroke(color);
        rect(500, 300, 100, 100);
        popStyle();


        blur();
        /*
        barrelBlurChroma();
        pixellate();
        chroma();
        */
    }

    void chroma() {
        if (random(1f) > .7f) {
            chromaticAbberation.set("time", (float) millis() / 1000f);
            filter(chromaticAbberation);
        }
    }

    void pixellate() {
        pixellate.set("cellSize", ((float)mouseY / (float)height) * .1f);
        filter(pixellate);
    }

    void barrelBlurChroma() {
        filter(barrelBlurChroma);
    }

    void blur() {
        gaussianBlur.set("horizontalPass", 0);
        filter(gaussianBlur);

        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        filter(gaussianBlur);
    }
}
