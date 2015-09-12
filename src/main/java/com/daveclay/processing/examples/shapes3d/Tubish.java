package com.daveclay.processing.examples.shapes3d;


import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PVector;
import shapes3d.utils.*;
import shapes3d.*;

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
public class Tubish extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new Tubish());
    }

    Tube tube;
    P_Bezier3D bez;
    BezTube btube;
    Extrusion extrude;

    Path path;
    Contour contour;
    ContourScale conScale;

    float angleX, angleY, angleZ;

    int numberOfPoints = 80;
    List<PVector> points = new ArrayList<>();
    public Noise2D xControlPointNoise = new Noise2D(this, .01f);
    public Noise2D yControlPointNoise = new Noise2D(this, .01f);
    public Noise2D zControlPointNoise = new Noise2D(this, .01f);
    public float xIncrement = 10;
    public float yIncrement = 10;
    public float zIncrement = -2;

    public void setup() {
        size(800, 800, P3D);

        path = new P_LinearPath(new PVector(0, 140, 0), new PVector(0, 0, 0));
        contour = getBuildingContour();
        conScale = new CS_ConstantScale();
        contour.make_u_Coordinates();
        extrude = new Extrusion(this, path, 1, contour, conScale);
        extrude.moveTo(0, -40, 0);
        extrude.setTexture("wall.png", 1, 1);
        extrude.drawMode(S3D.TEXTURE );
        extrude.setTexture("tartan.jpg", S3D.BOTH_CAP);
        extrude.drawMode(S3D.TEXTURE, S3D.BOTH_CAP);

        points.add(new PVector(xControlPoint(), yControlPoint(), zControlPoint()));
        points.add(new PVector(xControlPoint(), yControlPoint(), zControlPoint()));
        points.add(new PVector(xControlPoint(), yControlPoint(), zControlPoint()));
        points.add(new PVector(xControlPoint(), yControlPoint(), zControlPoint()));

        makeTube();
    }

    void makeTube() {
        makeBezierForTube();
        int color = color(130, 136, 141);
        btube = new BezTube(this, bez, 22, 10, 700);
        btube.drawMode(S3D.SOLID);
        btube.fill(color, S3D.BOTH_CAP);
        btube.fill(color);
    }

    public void draw() {
        background(30);
        makeTube();
        pushMatrix();
        //rotate();
        directionalLight(180, 255, 200, 0, -1, 0);
        smooth();
        btube.draw();
        popMatrix();
    }

    void rotate() {
        camera(width / 2, height / 2, 1500, 0, 0, 0, 0, 1, 0);
        angleX += radians(.013f);
        angleY += radians(.099f);
        angleZ += radians(.06f);
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

    public void makeBezierForTube() {
        points.add(new PVector(xControlPoint(), yControlPoint(), zControlPoint()));
        if (points.size() > 900) {
            points.remove(0);
        }
        PVector[] array = points.toArray(new PVector[points.size()]);
        bez = new P_Bezier3D(array, array.length);
    }

    public Contour getBuildingContour() {
        PVector[] c = new PVector[] {
                new PVector(-30, 30),
                new PVector(30, 30),
                new PVector(50, 10),
                new PVector(10, -30),
                new PVector(-10, -30),
                new PVector(-50, 10)
        };
        return new Building(c);
    }

    /**
     * Very basic class to represent a building
     * contour for the extrude shape
     *
     */
    public class Building extends Contour {

        public Building(PVector[] c) {
            this.contour = c;
        }
    }
}
