package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.gestures.*;
import com.daveclay.processing.kinect.api.*;
import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StageMonitor;
import com.daveclay.server.presentation.PresentationServer;
import com.daveclay.server.presentation.PresentationWebSocketListener;
import processing.core.PVector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class BodySeek extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();

        BodySeek bodyLocator = new BodySeek(logSketch);

        SketchRunner.run(logSketch, bodyLocator);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
    }

    private User user;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    public BodySeek(LogSketch logSketch) {
        super();
        setSketchCallback(new SketchCallback() {
            @Override
            public void draw() {
                drawBodyLocator();
            }

            @Override
            public void setup() {
                background(0);
            }
        });

        this.logSketch = logSketch;
        int width = 1920;
        int height = 1080;


        for (int i = 0; i < 600; i++) {
            this.vehicles.add(new Vehicle(random(width), random(height), i));
        }

        registerEventListeners();
    }

    protected void registerEventListeners() {
        onUserEntered(new UserEnteredHandler() {
            @Override
            public void userDidEnter(User user) {
                System.out.println("HELLO User " + user.getID() + "!");
                BodySeek.this.user = user;
            }
        });

        onUserWasLost(new UserWasLostHandler() {
            @Override
            public void userWasLost(User user) {
                System.out.println("User " + user.getID() + " LOST, eh well...");
                BodySeek.this.user = null;
            }
        });
    }

    private void drawBodyLocator() {
        logSketch.logRounded("FPS", frameRate);
        updateUserDataAndDrawStuff();
    }

    private void updateUserDataAndDrawStuff() {
        if (user != null) {
            PVector newUserPosition = user.getJointPosition(KinectPV2.JointType_SpineMid);
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
        fill(0, 0, 0, 200);
        rect(0, 0, getWidth(), getHeight());
        fill(255);
        textSize(100);
        text("Lost User", getHeight() / 3, getWidth() / 3);
    }

    private void drawUserData(User user) {
        noStroke();
        fill(0, 0, 0, 10);
        rect(0, 0, width, height);

        pushMatrix();

        PVector leftHandPosition2d = user.getRightHandPosition2D();
        PVector rightHandPosition2d = user.getLeftHandPosition2D();

        logSketch.logScreenCoords("Right Hand", rightHandPosition2d);
        logSketch.logScreenCoords("Left Hand", leftHandPosition2d);

        // Draw an ellipse at the mouse location
        fill(100, 200, 30);
        stroke(0);
        strokeWeight(2);
        ellipse(leftHandPosition2d.x, leftHandPosition2d.y, 20, 20);
        popMatrix();

        // Call the appropriate steering behaviors for our agents
        for (Vehicle vehicle : vehicles) {
            if (vehicle.index % 2 == 0) {
                vehicle.seek(leftHandPosition2d);
            } else {
                vehicle.seek(rightHandPosition2d);
            }
            vehicle.update();
            vehicle.display();
        }

    }


    class Vehicle {

        int index;
        int color;
        PVector location;
        PVector velocity;
        PVector acceleration;
        float r;
        float maxforce;    // Maximum steering force
        float maxspeed;    // Maximum speed

        List<PVector> previousLocations = new ArrayList<PVector>();

        public Vehicle(float x, float y, int index) {
            this.index = index;
            this.color = color(random(255), random(255), random(255));
            acceleration = new PVector(0, 0);
            velocity = new PVector(0, -2);
            location = new PVector(x, y);
            r = 10;
            maxspeed = random(9, 13);
            maxforce = random(.3f, .6f);
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
            fill(color);
            ellipse(location.x, location.y, r, r);
        }
    }
}


