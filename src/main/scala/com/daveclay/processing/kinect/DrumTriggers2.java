package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import ddf.minim.AudioPlayer;
import ddf.minim.Sound;
import processing.core.PApplet;
import processing.core.PVector;

public class DrumTriggers2 extends PApplet {

    public static void main(String[] args) {
        PApplet.main(DrumTriggers2.class.getName());
    }

    SimpleOpenNI kinect;
    Sound minim;

    HotpointAudio kick;
    HotpointAudio snare;

    float pointCloudScale = 1;

    public void setup() {
        kinect = new SimpleOpenNI(this);
        kinect.enableRGB();
        kinect.enableDepth();

        size(kinect.rgbWidth(), kinect.rgbHeight(), OPENGL);
        // size(1024, 768, OPENGL);

        minim = new Sound();
        // load both audio files
        AudioPlayer snareAudio = minim.loadFile("hat.wav");
        AudioPlayer kickAudio = minim.loadFile("kick.wav");

        // initialize hotpoints with their origins (x,y,z) and their size
        Hotpoint snareTrigger = new Hotpoint(200, 0, 600, 150, color(230, 230, 0));
        Hotpoint kickTrigger = new Hotpoint(-200, 0, 600, 150, color(130, 0, 230));

        kick = new HotpointAudio(kickTrigger, kickAudio);
        snare = new HotpointAudio(snareTrigger, snareAudio);
    }

    public void draw() {
        kinect.update();
        background(kinect.rgbImage());

        pushMatrix();
        renderPointCloudScene();
        popMatrix();

        pushMatrix();
        textSize(23);
        fill(200, 0, 200);
        text("snare points: " + snare.getHotpoint().pointsIncluded, 20, 20);
        popMatrix();

        handleTriggers();
    }

    private void handleTriggers() {
        snare.handleTriggers();
        kick.handleTriggers();
    }

    private void renderPointCloudScene() {
        translate(width/2, height/2, -1000);
        rotateX(radians(180));

        translate(0, 0, 1400); // move the pointcloud scene out a ways
        // rotateY(radians(map(mouseX, 0, width, -180, 180)));

        translate(0, 0, pointCloudScale *-1000);
        scale(pointCloudScale);

        stroke(255);

        PVector[] depthPoints = kinect.depthMapRealWorld();

        for (int i = 0; i < depthPoints.length; i+=10) {
            PVector currentPoint = depthPoints[i];

            // have each hotpoint check to see
            // if it includes the currentPoint
            snare.getHotpoint().check(currentPoint);
            kick.getHotpoint().check(currentPoint);

            point(currentPoint.x, currentPoint.y, currentPoint.z);
        }

        // display each hotpoint
        kick.getHotpoint().draw();
        snare.getHotpoint().draw();
    }

    public void stop() {
        kick.getAudioPlayer().close();
        snare.getAudioPlayer().close();

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

    static class HotpointAudio {
        private Hotpoint hotpoint;
        private AudioPlayer audioPlayer;

        HotpointAudio(Hotpoint hotpoint, AudioPlayer audioPlayer) {
            this.hotpoint = hotpoint;
            this.audioPlayer = audioPlayer;
        }

        public Hotpoint getHotpoint() {
            return hotpoint;
        }

        public AudioPlayer getAudioPlayer() {
            return audioPlayer;
        }

        private void handleTriggers() {
            if(hotpoint.isHit()) {
                audioPlayer.play();
            }

            if(!audioPlayer.isPlaying()) {
                audioPlayer.rewind();
                audioPlayer.pause();
            }
            hotpoint.clear();
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


        Hotpoint(float centerX, float centerY, float centerZ, int boxSize, int color) {
            center = new PVector(centerX, centerY, centerZ);
            size = boxSize;
            pointsIncluded = 0;
            maxPoints = 1000;
            threshold = 0;
            fillColor = strokeColor = color;
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
