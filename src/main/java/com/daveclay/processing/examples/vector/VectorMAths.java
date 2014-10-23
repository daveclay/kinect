package com.daveclay.processing.examples.vector;

import processing.core.PMatrix3D;
import processing.core.PVector;

public class VectorMaths {

    public static void main(String[] args) {

        PMatrix3D cam = new PMatrix3D();
        cam.rotateX(45);
        cam.rotateY(32);
        cam.rotateZ(123);

        cam.translate(20, 0, 0);

        PVector x = new PVector();
        cam.mult(new PVector(1, 0, 0), x);

        PVector y = new PVector();
        cam.mult(new PVector(0, 1, 0), y);

        PVector z = new PVector();
        cam.mult(new PVector(0, 0, 1), z);

        PVector c = x.cross(y);
        PVector norm = c.get();
        norm.normalize();

        assert c.equals(z);
        assert c.equals(norm);

        System.out.println("x: " + x + "\ny: " + y + "\nz: " + z + "\nX x Y: " + c + "\nnorm: " + norm);
    }
}
