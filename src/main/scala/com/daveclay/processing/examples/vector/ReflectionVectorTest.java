package com.daveclay.processing.examples.vector;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PVector;

public class ReflectionVectorTest extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new ReflectionVectorTest());
    }

    // Position of left hand side of floor
    PVector base1 = new PVector();
    // Position of right hand side of floor
    PVector base2 = new PVector();
    // Length of floor
    float baseLength;

    // An array of subpoints along the floor path
    PVector[] coords;

    // Variables related to moving ball
    PVector reflected = new PVector();
    PVector vector;

    public void setup() {
        size(640, 480);
        randomVector();

        base1.set(0, height, 0);
        base2.set(width, height, 0);
    }

    public void draw() {
        background(100);
        translate(width / 2, 0);

        stroke(0, 255, 0);
        drawVectorLocation(vector);

        PVector normal = new PVector(1, 0, 0);
        PVector v = vector.get();
        normal.mult(2f * v.dot(normal));
        v.sub(normal);

        stroke(255, 0, 0);
        drawVectorLocation(v);
    }

    void drawVectorLocation(PVector vector) {
        line(0, 0, vector.x, vector.y);
    }

    @Override
    public void keyPressed() {
        randomVector();
    }

    void randomVector() {
        vector = PVector.random2D();
        vector.mult(200f);
        vector.x = Math.abs(vector.x);
        vector.y = Math.abs(vector.y);
    }
}
