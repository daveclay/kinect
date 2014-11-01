package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.kinect.api.FrameExporter;
import com.daveclay.processing.kinect.api.HandGestureHandler;
import com.daveclay.processing.kinect.api.HandGestures;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandGestureCameraMotion extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandGestureCameraMotion.class.getName());
    }

    private SimpleOpenNI kinect;
    private FrameExporter frameExporter;
    private HandGestures handGestures;

    private double camDistance;
    private PeasyCam camera;

    private Random random = new Random(System.currentTimeMillis());
    private List<Star> stars = new ArrayList<Star>();

    private int numberOfStars = 100;
    private int xyRange = 10000;
    private int depthRange = 10000;
    private int minStarRadius = 10;
    private int maxStarRadius = 100;

    private class Star {
        public float x;
        public float y;
        public float z;
        public float radius;
    }

    public void setup() {
        frameExporter = new FrameExporter(this, "/Users/daveclay/Desktop/out/ball%s.tif");

        size(1024, 768, OPENGL);
        //initKinect();

        camera = new PeasyCam(this, 0);
        camera.setMinimumDistance(3);
        // camera.setMaximumDistance(10000);
        camera.setActive(true);

        // fucking depth
        // This absolutely FUCKS the camera's HUD, fucking piece of shit.
        float fov = PI/3;
        float cameraZ = (height/2.0f) / tan(fov/2.0f);
        float aspect = width  / height;
        perspective(fov, aspect, cameraZ/10.0f, cameraZ*100.0f);

        rebuildStarField();

        translateToTheMiddleOfTheStarField();
    }

    private void initKinect() {
        kinect = new SimpleOpenNI(this);
        kinect.setMirror(true);

        kinect.enableRGB();

        handGestures = HandGestureHandler.init(kinect);
        handGestures.useWaveGesture();
    }

    private void rebuildStarField() {
        System.out.println("Rebuilding Star Field... ");
        stars.clear();
        for (int i = 0; i < numberOfStars; i++) {
            buildStar(i);
        }
        System.out.println("Done rebuilding Star Field");
    }

    private void translateToTheMiddleOfTheStarField() {
        // translate(0, 0, -1 * depthRange / 2);
        camera.lookAt(0, 0, -1 * depthRange / 2);
    }

    private void buildStar(int i) {
        Star star = new Star();
        star.x = (xyRange / 2) - random.nextInt(xyRange);
        star.y = (xyRange / 2) - random.nextInt(xyRange);
        star.z = -1 * random.nextInt(depthRange);
        star.radius = random.nextInt(maxStarRadius - minStarRadius) + minStarRadius;
        stars.add(star);
    }

    public void draw() {
        background(50);
        // kinect.update();
        // camera.setDistance(camDistance);

        drawStars();
        drawHUD();

        // updateHandPositions();
        // frameExporter.writeFrame();
    }

    private void updateHandPositions() {
        for (PVector hand : handGestures.getAllCurrentHandPositions()) {
            float x = hand.x;
            float y = hand.y;
            float z = hand.z;

            // camera.lookAt(x, y, z);
        }
    }

    private void drawHUD() {
        camera.beginHUD();
        float[] lookAtPoint = camera.getRotations();
        String s = "x: " + lookAtPoint[0] + "\ny: " + lookAtPoint[1] + "\nz: " + lookAtPoint[2];
        fill(color(255, 255, 255));
        // textSize(32);
        text(s, 10, 10, 70, 80);
        camera.endHUD();
    }

    private void drawStars() {
        for (Star star : stars) {
            drawStar(star);
        }
    }

    private void drawStar(Star star) {
        pushMatrix();
        noStroke();
        smooth();
        translate(star.x, star.y, star.z);
        fill(color(255, 255, 30));
        lights();
        sphere(star.radius);
        popMatrix();
    }

    public void keyPressed() {
        System.out.println("keyCode: " + keyCode);
        switch(keyCode)
        {
            case LEFT:
                camDistance += 10;
                System.out.println(camDistance);
                break;
            case RIGHT:
                camDistance -= 10;
                System.out.println(camDistance);
                break;
            case UP:
                break;
            case DOWN:
                break;
            case 82: // r
                rebuildStarField();
                break;
        }
    }
}