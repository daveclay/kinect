package com.daveclay.processing.examples.vector;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class PMatrix3DTest extends PApplet {

    public static void main(String[] args) {
        PApplet.main(PMatrix3DTest.class.getName());
    }

    private static final boolean moveCamera = true;

    private PMatrix3D orbitMatrix = new PMatrix3D();
    private PMatrix3D rotationMatrix = new PMatrix3D();
    private PVector center;

    private PVector x = new PVector();
    private PVector y = new PVector();
    private PVector z = new PVector();

    private float angleX;
    private float angleY;
    private float angleZ;
    private float translateX;

    public void setup() {
        size(800, 800, P3D); //OPENGL);
        center = new PVector(width / 2, height / 2, -800);
    }

    public void draw() {
        background(80);
        lights();

        calculateMatrixStuff();

        drawOrbitingCube();
        moveUniverseAndDraw();

        textSize(11);
        fill(color(255, 255, 255));
        text("x: " + x + "\ny: " + y + "\nz: " + z, 30, 30);
    }

    private void drawOrbitingCube() {
        pushMatrix();
        translateToCenter();
        orbitMatrix.set(rotationMatrix);
        orbitMatrix.translate(300, 0, 0);
        applyMatrix(orbitMatrix);
        drawOrbitBox();


        popMatrix();
    }

    private void moveUniverseAndDraw() {
        pushMatrix();
        translateToCenter();
        applyMatrix(rotationMatrix);
        drawElements();
        popMatrix();
    }

    private void translateToCenter() {
        translate(center.x, center.y, center.z);
    }

    private void drawElements() {
        drawCenterBox();
        drawXAxis();
        drawZAxis();
        drawYAxis();
    }

    private void drawOrbitBox() {
        fill(color(0, 0, 180));
        stroke(color(255, 255, 255));
        box(100);
    }

    private void drawCenterBox() {
        fill(color(255, 0, 0));
        stroke(color(255, 255, 255));
        box(200);
    }

    private void calculateMatrixStuff() {
        rotationMatrix.rotateX(angleX);
        rotationMatrix.rotateY(angleY);
        rotationMatrix.rotateZ(angleZ);

        x.set(0, 0, 0);
        rotationMatrix.mult(new PVector(1, 0, 0), x);

        y.set(0, 0, 0);
        rotationMatrix.mult(new PVector(0, 1, 0), y);

        z.set(0, 0, 0);
        rotationMatrix.mult(new PVector(0, 0, 1), z);
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
            case SHIFT:
                translateX++;
                break;
            case 82: // r
                rotationMatrix.reset();
                break;
        }
    }

}
