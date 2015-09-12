package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.FrameExporter;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.api.UserEnteredHandler;
import com.daveclay.processing.kinect.api.UserTrackingSketch;
import com.daveclay.processing.kinect.api.UserWasLostHandler;
import com.daveclay.processing.kinect.api.stage.Stage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class BodySkeleton extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();

        BodySkeleton bodySkeleton = new BodySkeleton(logSketch.getHud());
        BodySkeleton bodyLocator = bodySkeleton;

        SketchRunner.run(logSketch, bodyLocator);

        logSketch.frame.setLocation(0, 0);
        bodyLocator.frame.setLocation(0, logSketch.getHeight() - 100);
    }

    private User user;
    private Stage stage;
    private FrameExporter frameExporter;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    public BodySkeleton(HUD hud) {
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
            this.vehicles.add(new Vehicle(random(width), random(height), i));
        }

        registerEventListeners();
    }

    protected void registerEventListeners() {
        onUserEntered(new UserEnteredHandler() {
            @Override
            public void userDidEnter(User user) {
                System.out.println("HELLO User " + user.getID() + "!");
                BodySkeleton.this.user = user;
                // BodySeek.this.frameExporter.start();
            }
        });

        onUserWasLost(new UserWasLostHandler() {
            @Override
            public void userWasLost(User user) {
                System.out.println("User " + user.getID() + " LOST, eh well...");
                BodySkeleton.this.user = null;
                BodySkeleton.this.frameExporter.stop();
            }
        });
    }

    private void drawBodyLocator() {
        hud.logRounded("FPS", frameRate);
        updateUserDataAndDrawStuff();
    }

    private void updateUserDataAndDrawStuff() {
        if (user != null) {
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
        PVector leftHandPosition2d = user.getJointPosition2D(KinectPV2.JointType_HandLeft);
        PVector rightHandPosition2d = user.getJointPosition2D(KinectPV2.JointType_HandRight);

        hud.logScreenCoords("Right Hand", rightHandPosition2d);
        hud.logScreenCoords("Left Hand", leftHandPosition2d);

        drawLines(leftHandPosition2d, rightHandPosition2d);
        this.frameExporter.writeFrame();
    }

    private void drawLines(PVector leftHandPosition2d, PVector rightHandPosition2d) {

    }

    void generateLines(PVector leftHandPosition2d, PVector rightHandPosition2d, PVector d, float mag) {
        float[] coordinatesA = noiseFactor(d, mag);
        float[] coordinatesB = noiseFactor(d, mag);

        /*
        int size = 30;
        ellipse(coordinatesA[0] + leftHandPosition2d.x, coordinatesA[1] + leftHandPosition2d.y, size, size);
        ellipse(coordinatesB[0] + rightHandPosition2d.x, coordinatesB[1] + rightHandPosition2d.y, size, size);
        */

        beginShape();
        vertex(leftHandPosition2d.x, leftHandPosition2d.y);
        bezierVertex(
                coordinatesA[0] + leftHandPosition2d.x,
                coordinatesA[1] + leftHandPosition2d.y,
                coordinatesB[0] + rightHandPosition2d.x,
                coordinatesB[1] + rightHandPosition2d.y,
                rightHandPosition2d.x,
                rightHandPosition2d.y);

        endShape();
    }

    float[] noiseFactor(PVector d, float mag) {
        float value = noise(random(10));
        float angle = value*3.14159265f*2;
        float x = cos(angle) * mag;
        float y = sin(angle) * mag;
        return new float[] { x, y };
    }

    class Vehicle {

        int index;
        int color;
        PVector previousLocation;
        PVector location;
        PVector velocity;
        PVector acceleration;
        float r;
        float maxforce;    // Maximum steering force
        float maxspeed;    // Maximum speed

        public Vehicle(float x, float y, int index) {
            this.index = index;
            if (index % 2 == 0) {
                this.color = color(255, random(255), 0);
            } else if (index % 3 == 0) {
                this.color = color(random(255), 0, 255);
            } else {
                this.color = color(0, random(255), 255);
            }
            acceleration = new PVector(0, 0);
            velocity = new PVector(0, -2);
            location = new PVector(x, y);
            r = 2;
            maxspeed = random(9, 13);
            maxforce = random(.6f, .9f);
        }

        // Method to draw location
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

            // Steering = Desired minus velocity
            PVector steer = PVector.sub(desired, velocity);
            steer.limit(maxforce);  // Limit to maximum steering force

            applyForce(steer);
        }

        void display() {
            // Draw a triangle rotated in the direction of velocity
            // float theta = velocity.heading() + PI / 2;
            if (previousLocation != null) {
                stroke(color);
                strokeWeight(r);
                line(previousLocation.x, previousLocation.y, location.x, location.y);
            } else {
                fill(color);
                ellipse(location.x, location.y, r, r);
            }

            previousLocation = location.get();
        }
    }
}


