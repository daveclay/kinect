package com.daveclay.processing.examples.camera;

import processing.core.*;

public class SimpleCamera extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SimpleCamera.class.getName());
    }

    int selectedParam = 0;

    float eyeX;
    float eyeY;
    float eyeZ;
    float centerX;
    float centerY;
    float centerZ;
    float upX;
    float upY;
    float upZ;

    public void setup() {
        size(800, 800, P3D);

        resetCamera();
    }

    private void resetCamera() {
        eyeX = width / 2;
        eyeY = height / 2;
        eyeZ = (height / 2) / tan(PI/6);

        centerX = width / 2;
        centerY = height / 2;
        centerZ = 0;

        upX = 0;
        upY = 1;
        upZ = 0;
    }

    public void draw() {
        background(80);
        lights();

        pushMatrix();
        translate(width / 2, height / 2, -100);
        fill(color(255, 0, 0));
        sphere(200);
        popMatrix();

        camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void logCamera() {
        System.out.println("eyeX: " + eyeX + " eyeY: " + eyeY + " eyeZ: " + eyeZ + " centerX: " + centerX + " centerY: " + centerY + " centerZ: " + centerZ + " upX: " + upX + " upY: " + upY + " upZ: " + upZ);
    }

    public void keyPressed() {
        System.out.println("keyCode: " + keyCode);
        switch(keyCode)
        {
            case LEFT:
                adjustParam(-10);
                break;
            case RIGHT:
                adjustParam(10);
                break;
            case UP:
                adjustParam(10);
                break;
            case DOWN:
                adjustParam(-10);
                break;
            case 82: // r
                resetCamera();
                logCamera();
                break;
            case 47:
                selectParam();
                break;
        }
    }

    private void selectParam() {
        selectedParam++;
        if (selectedParam > 8) {
            selectedParam = 0;
        }
        switch (selectedParam) {
            case 0:
                System.out.println("eyeX");
                break;
            case 1:
                System.out.println("eyeY");
                break;
            case 2:
                System.out.println("eyeZ");
                break;
            case 3:
                System.out.println("centerX");
                break;
            case 4:
                System.out.println("centerY");
                break;
            case 5:
                System.out.println("centerZ");
                break;
            case 6:
                System.out.println("upX");
                break;
            case 7:
                System.out.println("upY");
                break;
            case 8:
                System.out.println("upZ");
                break;
        }
    }

    private void adjustParam(int amount) {
        switch (selectedParam) {
            case 0:
                eyeX += amount;
                break;
            case 1:
                eyeY += amount;
                break;
            case 2:
                eyeZ += amount;
                break;
            case 3:
                centerX += amount;
                break;
            case 4:
                centerY += amount;
                break;
            case 5:
                centerZ += amount;
                break;
            case 6:
                upX += amount * .1;
                break;
            case 7:
                upY += amount * .1;
                break;
            case 8:
                upZ += amount * .1;
                break;
        }
        logCamera();
    }
}
