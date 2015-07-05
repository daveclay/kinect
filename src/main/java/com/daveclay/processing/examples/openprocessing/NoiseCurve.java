package com.daveclay.processing.examples.openprocessing;

import com.daveclay.processing.api.NoiseColor;
import processing.core.PApplet;
import processing.core.PVector;

public class NoiseCurve extends PApplet {

    public static void main(String[] args) {
        PApplet.main(NoiseCurve.class.getName());
    }

    float max = 0;

    int NumParticle = 400;
    PVector[] spot = new PVector[NumParticle];
    float[] t = new float[NumParticle];
    float[] s = new float[NumParticle];
    float[] radius = new float[NumParticle];
    float motionNoise = random(10);
    NoiseColor noiseColor = new NoiseColor(this, .000003f);
    NoiseColor spotNoiseColor = new NoiseColor(this, .000002f);

    public void setup() {
        size(1024, 1024, OPENGL);
        background(0);
        for (int i = 0; i < NumParticle; i++) {
            t[i] = random(-1, 1);
            s[i] = random(2 * PI);
            radius[i] = 160 + 30 * noise(sqrt(1 - t[i] * t[i]) * cos(s[i]), t[i], motionNoise);
            spot[i] = new PVector(
                    radius[i] * sqrt(1 - t[i] * t[i]) * cos(s[i]),
                    radius[i] * sqrt(1 - t[i] * t[i]) * sin(s[i]),
                    radius[i] * t[i]);
        }
    }

    public void draw() {
        background(0);
        Update();
        DrawMe();
    }

    public void Update() {
        motionNoise += 0.01;
        for (int i = 0; i < NumParticle; i++) {
            spot[i].normalize();
            radius[i] = (getWidth() / 2) * noise(sqrt(1 - t[i] * t[i]) * cos(s[i]), t[i], motionNoise);
            spot[i].mult(radius[i]);
        }
    }

    public int noiseColor(int alpha) {
        return noiseColor.nextColor(alpha);
    }

    public void DrawMe() {
        translate(width / 2, height / 2);

        for (int i = 0; i < NumParticle - 1; i++) {
            strokeWeight(2);
            stroke(noiseColor(50));
            line(spot[i].x, spot[i].y, spot[i + 1].x, spot[i + 1].y); // , spot[i + 1].z);
        }
        stroke(noiseColor(50));
        line(spot[0].x, spot[0].y, spot[NumParticle - 1].x, spot[NumParticle - 1].y); //, spot[NumParticle - 1].z);

        for (int i = 0; i < NumParticle - 1; i++) {
            drawSpot(i, i + 1);
        }
        drawSpot(NumParticle - 1, 0);
    }

    void drawSpot(int indexA, int indexB) {
        float dist = distSqrd(
                spot[indexA].x,
                spot[indexA].y,
                spot[indexA].z,
                spot[indexB].x,
                spot[indexB].y,
                spot[indexB].z);
        pushMatrix();
        translate(spot[indexA].x, spot[indexA].y); // , spot[indexA].z);
        if (dist > max) {
            //System.out.println(dist);
            max = dist;
        }
        int alpha = (int) map(dist, 0, 300493, 0, 255);
        alpha = max(0, min(255, alpha));
        fill(spotNoiseColor.nextColor(alpha));
        //strokeWeight((int) (dist * .0001));
        int radius = (int) (dist * .0001);
        ellipse(0, 0, radius, radius);
        popMatrix();
    }

    float distSqrd(float x1, float y1, float z1, float x2, float y2, float z2) {
        return sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1);
    }
}
