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

        // Note that this never calls rotateZ...
        // There are orientations that are impossible with a rotation on the Z-axis (upright along the Z-axis looking down the X-axis, for example)

        PVector x = new PVector();
        cam.mult(new PVector(1, 0, 0), x);

        PVector y = new PVector();
        cam.mult(new PVector(0, 1, 0), y);

        // vector cross product: results in a vector pointing perpendicular from the plane that the x and y vectors make.
        PVector d = x.cross(y);
        d.normalize();

        pushMatrix();
        translate(width / 2, height / 2, -500);

        rotateX(angleX);
        rotateY(angleY);

        fill(color(255, 0, 0));
        stroke(color(255, 255, 255));
        box(200);
        popMatrix();

        textSize(32);
        fill(color(255, 255, 255));
        text("Hi there.", 10, 10);
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
