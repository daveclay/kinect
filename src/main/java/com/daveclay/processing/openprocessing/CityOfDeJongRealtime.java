package com.daveclay.processing.openprocessing;/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/2097*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */

import com.daveclay.processing.api.ColorUtils;
import com.daveclay.processing.api.SketchRunner;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.awt.*;

/**
 * Peter de Jong attractor applet by Thor Fr&#248;lich.<br>
 * <br>
 * Hold mouse button and move mouse to adjust variables.<br>
 * Release mouse button to render attractor in high quality.
 */

public class CityOfDeJongRealtime extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new CityOfDeJongRealtime());
    }

    private PeasyCam cam;
    private PFont font;
    float pa, pb, pc, pd; // deJong params
    float newx, newy, oldx, oldy, logmaxd;
    int imageWidth = 400;
    int maxdensity = 0;
    int pointsImageSize;
    private int[][] pointsImage;

    public void setup() {
        size(1400, 1000, OPENGL);
        background(0);
        pointsImage = new int[imageWidth][imageWidth];
        pointsImageSize = pointsImage.length * pointsImage[0].length;
        font = createFont("Arial", 16, true);
        params();
        for (int i = 0; i < pointsImageSize; i++) {
            int x = i % pointsImage.length;
            int y = i / pointsImage[0].length;
            pointsImage[x][y] = 0;
        }

        cam = new PeasyCam(this, 200);
        cam.setFreeRotationMode();
        plot();
    }

    public void draw() {
        background(0);
        plot();
        noLights();
        for (int i = 0; i < pointsImageSize; i += 10) {
            int x = i / pointsImage.length;
            int y = i % pointsImage[0].length;
            int density = pointsImage[x][y];
            if (density > 4) {
                pushMatrix();
                int stageX = (3 * imageWidth) - (5 * x);
                int stageY = (3 * imageWidth) - (5 * y);
                //int stageZ = -20 * density;
                int stageZ = (int) map(density, -2, 60, 100, -1200);
                translate(stageX, stageY, stageZ);

                pushStyle();
                float hue = map(density, 0, maxdensity, 0f, .3f);
                int color = Color.HSBtoRGB(hue, .7f, .8f);
                float alpha = map(density, 0, maxdensity, 0, 1f);
                stroke(ColorUtils.addAlpha(color, alpha));
                noFill();
                box(200);

                popStyle();
                rotateY(radians(180));

                pushStyle();
                textSize(11);
                textFont(font);
                fill(120);
                noStroke();
                text("C" + density, 32, 0, 0);
                popStyle();

                popMatrix();
            }
        }
    }

    public void params() {
        oldx = width/2;
        oldy = height/2;

        pa = -0.00901f;
        pb = -0.008024001f;
        pc = 0.00901f;
        pd = 0.008024001f;
    }

    public void plot() {
        for (int i = 0; i < 1200; i++) {
            newx = (((sin(pa * oldy) - cos(pb * oldx)) * imageWidth) + imageWidth /2);
            newy = (((sin(pc * oldx) - cos(pd * oldy)) * imageWidth) + imageWidth /2);
            if ((newx > 0) && (newx < imageWidth) && (newy > 0) && (newy < imageWidth) ) {
                int hi = pointsImage[(int)newx][(int)newy];
                if (hi > maxdensity) {
                    maxdensity = hi;
                }
                pointsImage[(int)newx][(int)newy] = ++hi;
            }
            oldx = newx;
            oldy = newy;
        }
    }

    class deJongAttractor {
        PImage image;
        float pa, pb, pc, pd, newx, newy, oldx, oldy, logmaxd;
        int IMAGE_SIZE = width;
        int maxdense = 0;
        int[][] density = new int[IMAGE_SIZE][IMAGE_SIZE];
        float[][] previousx = new float[IMAGE_SIZE][IMAGE_SIZE];

        void construct() {
            float sensitivity = 0.017f;
            pa = map(mouseX, 0, width, -1, 1) * sensitivity;
            pb = map(mouseY, 0, height, -1, 1) * sensitivity;
            pc = map(mouseX, 0, width, 1, -1) * sensitivity;
            pd = map(mouseY, 0, height, 1, -1) * sensitivity;
        }

        void construct(float pa, float pb, float pc, float pd) {
            //Produces the four variables to pass to the attractor
            this.pa = pa;
            this.pb = pb;
            this.pc = pc;
            this.pd = pd;
            oldx = width/2;
            oldy = height/2;
        }

        void populate(int samples, boolean clear) {
            //Populate array with density info with s number of samples
            if (clear) {
                for (int i = 0; i < IMAGE_SIZE; i++) {
                    for (int j = 0; j < IMAGE_SIZE; j++) {
                        density[i][j] = 0;
                        previousx[i][j] = 0;
                    }
                }
            }
            for (int i = 0; i < samples; i++) {
                for (int j = 0; j < 10000; j++) {

                    //De Jong's attractor
                    /*
                        http://paulbourke.net/fractals/peterdejong/
                        http://en.wikipedia.org/wiki/Attractor

                         xn+1 = d sin(a xn) - sin(b yn)
                         yn+1 = c cos(a xn) + cos(b yn)

                         n+1 = sin(a yn) - cos(b xn)
                         yn+1 = sin(c xn) - cos(d yn)
                     */
                    newx = (float) (((sin(pa * oldy) - cos(pb * oldx)) * IMAGE_SIZE * 0.2) + IMAGE_SIZE /2);
                    newy = (float) (((sin(pc * oldx) - cos(pd * oldy)) * IMAGE_SIZE * 0.2) + IMAGE_SIZE /2);

                    //Smoothie
                    newx += random(-0.001f, 0.001f);
                    newy += random(-0.001f, 0.001f);
                    //If coordinates are within range, up density count at its position
                    if ((newx > 0) && (newx < IMAGE_SIZE) && (newy > 0) && (newy < IMAGE_SIZE) ) {
                        density[(int)newx][(int)newy] += 1;
                        previousx[(int)newx][(int)newy] = oldx;
                    }
                    oldx = newx;
                    oldy = newy;
                }
            }
            //Put maximum density and its log()-value into variables
            for (int i = 0; i < IMAGE_SIZE; i++) {
                for (int j = 0; j < IMAGE_SIZE; j++) {
                    if (density[i][j] > maxdense) {
                        maxdense = density[i][j];
                        logmaxd = log(maxdense);
                    }
                }
            }
        }

        void incrementalupdate() {
            //Loops the non-clearing update and plotting to produce low-noise render
            populate(16, false);
            plot(80, false);
            redraw();
        }

        PImage plot(int factor, boolean clear) {
            //Plot image from density array
            if (image == null) {
                image = createImage(IMAGE_SIZE, IMAGE_SIZE, RGB);
            }
            image.loadPixels();
            for (int i = 0; i < IMAGE_SIZE; i++) {
                for (int j = 0; j < IMAGE_SIZE; j++) {
                    if (density[i][j] > 0) {
                        float myhue = map(previousx[i][j], 0, IMAGE_SIZE, 128, 255); //Select hue based on the x-coord that gave rise to current coord
                        float mysat = map(log(density[i][j]), 0, logmaxd, 128, 0);
                        float mybright = map(log(density[i][j]), 0, logmaxd, 0, 255) + factor;
                        int newc = color(myhue, mysat, mybright);
                        int oldc = image.pixels[i * IMAGE_SIZE + j];
                        newc = blendColor(newc, oldc, SOFT_LIGHT);
                        image.pixels[i * IMAGE_SIZE + j] = newc;
                    }
                }
            }
            image.updatePixels();
            return image;
        }
    }

}

