package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PVector;
import glitchP5.*; // import GlitchP5

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContinuousCurveYarnBall extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new ContinuousCurveYarnBall());
    }

    GlitchP5 glitchP5;
    float rotation;
    List<PVector> points = new ArrayList<>();
    public Noise2D xControlPointNoise = new Noise2D(this, .01f);
    public Noise2D yControlPointNoise = new Noise2D(this, .01f);
    public Noise2D zControlPointNoise = new Noise2D(this, .01f);
    private PVector lastAnchorPoint;

    public void setup() {
        size(800, 800, P3D);
        background(30);
        glitchP5 = new GlitchP5(this);
        xControlPointNoise.setScale(1.9f);
        yControlPointNoise.setScale(1.9f);
        zControlPointNoise.setScale(1.3f);
        lastAnchorPoint = nextPoint();
    }

    void makeTube() {
        float hue = zControlPointNoise.next();
        stroke(Color.HSBtoRGB(hue, random(.5f), 1f));
        strokeWeight(1);
        PVector endAnchor = nextPoint();

        //bezier(anchor.x, anchor.y, anchor.z, control1.x, control1.y, control1.z, control2.x, control2.y, control2.z, endAnchor.x, endAnchor.y, endAnchor.z);
        // curve: start and end points must match between each segment.
        // 2d:
        //curve(lastAnchorPoint.x, lastAnchorPoint.y, lastAnchorPoint.x, lastAnchorPoint.y, endAnchor.x, endAnchor.y, endAnchor.x, endAnchor.y);
        // 3d:
        curve(lastAnchorPoint.x,
                lastAnchorPoint.y,
                lastAnchorPoint.z,
                lastAnchorPoint.x,
                lastAnchorPoint.y,
                lastAnchorPoint.z,
                endAnchor.x,
                endAnchor.y,
                endAnchor.z,
                endAnchor.x,
                endAnchor.y,
                endAnchor.z);

        lastAnchorPoint = endAnchor;
    }

    public void draw() {
        makeTube();
        glitchP5.run();
    }

    void rotate() {
        float orbitRadius= mouseX/2+50;
        float ypos= mouseY/3;
        float xpos= cos(radians(rotation))*orbitRadius;
        float zpos= sin(radians(rotation))*orbitRadius;

        camera(xpos, ypos, zpos, 1000, 0, 0, 0, -1, 0);
    }

    float xControlPoint() {
        return xControlPointNoise.next() * width - (width / 2);
    }

    float yControlPoint() {
        return yControlPointNoise.next() * height - (height / 2);
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
