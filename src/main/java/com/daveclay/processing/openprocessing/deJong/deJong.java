package com.daveclay.processing.openprocessing.deJong;/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/2097*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Peter de Jong attractor applet by Thor Fr&#248;lich.<br>
 * <br>
 * Hold mouse button and move mouse to adjust variables.<br>
 * Release mouse button to render attractor in high quality.
 */

public class deJong extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new deJong());
    }

    deJongAttractor dj;
    boolean stop;
    int stepCounter;

    public void setup() {
        size(640, 640);
        noFill();
        smooth();
        colorMode(HSB, 255);
        dj = new deJongAttractor();
        dj.reparam();
    }

    public void draw() {
        if (!stop) {
            stepCounter++;
            if (stepCounter > 127) {
                stop = true;
                return;
            }
            dj.incrementalupdate();
        }
        image(dj.pi, 0, 0, width, height);
    }

    public void mouseDragged() {
        noLoop();
        stop = true;
        dj.reparam();
        redraw();
    }

    public void mouseReleased() {
        loop();
        stop = false;
        stepCounter = 0;
        dj.updateloop();
    }

    class deJongAttractor {
        PImage pi;
        float pa, pb, pc, pd, newx, newy, oldx, oldy, logmaxd;
        int N = width;
        int maxdense = 0;
        int[][] density = new int[N][N];
        float[][] previousx = new float[N][N];

        void construct() {
            //Produces the four variables to pass to the attractor
            float sensitivity = 0.017f;
            pa = map(mouseX, 0, width, -1, 1) * sensitivity;
            pb = map(mouseY, 0, height, -1, 1) * sensitivity;
            pc = map(mouseX, 0, width, 1, -1) * sensitivity;
            pd = map(mouseY, 0, height, 1, -1) * sensitivity;
            oldx = width/2;
            oldy = height/2;
        }

        void populate(int s, boolean c) {
            //Populate array with density info with s number of samples
            int samples = s;
            boolean clear = c;
            if (clear) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
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
                    newx = (float) (((sin(pa * oldy) - cos(pb * oldx)) * N * 0.2) + N/2);
                    newy = (float) (((sin(pc * oldx) - cos(pd * oldy)) * N * 0.2) + N/2);


                    //Smoothie
                    newx += random(-0.001f, 0.001f);
                    newy += random(-0.001f, 0.001f);
                    //If coordinates are within range, up density count at its position
                    if ((newx > 0) && (newx < N) && (newy > 0) && (newy < N) ) {
                        density[(int)newx][(int)newy] += 1;
                        previousx[(int)newx][(int)newy] = oldx;
                    }
                    oldx = newx;
                    oldy = newy;
                }
            }
            //Put maximum density and its log()-value into variables
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (density[i][j] > maxdense) {
                        maxdense = density[i][j];
                        logmaxd = log(maxdense);
                    }
                }
            }
        }

        void updateloop() {
            stop = false;
            stepCounter = 0;
        }

        void incrementalupdate() {
            //Loops the non-clearing update and plotting to produce low-noise render
            populate(16, false);
            plot(0, false);
            redraw();
        }

        void reparam() {
            //Fast reparametrization of variables
            dj.construct();
            dj.populate(1, true);
            dj.plot(100, true);
        }

        PImage plot(int f, boolean c) {
            int factor = f;
            boolean clear = c;
            //Plot image from density array
            if (clear) {
                pi = createImage(N, N, RGB);
            }
            pi.loadPixels();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (density[i][j] > 0) {
                        float myhue = map(previousx[i][j], 0, N, 128, 255); //Select hue based on the x-coord that gave rise to current coord
                        float mysat = map(log(density[i][j]), 0, logmaxd, 128, 0);
                        float mybright = map(log(density[i][j]), 0, logmaxd, 0, 255) + factor;
                        int newc = color(myhue, mysat, mybright);
                        int oldc = pi.pixels[i * N + j];
                        newc = blendColor(newc, oldc, SOFT_LIGHT);
                        pi.pixels[i * N + j] = newc;
                    }
                }
            }
            pi.updatePixels();
            return pi;
        }

    }

}

