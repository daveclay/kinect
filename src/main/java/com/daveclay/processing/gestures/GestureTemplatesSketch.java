package com.daveclay.processing.gestures;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;

import java.util.List;

public class GestureTemplatesSketch extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new GestureTemplatesSketch());
    }

    GeometricRecognizer recognizer = new GeometricRecognizer();
    private List<GestureData> normalizedTemplates;
    private int gestureIndex = 0;

    @Override
    public void setup() {
        super.setup();
        size(1024, 768);
        background(200);
        GestureDataStore gestureDataStore = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        gestureDataStore.load();
        recognizer.addTemplates(gestureDataStore.getAll());
        normalizedTemplates = recognizer.getNormalizedTemplates();
    }

    @Override
    public void draw() {
        GestureData gestureData = normalizedTemplates.get(gestureIndex);

        fill(80);
        stroke(80);
        textSize(20);
        text(gestureData.name, 10, 20);

        stroke(2);
        translate(width / 2, height / 2);
        Point2D previous = null;
        for (Point2D point : gestureData.points) {
            if (previous != null) {
                line(previous.x, previous.y, point.x, point.y);
            }
            previous = point;
            ellipse(point.x, point.y, 2, 2);
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
