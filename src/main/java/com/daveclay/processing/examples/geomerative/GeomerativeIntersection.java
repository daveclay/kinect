package com.daveclay.processing.examples.geomerative;

import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import geomerative.*;

public class GeomerativeIntersection extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new GeomerativeIntersection());
    }

    RPolygon frontLeftZone;
    RPolygon frontRightZone;
    RPolygon backLeftZone;
    RPolygon backRightZone;

    public void setup(){
        size(400, 400, P3D);
        //smooth();
        noStroke();
        fill(0);
        frontLeftZone = RPolygon.createRectangle(0, 0, 200, 200);
        frontRightZone = RPolygon.createRectangle(200, 0, 200, 200);
        backLeftZone = RPolygon.createRectangle(0, 200, 200, 200);
        backRightZone = RPolygon.createRectangle(200, 200, 200, 200);

    }

    public void draw(){
        RPolygon centerCircle = RPolygon.createCircle(getWidth() / 2, getHeight() / 2, getWidth() / 4);
        frontLeftZone = frontLeftZone.diff(centerCircle);
        frontRightZone = frontRightZone.diff(centerCircle);
        backLeftZone = backLeftZone.diff(centerCircle);
        backRightZone = backRightZone.diff(centerCircle);

        fill(128);
        stroke(255);
        frontLeftZone.draw(this);
        frontRightZone.draw(this);
        backLeftZone.draw(this);
        backRightZone.draw(this);
        centerCircle.draw(this);

    }
}
