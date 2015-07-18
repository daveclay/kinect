package com.daveclay.processing.sketches;


import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple program that displays some the shapes
 * and features available in the Shapes3D library.
 *
 * Press the space bar for the next shape.
 * drag mouse to rotate.
 *
 * Also requires PeasyCam library available from
 * http://www.processing.org/reference/libraries/#3d
 *
 * created by Peter Lager
 */
public class BezierGrowthStoredArray extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new BezierGrowthStoredArray());
    }

    float angleX, angleY, angleZ;

    int numPoints = 0;
    private PVector lastAnchorPoint = nextPoint();
    List<PVector> points = new ArrayList<>();
    public Noise2D xControlPointNoise = new Noise2D(this, .01f);
    public Noise2D yControlPointNoise = new Noise2D(this, .01f);
    public Noise2D zControlPointNoise = new Noise2D(this, .01f);

    public void setup() {
        size(800, 800, P2D);
        background(30);
    }

    void makeTube() {
        int index = 1;
        //stroke(130, 136, 141);
        stroke(255);
        strokeWeight(4);
        fill(90, 90, 0);
        while (index < numPoints) {
            PVector control1 = nextPoint();
            PVector control2 = nextPoint();
            PVector endAnchor = nextPoint();

            //bezier(anchor.x, anchor.y, anchor.z, control1.x, control1.y, control1.z, control2.x, control2.y, control2.z, endAnchor.x, endAnchor.y, endAnchor.z);
            // curve: start and end points must match between each segment.
            // 3d:
            // curve(anchor.x, anchor.y, anchor.z, control1.x, control1.y, control1.z, control2.x, control2.y, control2.z, endAnchor.x, endAnchor.y, endAnchor.z);

            curve(lastAnchorPoint.x, lastAnchorPoint.y, control1.x, control1.y, control2.x, control2.y, endAnchor.x, endAnchor.y);
            index += 2;
            lastAnchorPoint = endAnchor;
        }
        numPoints = index + 1;
    }

    public void draw() {
        makeTube();
    }

    void rotate() {
        camera(width / 2, height / 2, 1000, 0, 0, 0, 0, 1, 0);
        angleX += radians(.13f);
        angleY += radians(.99f);
        angleZ += radians(.6f);
        rotateX(angleX);
        rotateY(angleY);
        rotateZ(angleZ);
    }

    public void keyPressed() {
        makeTube();
    }

    float xControlPoint() {
        return width - xControlPointNoise.next() * width;
    }

    float yControlPoint() {
        return height - yControlPointNoise.next() * height;
    }

    float zControlPoint() {
        return -1000 + (zControlPointNoise.next() * 1000);
    }

    public PVector nextStoredPoint(int index) {
        if (index >= points.size()) {
            points.add(nextPoint());
        }
        return points.get(index);
    }

    public PVector nextPoint() {
        return new PVector(xControlPoint(), yControlPoint(), zControlPoint());
    }
}
