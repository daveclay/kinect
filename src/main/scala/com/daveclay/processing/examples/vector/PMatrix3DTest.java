package com.daveclay.processing.examples.vector;

import processing.core.PApplet;
import processing.core.PVector;

public class PMatrix3DTest extends PApplet {

    public static void main(String[] args) {
        PApplet.main(PMatrix3DTest.class.getName());
    }

    processing.core.PMatrix3D matrix;
    private PVector center;

    private PVector x = new PVector();
    private PVector y = new PVector();
    private PVector z = new PVector();
    private PVector d;

    private PVector cameraPoint = new PVector(0, 0, 0);

    private float angleX;
    private float angleY;
    private float angleZ;

    public void setup() {
        size(800, 800, P3D); //OPENGL);
        matrix = new processing.core.PMatrix3D();
        matrix.translate(100f, 0f, 0f);
        center = new PVector(width / 2, height / 2, -500);
    }

    public void draw()
    {
        background(80);
        lights();

        calculateMatrixStuff();
        drawShit();

        camera(x.x, y.y, z.z, center.x, center.y, center.z, 0f, 1f, 0f);
        // camera(0, 0, 100, 0, 0, 0, 0f, 1f, 0f);

        textSize(11);
        fill(color(255, 255, 255));
        text("x: " + x + "\ny: " + y + "\nz: " + z, 30, 30);
    }

    private void drawShit() {
        pushMatrix();

        translate(center.x, center.y, center.z);
        // applyMatrix(matrix);

        drawCenterBox();
        drawXAxis();
        drawZAxis();
        drawYAxis();

        popMatrix();
    }

    private void drawCenterBox() {
        fill(color(255, 0, 0));
        stroke(color(255, 255, 255));
        box(200);
    }

    private void calculateMatrixStuff() {
        matrix.rotateX(angleX);
        matrix.rotateY(angleY);
        matrix.rotateZ(angleZ);

        x.set(0, 0, 0);
        matrix.mult(new PVector(1, 0, 0), x);

        y.set(0, 0, 0);
        matrix.mult(new PVector(0, 1, 0), y);

        z.set(0, 0, 0);
        matrix.mult(new PVector(0, 0, 1), z);

        // vector cross product: results in a vector pointing perpendicular from the plane that the x and y vectors make.
        d = x.cross(y);
        d.normalize();
    }

    private void drawXAxis() {
        pushMatrix();
        translate(-10000, 0, 0);
        noStroke();
        fill(color(0, 255, 0));
        box(30000, 1, 1);
        popMatrix();
    }

    private void drawZAxis() {
        pushMatrix();
        translate(0, 0, -10000);
        noStroke();
        fill(color(255, 0, 0));
        box(1, 1, 30000);
        popMatrix();
    }

    private void drawYAxis() {
        pushMatrix();
        translate(0, -10000, 0);
        noStroke();
        fill(color(0, 0, 255));
        box(1, 30000, 1);
        popMatrix();
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
