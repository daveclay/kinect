package com.daveclay.processing.sketches;

import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.awt.*;

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

    ImgProc imgProc;
    PFont orator9;
    PFont orator23;

    NoiseColor noiseColor;

    public void setup() {
        size(1600, 800, P2D);

        orator9 = createFont("OratorStd", 9);
        orator23 = createFont("OratorStd", 23);

        noiseColor = new NoiseColor(this, .01f);

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
        Dimension size = new Dimension(60, 60);
        int offset = 10;
        int x = mouseX - 25;
        int y = mouseY - 25;

        background(0);

        sprite.beginDraw();
        sprite.background(0);
        drawRect(sprite, offset, size, noiseColor.nextColor(255));
        sprite.endDraw();

        screenBlur.beginDraw();
        screenBlur.blendMode(SCREEN);
        screenBlur.image(sprite, x, y);
        blur(screenBlur, 9, 2f);
        screenBlur.filter(badBlur);
        screenBlur.endDraw();

        image(screenBlur, 0, 0);
        strokeWeight(1);
        noFill();
        stroke(color(255, 100));
        rect(x + offset, y + offset, size.width, size.height);
        line(x + offset, y + offset, x + size.width + offset, y + size.height + offset);
        line(x + offset, y + size.height + offset, x + size.width + offset, y + offset);

        textFont(orator9);
        fill(255, 120);
        text("0x" + Integer.toHexString(frameCount).toUpperCase(), x + size.height + offset, y + offset + 23);
        text("[" + x + "," + y + "]", x + size.height + offset, y + 46);
    }

    void drawRect(PGraphics graphics, int offset, Dimension size, int color) {
        graphics.pushStyle();
        graphics.strokeWeight(1);
        graphics.noFill();
        graphics.stroke(color);
        // blur size...
        graphics.rect(offset, offset, size.width, size.height);
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
