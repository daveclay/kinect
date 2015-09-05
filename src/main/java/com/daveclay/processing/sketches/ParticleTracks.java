package com.daveclay.processing.sketches;


import com.daveclay.processing.api.Noise2D;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import peasy.PeasyCam;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.Map;

public class ParticleTracks extends PApplet {

    public static void main(String[] args) {
        SketchRunner.run(new ParticleTracks());
    }

    PeasyCam cam;
    ImgProc imgProc;
    public Noise2D xControlPointNoise = new Noise2D(this, .01f);
    public Noise2D yControlPointNoise = new Noise2D(this, .01f);
    public Noise2D zControlPointNoise = new Noise2D(this, .01f);
    public NoiseColor noiseColor = new NoiseColor(this, .000001f);
    Map<Point, Integer> zPoints = new HashMap<>();
    int gridSize = 20;
    int size = 1000;

    public void setup() {
        size(800, 800, P3D);
        cam = new PeasyCam(this, 200);
        cam.setFreeRotationMode();
        zControlPointNoise.setScale(200);
        smooth();
        background(0);
        imgProc = new ImgProc(this);
        xControlPointNoise.next();
        yControlPointNoise.next();
    }

    public void draw() {
        beginShape(LINES);
        for (int x = 0; x < size; x += gridSize) {
            for (int y = 0; y < size; y += gridSize) {
                stroke(220, 220, 220, 10);
                // x = (int) (xControlPointNoise.next() * x);
                y = (int) (yControlPointNoise.next() * y);
                zvertex(x, y);
                zvertex(x, y + gridSize);
                zvertex(x + gridSize, y + gridSize);
                zvertex(x + gridSize, y);
            }
        }
        endShape();
        imgProc.simpleBlur();
    }

    void zvertex(int x, int y) {
        vertex(x, y, z(x, y));
    }

    int z(int x, int y) {
        Point p = point(x, y);
        if (zPoints.containsKey(p)) {
            return zPoints.get(p);
        } else {
            int z = (int) zControlPointNoise.next();
            zPoints.put(p, z);
            return z;
        }
    }

    public static Point point(int x, int y) {
        return new Point(x, y);
    }

    static class Point {

        public final int x;
        public final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}
