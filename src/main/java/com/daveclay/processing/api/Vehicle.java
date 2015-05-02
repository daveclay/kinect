package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Vehicle {

    PApplet sketch;
    public PVector location;
    public PVector velocity;
    public PVector acceleration;
    public float r;
    public float maxforce;    // Maximum steering force
    public float maxspeed;    // Maximum speed

    // Constructor initialize all values
    public Vehicle(PApplet sketch, float x, float y) {
        this.sketch = sketch;
        location = new PVector(x, y);
        r = 12;
        maxspeed = 3;
        maxforce = 0.1f;
        acceleration = new PVector(0, 0);
        velocity = new PVector(0, 0);
    }

    public void applyForce(PVector force) {
        // We could add mass here if we want A = F / M
        acceleration.add(force);
    }

    public void applyBehaviors(ArrayList<Vehicle> vehicles) {
        PVector separateForce = separate(vehicles);
        separateForce.mult(2);
        applyForce(separateForce);
    }

    // A method that calculates a steering force towards a target
    // STEER = DESIRED MINUS VELOCITY
    public void seek(PVector target) {
        PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target
        flow(desired);
    }

    public void flow(PVector desired) {
        // Normalize desired and scale to maximum speed
        desired.normalize();
        desired.mult(maxspeed);
        // Steering = Desired minus velocity
        PVector steer = PVector.sub(desired,velocity);
        steer.limit(maxforce);  // Limit to maximum steering force

        applyForce(steer);
    }

    // Separation
    // Method checks for nearby vehicles and steers away
    public PVector separate(ArrayList<Vehicle> vehicles) {
        float desiredseparation = r*2;
        PVector sum = new PVector();
        int count = 0;
        // For every boid in the system, check if it's too close
        for (Vehicle other : vehicles) {
            float d = PVector.dist(location, other.location);
            // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
            if ((d > 0) && (d < desiredseparation)) {
                // Calculate vector pointing away from neighbor
                PVector diff = PVector.sub(location, other.location);
                diff.normalize();
                diff.div(d);        // Weight by distance
                sum.add(diff);
                count++;            // Keep track of how many
            }
        }
        // Average -- divide by how many
        if (count > 0) {
            sum.div(count);
            // Our desired vector is the average scaled to maximum speed
            sum.normalize();
            sum.mult(maxspeed);
            // Implement Reynolds: Steering = Desired - Velocity
            sum.sub(velocity);
            sum.limit(maxforce);
        }
        return sum;
    }


    // Method to update location
    public void update() {
        // Update velocity
        velocity.add(acceleration);
        // Limit speed
        velocity.limit(maxspeed);
        location.add(velocity);

        if (location.x > sketch.width) {
            location.x = 0;
        } else if (location.x < 0) {
            location.x = sketch.width;
        }
        if (location.y > sketch.height) {
            location.y = 0;
        } else if (location.y < 0) {
            location.y = sketch.height;
        }

        // Reset accelertion to 0 each cycle
        acceleration.mult(0);
    }
}