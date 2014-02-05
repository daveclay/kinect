package com.daveclay.processing.gestures;

import com.daveclay.processing.api.SketchRunner;
import com.sun.org.apache.regexp.internal.recompile;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class GestureTemplatesSketch extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new GestureTemplatesSketch());
    }

    GeometricRecognizer recognizer = new GeometricRecognizer();
    private List<GestureTemplate> normalizedTemplates;
    private int gestureIndex = 0;

    @Override
    public void setup() {
        super.setup();
        size(1024, 768);
        background(200);
        recognizer.loadDefaultGestures();
        normalizedTemplates = recognizer.getNormalizedTemplates();
    }

    @Override
    public void draw() {
        GestureTemplate gestureTemplate = normalizedTemplates.get(gestureIndex);

        fill(80);
        textSize(20);
        text(gestureTemplate.name, 10, 20);
        translate(width / 2, height / 2);
        for (Point2D point : gestureTemplate.points) {
            ellipse((float) point.x, (float) point.y, 5, 5);
        }
    }

    @Override
    public void keyPressed() {
        switch (keyCode) {
            case LEFT:
                background(200);
                if (gestureIndex == 0) {
                    gestureIndex = normalizedTemplates.size() - 1;
                } else {
                    gestureIndex--;
                }
                break;
            case RIGHT:
                background(200);
                if (gestureIndex == normalizedTemplates.size() - 1) {
                    gestureIndex = 0;
                } else {
                    gestureIndex++;
                }
                break;
        }

    }
}
