package com.daveclay.processing.openprocessing.NebulaDefault;/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/1412*@* */

import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.opengl.PShader;

/* !do not delete the line above, required for linking your tweak if you upload again */
//NEBULA
//Matt Schroeter
//December 1st, 2008
//matthanns.com
public class NebulaDefault extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new NebulaDefault());
    }

    float depth = 400;
    PShader gaussianBlur;
    PShader chromaticAbberation;
    PShader pixellate;
    PShader barrelBlurChroma;

    public void setup(){
        size(1600, 800, OPENGL);
        noStroke();
        gaussianBlur = ImgProc.shader(this, "gaussianBlur");
        gaussianBlur.set("kernelSize", 32); // How big is the sampling kernel?
        gaussianBlur.set("strength", 30f); // How strong is the gaussianBlur?

        chromaticAbberation = ImgProc.shader(this, "colorSeparation");
        pixellate = ImgProc.shader(this, "pixellate");
        barrelBlurChroma = ImgProc.shader(this, "barrelBlurChroma");
        barrelBlurChroma.set("sketchSize", (float) width, (float) height);
    }

    public void draw(){
        float cameraY = height/1;
        float cameraX = width/1;

        translate(width/2, height/2, -depth/2);

        rotateY(frameCount*PI/500);

        float fov = cameraX/(width * PI/2);
        float cameraZ = cameraY / tan(fov / 2.0f);
        float aspect = (float)(width/height);

        perspective(fov, aspect, cameraZ/2000.0f, cameraZ*4000.0f);


        translate(width/10, height/10, depth/2);

        for(int i=0; i<2; i++) {
            float r = random(100);
            directionalLight(2, 83, 115, // Color
                    1, 10, 0); // The x-, y-, z-axis direction'
            directionalLight(3, 115, 140, // Color
                    10, 10, 0); // The x-, y-, z-axis direction'
        }


        for(int i=0; i<10; i++) {

            float r = random(20);

            rotateX(frameCount*PI/1000);

            //alt effect
            //rotateY(frameCount*PI/1000);

            for (int y = -2; y < 2; y++) {
                for (int x = -2; x < 2; x++) {
                    for (int z = -2; z < 2; z++) {

                        pushMatrix();
                        translate(400*x, 300*y, 300*z);
                        box(5, 5, random(100));
                        popMatrix();

                        pushMatrix();
                        translate(400*x, 300*y, 50*z);
                        box(random(100) + 30, 5, 5);
                        popMatrix();

                        pushMatrix();
                        translate(400*x, 10*y, 50*z);
                        box(random(500), 5, 5);
                        popMatrix();

                        pushMatrix();
                        rotateY(frameCount*PI/400);
                        translate(100*x, 300*y, 300*z);
                        box(random(60), random(40), 20);
                        popMatrix();

                    }
                }
            }
        }

        barrelBlurChroma();
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
