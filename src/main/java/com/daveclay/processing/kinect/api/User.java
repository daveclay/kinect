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
    public Joint head;
    public Joint neck;
    public Joint torso;
    public Joint leftShoulder;
    public Joint leftElbow;
    public Joint rightHand;
    public Joint leftFingertip;
    public Joint rightShoulder;
    public Joint rightElbow;
    public Joint leftHand;
    public Joint rightFingertip;
    public Joint leftHip;
    public Joint leftKnee;
    public Joint leftFoot;
    public Joint rightHip;
    public Joint rightKnee;
    public Joint rightFoot;

    private List<Joint> joints;

    /**
     * Kinda very much required, but isn't available at construction time.
     *
     * Note that left and right are reversed: SimpleOpenNI's constants are
     * based on the mirrored image of the data. Since most of what I use
     * this for is gestures, and I reference those gestures back to the
     * user, I reverse them back so that "left to right" is correct in
     * the real world.
     *
     * If we're using this to render a character in the virtual world,
     * in the character's virtual world these would have to be reversed.
     * The character's left and right are mirrored. But, you'd have to
     * reverse left and right if you were talking to the real world user
     * about movements.
     */
    public void setKinect(SimpleOpenNI kinect) {
        this.kinect = kinect;

        joints = new ArrayList<Joint>();

        head = new Joint(SimpleOpenNI.SKEL_HEAD);
        neck = new Joint(SimpleOpenNI.SKEL_NECK);
        torso = new Joint(SimpleOpenNI.SKEL_TORSO);
        leftShoulder = new Joint(SimpleOpenNI.SKEL_LEFT_SHOULDER);
        leftElbow = new Joint(SimpleOpenNI.SKEL_LEFT_ELBOW);
        rightHand = new Joint(SimpleOpenNI.SKEL_LEFT_HAND);
        leftFingertip = new Joint(SimpleOpenNI.SKEL_LEFT_FINGERTIP);
        rightShoulder = new Joint(SimpleOpenNI.SKEL_RIGHT_SHOULDER);
        rightElbow = new Joint(SimpleOpenNI.SKEL_RIGHT_ELBOW);
        leftHand = new Joint(SimpleOpenNI.SKEL_RIGHT_HAND);
        rightFingertip = new Joint(SimpleOpenNI.SKEL_RIGHT_FINGERTIP);
        leftHip = new Joint(SimpleOpenNI.SKEL_LEFT_HIP);
        leftKnee = new Joint(SimpleOpenNI.SKEL_LEFT_KNEE);
        leftFoot = new Joint(SimpleOpenNI.SKEL_LEFT_FOOT);
        rightHip = new Joint(SimpleOpenNI.SKEL_RIGHT_HIP);
        rightKnee = new Joint(SimpleOpenNI.SKEL_RIGHT_KNEE);
        rightFoot = new Joint(SimpleOpenNI.SKEL_RIGHT_FOOT);

        joints.add(head);
        joints.add(neck);
        joints.add(torso);
        joints.add(leftShoulder);
        joints.add(leftElbow);
        joints.add(rightHand);
        joints.add(leftFingertip);
        joints.add(rightShoulder);
        joints.add(rightElbow);
        joints.add(leftHand);
        joints.add(rightFingertip);
        joints.add(leftHip);
        joints.add(leftKnee);
        joints.add(leftFoot);
        joints.add(rightHip);
        joints.add(rightKnee);
        joints.add(rightFoot);
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

    public PVector convertRealWorldToProjectiveMirrored(Joint joint) {
        PVector projective = convertRealWorldToProjective(joint);
        return VectorMath.reflectVertically(projective);
    }

    public void startTrackingWithUserId(int userId) {
        this.userId = userId;
        kinect.startTrackingSkeleton(userId);
        System.out.println(this + ": start tracking userId " + userId);
    }

    public boolean isRightHandExtended(float threshold) {
        /*
        double distance = Math.sqrt(VectorMath.getZDistanceSquared(rightHand.position, centerOfMass));
        logSketch.logRounded("Left hand distance", distance);
        logSketch.logVector("Left Hand Pos", rightHand.position);
        logSketch.logVector("Torso", torso.position);
        */
        return ! VectorMath.isWithinZ(rightHand.position, torso.position, threshold);
    }

    public boolean isLeftHandExtended(float threshold) {
        return ! VectorMath.isWithin(leftHand.position, torso.position, threshold);
    }

    public boolean isWithinDistanceFromCenterOfMass(PVector position, float threshold) {
        return VectorMath.isWithin(position, centerOfMass, threshold);
    }
}
