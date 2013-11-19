package com.daveclay.processing.examples.vector;

import processing.core.PMatrix3D;
import processing.core.PVector;

public class VectorMaths {

    public static void main(String[] args) {

        PMatrix3D cam = new PMatrix3D();
        cam.rotateX(90);

        PVector x = new PVector();
        cam.mult(new PVector(1, 0, 0), x);

        PVector y = new PVector();
        cam.mult(new PVector(0, 1, 0), y);

        PVector c = x.cross(y);

        System.out.println(x + "\n" + y + "\n" + c + "\n");
    }
}
