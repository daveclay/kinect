package com.daveclay.processing.examples.vector;

import processing.core.PApplet;
import processing.core.PVector;

public class PMatrix3DTest extends PApplet {

    public static void main(String[] args) {
        PApplet.main(PMatrix3DTest.class.getName());
    }

    processing.core.PMatrix3D cam;
    private float angleX;
    private float angleY;
    private float angleZ;

    public void setup() {
        size(800, 800, P3D); //OPENGL);
        cam = new processing.core.PMatrix3D();
    }

    public void draw()
    {
        background(80);
        lights();

        cam.rotateX(angleX);
        cam.rotateY(angleY);
        cam.rotateZ(angleZ);

        PVector x = new PVector();
        cam.mult(new PVector(1, 0, 0), x);

        PVector y = new PVector();
        cam.mult(new PVector(0, 1, 0), y);

        PVector z = new PVector();
        cam.mult(new PVector(0, 0, 1), z);

        // vector cross product: results in a vector pointing perpendicular from the plane that the x and y vectors make.
        PVector d = x.cross(y);
        d.normalize();

        pushMatrix();
        translate(width / 2, height / 2, -500);

        applyMatrix(cam);

        fill(color(255, 0, 0));
        stroke(color(255, 255, 255));
        box(200);

        pushMatrix();
        translate(0, 0, -10000);
        noStroke();
        fill(color(255, 0, 0));
        box(1, 1, 30000);
        popMatrix();

        pushMatrix();
        translate(-10000, 0, 0);
        noStroke();
        fill(color(0, 255, 0));
        box(30000, 1, 1);
        popMatrix();

        pushMatrix();
        translate(0, -10000, 0);
        noStroke();
        fill(color(0, 0, 255));
        box(1, 30000, 1);
        popMatrix();


        popMatrix();

        textSize(11);
        fill(color(255, 255, 255));
        text("x: " + x + "\ny: " + y + "\nz: " + z, 30, 30);
    }

    public void keyPressed() {
        System.out.println("keyCode: " + keyCode);
        switch(keyCode)
        {
            case LEFT:
                angleX += .01;
                break;
            case RIGHT:
                angleX -= .01;
                break;
            case UP:
                angleY += .01;
                break;
            case DOWN:
                angleY -= .01;
                break;
            case 82: // r
                break;
        }
    }

}
