package com.daveclay.processing.sketches.testing;

import com.daveclay.processing.api.SketchRunner;
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

    public void setup() {
        size(1600, 900, P2D);

        File f = new File(System.getProperty("user.dir") + "/src/main/resources/shaders/gaussianBlur.glsl");
        gaussianBlur = loadShader(f.getAbsolutePath());
        gaussianBlur.set("kernelSize", 12); // How big is the sampling kernel?
        gaussianBlur.set("strength", 8f); // How strong is the gaussianBlur?

        f = new File(System.getProperty("user.dir") + "/src/main/resources/shaders/colorSeparation.glsl");
        chromaticAbberation = loadShader(f.getAbsolutePath());

        f = new File(System.getProperty("user.dir") + "/src/main/resources/shaders/pixellate.glsl");
        pixellate = loadShader(f.getAbsolutePath());
    }

    public void draw() {
        background(10);

        // Show the values on screen
        fill(100, 0, 0);
        strokeWeight(4);
        stroke(255, 128, 0);
        rect(5, 5, 130, 145);
        stroke(0, 128, 255);
        rect(205, 50, 130, 145);
        stroke(255, 228, 255);
        rect(305, 200, 130, 145);

        fill(255);
        rect(500, 200, 200, 200);

        blur();
        pixellate();
        chroma();
    }

    void chroma() {
        chromaticAbberation.set("time", (float) millis() / 1000f);
        filter(chromaticAbberation);
    }

    void pixellate() {
        pixellate.set("cellSize", ((float)mouseY / (float)height) * .1f);
        filter(pixellate);
    }

    public void blur() {
        gaussianBlur.set("horizontalPass", 0);
        filter(gaussianBlur);

        // Horizontal pass
        gaussianBlur.set("horizontalPass", 1);
        filter(gaussianBlur);
    }
}
