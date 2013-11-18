package com.daveclay.processing.examples.camera;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class PMatrix3DCamera extends PApplet {
    public static void main(String[] args) {
        PApplet.main(PMatrix3DCamera.class.getName());
    }

    final static int R = 1000;
    PMatrix3D cam;
    float[][] stars;

    public void setup()
    {
        size(800, 800, P3D); //OPENGL);
        stars = new float[1500][3];
        for(int i = 0; i < stars.length; i++)
        {
            float p = random(-PI, PI);
            float t = asin(random(-1, 1));
            stars[i] = new float[] {
                    R * cos(t) * cos(p),
                    R * cos(t) * sin(p),
                    R * sin(t)
            };
        }
        cam = new PMatrix3D();
    }

    public void draw()
    {
        background(80);
        lights();

        translate(0, 0, 0);
        fill(color(255, 0, 0));
        sphere(500);

        translate(5, 1, 0);
        fill(color(0, 180, 180));
        sphere(20);

        cam.rotateX(-(mouseY - height / 2.0f) / height / 20f);
        cam.rotateY(-(mouseX - width  / 2.0f) / width  / 20f);

        PVector x = new PVector();
        cam.mult(new PVector(1, 0, 0), x);

        PVector y = new PVector();
        cam.mult(new PVector(0, 1, 0), y);

        PVector d = x.cross(y);
        d.normalize();
        d.mult(R);

        camera(0, 0, 0, d.x, d.y, d.z, y.x, y.y, y.z);

        textSize(32);
        fill(color(255, 255, 255));
        text("Hi there.", 10, 10);

        /*
        for(int i = 0; i < stars.length; i++)
        {
            pushMatrix();
            float[] p = stars[i];
            translate(stars[i][0], stars[i][1], stars[i][2]);
            sphere(5);
            popMatrix();
        }
        camera();
        stroke(255);
        line(width / 2 - 9, height / 2 - 0, width / 2 + 8, height / 2 + 0);
        line(width / 2 - 0, height / 2 - 9, width / 2 + 0, height / 2 + 8);
        */
    }

}
