package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Field;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.Vehicle;
import com.daveclay.processing.api.image.ImgProc;
import processing.core.PApplet;
import processing.core.PVector;

public class VehicleBezier extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new VehicleBezier());
    }

    ImgProc imgProc;
    Field field;
    Vehicle controlPoint1;
    Vehicle controlPoint2;
    Vehicle anchorPoint1;
    Vehicle anchorPoint2;
    Vehicle target1;
    Vehicle target2;

    float noiseR = random(7536451);
    float noiseG = random(16432);
    float noiseB = random(64311);

    public void setup() {
        size(1024, 768);
        background(0);
        imgProc = new ImgProc(this);

        field = new Field(50, this);
        controlPoint1 = new Vehicle(this, 100, 100);
        controlPoint2 = new Vehicle(this, 500, 300);

        controlPoint1.maxspeed = controlPoint2.maxspeed = 6;
        controlPoint1.maxforce = controlPoint2.maxforce = .5f;

        anchorPoint1 = new Vehicle(this, 0, height/2);
        anchorPoint2 = new Vehicle(this, width, height/2);
        target1 = new Vehicle(this, random(0, width), random(0, height));
        target2 = new Vehicle(this, random(0, width), random(0, height));
    }

    public void draw() {
        //background(0);
        smooth();
        PVector mouse = new PVector(mouseX, mouseY);
        target1.seek(mouse);
        target2.seek(mouse);

        controlPoint1.flow(field.lookup(controlPoint1.location));
        controlPoint2.flow(field.lookup(controlPoint2.location));

        controlPoint1.update();
        controlPoint2.update();
        anchorPoint1.update();
        anchorPoint2.update();
        target1.update();
        target2.update();

        noFill();

        stroke(color(
                noise(noiseR += .01) * 255,
                noise(noiseG += .01) * 255,
                noise(noiseB += .01) * 255,
                255
                ));
        beginShape();
        vertex(anchorPoint1.location.x, anchorPoint1.location.y);
        bezierVertex(
                controlPoint1.location.x,
                controlPoint1.location.y,
                controlPoint2.location.x,
                controlPoint2.location.y,
                anchorPoint2.location.x,
                anchorPoint2.location.y
        );
        endShape();

        //drawControls();
        // field.draw();

        imgProc.simpleBlur();
        field.initialize();
    }

    void drawTargets(Vehicle controlPoint) {

    }

    public void drawControls() {
        drawControlPoint(controlPoint1, color(255, 0, 0));
        drawControlPoint(controlPoint2, color(0, 255, 0));
    }

    void drawControlPoint(Vehicle controlPoint, int color) {
        stroke(color);
        rect(
                controlPoint.location.x,
                controlPoint.location.y,
                3,
                3);

    }

    PVector randomTarget(float offset) {
        return new PVector(random(0, width - offset) + offset, random(0, height - offset) + offset);
    }
}
