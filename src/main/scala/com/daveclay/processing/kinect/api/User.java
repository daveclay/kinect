package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

public class User {

    private SimpleOpenNI kinect;
    private LogSketch logSketch;
    private Integer userId;
    private PVector centerOfMass = new PVector();
    private PVector leftHandPosition3d = new PVector();
    private PVector rightHandPosition3d = new PVector();

    // if we're translating, but don't bother by default.
    private PVector leftHandPosition2d = new PVector();
    private PVector rightHandPosition2d = new PVector();

    /**
     * Kinda required, but isn't available at construction time.
     */
    public void setKinect(SimpleOpenNI kinect) {
        this.kinect = kinect;
    }

    public void setLogSketch(LogSketch logSketch) {
        this.logSketch = logSketch;
    }

    public void updateData() {
        if (isCurrentlyTracking()) {
            logSketch.log("FUCK", "Tracking: " + userId);
            kinect.getCoM(userId, centerOfMass);
            kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHandPosition3d);
            kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHandPosition3d);
        } else {
            logSketch.log("FUCK", "NOT FUCKING TRACKING: " + userId);
        }
    }

    public void convertRealWorld3DToProjective2D() {
        // switch from 3D "real world" to 2D "projection"
        kinect.convertRealWorldToProjective(leftHandPosition3d, leftHandPosition2d);
        kinect.convertRealWorldToProjective(rightHandPosition3d, rightHandPosition2d);
    }

    public void lost() {
        System.out.println("Lost " + userId);
        this.userId = null;
    }

    public boolean isCurrentlyTracking() {
        //return userId != null && kinect.isTrackingSkeleton(userId);
        return userId != null;
    }

    public Integer getUserId() {
        return userId;
    }

    public PVector getCenterOfMass() {
        return centerOfMass;
    }

    public PVector getLeftHandPosition3d() {
        return leftHandPosition3d;
    }

    public PVector getRightHandPosition3d() {
        return rightHandPosition3d;
    }

    public PVector getLeftHandPosition2d() {
        return leftHandPosition2d;
    }

    public PVector getRightHandPosition2d() {
        return rightHandPosition2d;
    }

    public PVector getLeftHandPositionMirrored2D() {
        return VectorMath.reflectVertically(leftHandPosition2d);
    }

    public PVector getRightHandPositionMirrored2D() {
        return VectorMath.reflectVertically(rightHandPosition2d);
    }

    public void startTrackingWithUserId(int userId) {
        this.userId = userId;
        kinect.startTrackingSkeleton(userId);
        System.out.println(this + ": start tracking userId " + userId);
    }
}
