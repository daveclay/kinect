package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.FrameExporter;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.*;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class BodySeekers extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();

        BodySeekers bodyLocator = new BodySeekers(logSketch.getHud());

        SketchRunner.run(logSketch, bodyLocator);

        logSketch.frame.setLocation(0, 0);
        bodyLocator.frame.setLocation(0, logSketch.getHeight() - 120);
    }

    private User user;
    private FrameExporter frameExporter;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
    private FloatValueMeasurement zValues = new FloatValueMeasurement();
    private FloatValueMeasurement hueValues = new FloatValueMeasurement();

    public BodySeekers(HUD hud) {
        super();
        setSketchCallback(new SketchCallback() {
            @Override
            public void draw() {
                drawBodyLocator();
            }

            @Override
            public void setup(KinectPV2 kinect) {
                kinect.enableSkeleton(true);
                kinect.enableSkeleton3dMap(true);
                kinect.enableSkeletonColorMap(true);
                background(0);
            }
        });

        this.frameExporter = new FrameExporter(this, "C:/Users/daveclay/Documents/ProcessingMovies/bodyseeker%s.tif");
        this.hud = hud;
        int width = 1920;
        int height = 1080;

        for (int i = 0; i < 220; i++) {
            this.vehicles.add(new Vehicle(width / 2, height / 2, i));
        }

        registerEventListeners();
    }

    protected void registerEventListeners() {
        onUserEntered(new UserEnteredHandler() {
            @Override
            public void userDidEnter(User user) {
                System.out.println("HELLO User " + user.getID() + "!");
                BodySeekers.this.user = user;
                // BodySeek.this.frameExporter.start();
            }
        });

        onUserWasLost(new UserWasLostHandler() {
            @Override
            public void userWasLost(User user) {
                System.out.println("User " + user.getID() + " LOST, eh well...");
                BodySeekers.this.user = null;
                BodySeekers.this.frameExporter.stop();
            }
        });
    }

    private void drawBodyLocator() {
        hud.logRounded("FPS", frameRate);
        updateUserDataAndDrawStuff();
    }

    private void updateUserDataAndDrawStuff() {
        if (user != null) {
            // draw user data.
            drawUserData(user);
        } else {
            drawLostUser();
        }
    }

    private void drawLostUser() {
        noStroke();
        fill(0, 5);
        rect(0, 0, width, height);
    }

    private void drawUserData(User user) {
        noStroke();
        fill(0, 5);
        rect(0, 0, width, height);

        PVector leftHip = user.getJointPosition2D(KinectPV2.JointType_HipLeft);

        PVector leftHandPosition2d = user.getJointPosition2D(KinectPV2.JointType_HandLeft);
        leftHandPosition2d.z = user.getJointPosition3D(KinectPV2.JointType_HandLeft).z;

        PVector rightHandPosition3d = user.getJointPosition3D(KinectPV2.JointType_HandRight);
        PVector rightHandPosition2d = user.getJointPosition2D(KinectPV2.JointType_HandRight);

        float z = rightHandPosition3d.z;
        rightHandPosition2d.z = z;
        zValues.add(z);

        hud.logScreenCoords("Right Hand", rightHandPosition2d);
        hud.logScreenCoords("Left Hand", leftHandPosition2d);
        hud.log("Z", zValues);

        // Draw an ellipse at the mouse location
        /*
        pushMatrix();
        fill(100, 200, 30);
        stroke(0);
        strokeWeight(2);
        ellipse(leftHandPosition2d.x, leftHandPosition2d.y, 20, 20);
        popMatrix();
        */

        // Call the appropriate steering behaviors for our agents
        for (Vehicle vehicle : vehicles) {
            if (vehicle.index % 2 == 0) {
                vehicle.seek(leftHandPosition2d);
            } else if (vehicle.index % 3 == 0) {
                vehicle.seek(rightHandPosition2d);
            } else {
                vehicle.seek(leftHip);
            }
            vehicle.update();
            vehicle.display();
        }

        this.frameExporter.writeFrame();
    }


    class Vehicle {

        int index;
        int r;
        int g;
        int b;
        float alpha;
        PVector previousLocation;
        PVector location;
        PVector velocity;
        PVector acceleration;
        float size;
        float maxforce;    // Maximum steering force
        float maxspeed;    // Maximum speed

        public Vehicle(float x, float y, int index) {
            this.index = index;
            if (index % 2 == 0) {
                this.r = 255;
                this.g = (int) random(255);
                this.b = 0;
            } else if (index % 3 == 0) {
                this.r = (int) random(255);
                this.g = 0;
                this.b = 255;
            } else {
                this.r = 0;
                this.g = (int) random(255);
                this.b = 255;
            }
            acceleration = new PVector(0, 0);
            velocity = new PVector(0, -2);
            location = new PVector(x, y);
            size = 2;
            maxspeed = random(9, 13);
            maxforce = random(.6f, .9f);
        }

        // Method to update location
        public void update() {
            // Update velocity
            velocity.add(acceleration);
            // Limit speed
            velocity.limit(maxspeed);
            location.add(velocity);
            // Reset accelerationelertion to 0 each cycle
            acceleration.mult(0);
        }

        void applyForce(PVector force) {
            // We could add mass here if we want A = F / M
            acceleration.add(force);
        }

        // A method that calculates a steering force towards a target
        // STEER = DESIRED MINUS VELOCITY
        void seek(PVector target) {
            PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target

            float distance = desired.mag();
            if (distance < 100) {
                float speed = map(distance, 0, 100, 0, maxspeed);
                desired.setMag(speed);
            } else {
                desired.setMag(maxspeed);
            }

            desired.mult(maxspeed);

            alpha = map(target.z, zValues.getMin(), zValues.getMax(), 0, 1);

            // Steering = Desired minus velocity
            PVector steer = PVector.sub(desired, velocity);
            steer.limit(maxforce);  // Limit to maximum steering force

            applyForce(steer);
        }

        void display() {
            // Draw a triangle rotated in the direction of velocity
            // float theta = velocity.heading() + PI / 2;
            float hsb[] = new float[3];
            java.awt.Color.RGBtoHSB(r, g, b, hsb);
            float hue = hsb[0] + alpha;
            if (hue > 1f) hue -= 1f;
            int color = java.awt.Color.HSBtoRGB(hue, hsb[1], hsb[2]);

            if (index % 2 == 0) {
                hueValues.add(hue);
                hud.log("Hue", hueValues);
            }

            if (previousLocation != null) {
                stroke(color);
                strokeWeight(size);
                line(previousLocation.x, previousLocation.y, location.x, location.y);
            } else {
                fill(color);
                ellipse(location.x, location.y, size, size);
            }

            previousLocation = location.get();
        }
    }
}


