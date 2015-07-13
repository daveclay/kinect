package com.daveclay.processing.examples.geomerative;

import com.daveclay.processing.api.SketchRunner;
import geomerative.RMesh;
import geomerative.RPolygon;
import processing.core.PApplet;

public class GeomerativeIntersectionExample extends PApplet {
    public static void main(String[] args) {
        SketchRunner.run(new GeomerativeIntersectionExample());
    }

    RMesh m;

    float t=0;

    public void setup(){
        size(400, 400, P3D);
        //smooth();
        noStroke();
        fill(0);
    }

    public void draw(){
        RPolygon p = RPolygon.createCircle(120, 70, 6);
        RPolygon p2 = RPolygon.createStar(60,50,30);
        p=p.diff(p2);

        m = p.toMesh();
        background(255);
        translate(width/2,height/2);

        rotateX(t/39);
        m.draw(g);

        rotateY(-t/5);
        scale(0.3f);
        m.draw(g);

        t++;
    }
}
