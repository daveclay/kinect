package com.daveclay.processing.kinect.examples;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import SimpleOpenNI.SimpleOpenNI;
import processing.core.PVector;

public class DrumTriggers extends PApplet {

    public static void main(String[] args) {
        PApplet.main(DrumTriggers.class.getName());
    }

    SimpleOpenNI kinect;

    float rotation = 0;

    // two AudioPlayer objects this time
    Minim minim;
    AudioPlayer kick;
    AudioPlayer snare;

    // declare our two hotpoint objects
    Hotpoint snareTrigger;
    Hotpoint kickTrigger;

    float pointCloudScale = 1;

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableRGB();
        kinect.enableDepth();

        size(kinect.rgbWidth(), kinect.rgbHeight(), OPENGL);
        // size(1024, 768, OPENGL);

        minim = new Minim(this);
        // load both audio files
        snare = minim.loadFile("hat.wav");
        kick = minim.loadFile("kick.wav");

        // initialize hotpoints with their origins (x,y,z) and their size
        snareTrigger = new Hotpoint(200, 0, 600, 150);
        kickTrigger = new Hotpoint(-200, 0, 600, 150);

    }

    public void draw() {
        kinect.update();

        background(kinect.rgbImage());

        translate(width/2, height/2, -1000);
        rotateX(radians(180));

        translate(0, 0, 1400);
        // rotateY(radians(map(mouseX, 0, width, -180, 180)));

        translate(0, 0, pointCloudScale *-1000);
        scale(pointCloudScale);


        stroke(255);

        PVector[] depthPoints = kinect.depthMapRealWorld();

        for (int i = 0; i < depthPoints.length; i+=10) {
            PVector currentPoint = depthPoints[i];

            // have each hotpoint check to see
            // if it includes the currentPoint
            snareTrigger.check(currentPoint);
            kickTrigger.check(currentPoint);

            point(currentPoint.x, currentPoint.y, currentPoint.z);
        }

        pushMatrix();
        rotateX(radians(180));
        translate(0, 0, -500);
        textSize(13);
        fill(200, 140, 0);
        text("snare points: " + snareTrigger.pointsIncluded, 20, 20);
        popMatrix();

        if(snareTrigger.isHit()) {
            snare.play();
        }

        if(!snare.isPlaying()) {
            snare.rewind();
            snare.pause();
        }

        if (kickTrigger.isHit()) {
            kick.play();
        }

        if(!kick.isPlaying()) {
            kick.rewind();
            kick.pause();
        }

        // display each hotpoint
        // and clear its points
        snareTrigger.draw();
        snareTrigger.clear();

        kickTrigger.draw();
        kickTrigger.clear();
    }

    public void stop()
    {
        // make sure to close
        // both AudioPlayer objects
        kick.close();
        snare.close();

        minim.stop();
        super.stop();
    }

    public void keyPressed() {
        if (keyCode == 38) {
            pointCloudScale = pointCloudScale + 0.1f;
        }
        if (keyCode == 40) {
            pointCloudScale = pointCloudScale - 0.1f;
        }
    }

    class Hotpoint {
        PVector center;
        int fillColor;
        int strokeColor;
        int size;
        int pointsIncluded;
        int maxPoints;
        boolean wasJustHit;
        int threshold;


        Hotpoint(float centerX, float centerY, float centerZ, int boxSize) {
            center = new PVector(centerX, centerY, centerZ);
            size = boxSize;
            pointsIncluded = 0;
            maxPoints = 1000;
            threshold = 0;

            fillColor = strokeColor = color(random(255), random(255), random(255));
        }

        void setThreshold( int newThreshold ){
            threshold = newThreshold;
        }

        void setMaxPoints(int newMaxPoints) {
            maxPoints = newMaxPoints;
        }

        void setColor(float red, float blue, float green){
            fillColor = strokeColor = color(red, blue, green);
        }

        boolean check(PVector point) {
            boolean result = false;

            if (point.x > center.x - size/2 && point.x < center.x + size/2) {
                if (point.y > center.y - size/2 && point.y < center.y + size/2) {
                    if (point.z > center.z - size/2 && point.z < center.z + size/2) {
                        result = true;
                        pointsIncluded++;
                    }
                }
            }

            return result;
        }

        void draw() {
            pushMatrix();
            translate(center.x, center.y, center.z);

            fill(red(fillColor), blue(fillColor), green(fillColor), 255 * percentIncluded());
            stroke(red(strokeColor), blue(strokeColor), green(strokeColor), 255);
            box(size);
            popMatrix();
        }

        float percentIncluded() {
            return map(pointsIncluded, 0, maxPoints, 0, 1);
        }


        boolean currentlyHit() {
            return (pointsIncluded > threshold);
        }


        boolean isHit() {
            return currentlyHit() && !wasJustHit;
        }

        void clear() {
            wasJustHit = currentlyHit();
            pointsIncluded = 0;
        }
    }
}
