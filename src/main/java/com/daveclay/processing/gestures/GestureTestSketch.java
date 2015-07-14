package com.daveclay.processing.gestures;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class GestureTestSketch extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new GestureTestSketch());
    }

    private GeometricRecognizer geometricRecognizer = new GeometricRecognizer();
    private boolean drawing = false;
    private List<PVector> points = new ArrayList<PVector>();

    @Override
    public void setup() {
        geometricRecognizer.loadDefaultGestures();
        size(1024, 768);
    }

    @Override
    public void draw() {
        fill(255, 0, 0);
        if (drawing) {
            points.add(new PVector(mouseX, mouseY));
            ellipse(mouseX, mouseY, 10, 10);
        }
    }

    @Override
    public void mousePressed() {
        drawing = true;
    }

    @Override
    public void mouseReleased() {
        drawing = false;
        RecognitionResult result = geometricRecognizer.recognize(points);
        System.out.println(result.name + " " + result.score);
        points.clear();
    }
}
