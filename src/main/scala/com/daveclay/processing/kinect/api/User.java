package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class User {

    private SimpleOpenNI kinect;
    private LogSketch logSketch;
    private Integer userId;

    public final PVector centerOfMass = new PVector();
    public final Joint head = new Joint(SimpleOpenNI.SKEL_HEAD);
    public final Joint neck = new Joint(SimpleOpenNI.SKEL_NECK);
    public final Joint torso = new Joint(SimpleOpenNI.SKEL_TORSO);
    public final Joint leftShoulder = new Joint(SimpleOpenNI.SKEL_LEFT_SHOULDER);
    public final Joint leftElbow = new Joint(SimpleOpenNI.SKEL_LEFT_ELBOW);
    public final Joint leftHand = new Joint(SimpleOpenNI.SKEL_LEFT_HAND);
    public final Joint leftFingertip = new Joint(SimpleOpenNI.SKEL_LEFT_FINGERTIP);
    public final Joint rightShoulder = new Joint(SimpleOpenNI.SKEL_RIGHT_SHOULDER);
    public final Joint rightElbow = new Joint(SimpleOpenNI.SKEL_RIGHT_ELBOW);
    public final Joint rightHand = new Joint(SimpleOpenNI.SKEL_RIGHT_HAND);
    public final Joint rightFingertip = new Joint(SimpleOpenNI.SKEL_RIGHT_FINGERTIP);
    public final Joint leftHip = new Joint(SimpleOpenNI.SKEL_LEFT_HIP);
    public final Joint leftKnee = new Joint(SimpleOpenNI.SKEL_LEFT_KNEE);
    public final Joint leftFoot = new Joint(SimpleOpenNI.SKEL_LEFT_FOOT);
    public final Joint rightHip = new Joint(SimpleOpenNI.SKEL_RIGHT_HIP);
    public final Joint rightKnee = new Joint(SimpleOpenNI.SKEL_RIGHT_KNEE);
    public final Joint rightFoot = new Joint(SimpleOpenNI.SKEL_RIGHT_FOOT);

    final List<Joint> joints = new ArrayList<Joint>();
    {
        joints.add(head);
        joints.add(neck);
        joints.add(torso);
        joints.add(leftShoulder);
        joints.add(leftElbow);
        joints.add(leftHand);
        joints.add(leftFingertip);
        joints.add(rightShoulder);
        joints.add(rightElbow);
        joints.add(rightHand);
        joints.add(rightFingertip);
        joints.add(leftHip);
        joints.add(leftKnee);
        joints.add(leftFoot);
        joints.add(rightHip);
        joints.add(rightKnee);
        joints.add(rightFoot);
    }


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
            for (Joint joint : joints) {
                kinect.getJointPositionSkeleton(userId, joint.id, joint.position);
            }
            kinect.getCoM(userId, centerOfMass);
        }
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

    public PVector convertRealWorldToProjective(Joint joint) {
        PVector converted = new PVector();
        kinect.convertRealWorldToProjective(joint.position, converted);
        return converted;
    }

    public void startTrackingWithUserId(int userId) {
        this.userId = userId;
        kinect.startTrackingSkeleton(userId);
        System.out.println(this + ": start tracking userId " + userId);
    }

    public boolean isLeftHandExtended(float threshold) {
        return ! VectorMath.isWithin(leftHand.position, leftShoulder.position, threshold);
    }

    public boolean isRightHandExtended(float threshold) {
        return ! VectorMath.isWithin(rightHand.position, rightShoulder.position, threshold);
    }

    public boolean isWithinDistanceFromCenterOfMass(PVector position, float threshold) {
        return VectorMath.isWithin(position, centerOfMass, threshold);
    }
}
