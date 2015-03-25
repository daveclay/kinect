package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.FrameExporter;
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

public class BodyLines extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();

        BodyLines bodyLocator = new BodyLines(logSketch);

        SketchRunner.run(logSketch, bodyLocator);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
    }

    private User user;
    private Stage stage;
    private FrameExporter frameExporter;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    public BodyLines(LogSketch logSketch) {
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
        this.logSketch = logSketch;
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
                BodyLines.this.user = user;
                // BodySeek.this.frameExporter.start();
            }
        });

        onUserWasLost(new UserWasLostHandler() {
            @Override
            public void userWasLost(User user) {
                System.out.println("User " + user.getID() + " LOST, eh well...");
                BodyLines.this.user = null;
                BodyLines.this.frameExporter.stop();
            }
        });
    }

    private void drawBodyLocator() {
        logSketch.logRounded("FPS", frameRate);
        updateUserDataAndDrawStuff();
    }

    private void updateUserDataAndDrawStuff() {
        if (user != null) {
            PVector newUserPosition = user.getJointPosition3D(KinectPV2.JointType_SpineMid);
            stage.updatePosition(newUserPosition);
            // draw user data.
            drawUserData(user);
        } else {
            // Note that this will override the gesture recognized notification. The user will likely
            // have seen the results of a recognized gesture, and wants to know that they should stop
            // expecting gestures immediately if the sensor has lost them.
            //
            // In other words, don't allow the user to look like an idiot expecting gestures to work
            // if we've lost them. Notify them immediately so they don't look like an idiot.
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
        PVector rightHandPosition2d = user.getJointPosition2D(KinectPV2.JointType_HandRight);

        logSketch.logScreenCoords("Right Hand", rightHandPosition2d);
        logSketch.logScreenCoords("Left Hand", leftHandPosition2d);

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


