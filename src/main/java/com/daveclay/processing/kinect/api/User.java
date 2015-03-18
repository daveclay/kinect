package com.daveclay.processing.kinect.api;

import KinectPV2.*;
import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

public class User {

    private final int id;
    private final KinectPV2 kinect;
    private final Skeleton skeleton;
    private final KJoint leftHand;
    private final KJoint rightHand;
    private final KJoint torso;
    private final HandState leftHandState;
    private final HandState rightHandState;

    public User(KinectPV2 kinect, Skeleton skeleton, UserEventsConfig userEventsConfig, int index) {
        this.id = index;
        this.kinect = kinect;
        this.skeleton = skeleton;
        this.leftHand = findJoint(KinectPV2.JointType_HandLeft);
        this.rightHand = findJoint(KinectPV2.JointType_HandLeft);
        this.torso = findJoint(KinectPV2.JointType_SpineBase);
        this.leftHandState = new HandState(this, this.leftHand);
        this.rightHandState = new HandState(this, this.rightHand);
        this.leftHandState.setUserEventsConfig(userEventsConfig);
        this.rightHandState.setUserEventsConfig(userEventsConfig);
    }

    public int getID() {
        return id;
    }

    public void triggerUserInteractionListeners() {
        leftHandState.triggerHandStateEvents();
        rightHandState.triggerHandStateEvents();
    }

    public boolean isCurrentlyTracking() {
        return skeleton.isTracked();
    }

    public PVector getLeftHandMirroredPosition() {
        return getMirroredPosition(leftHand);
    }

    public PVector getRightHandPosition() {
        return KinectUtils.getPosition(rightHand);
    }

    public PVector getRightHandMirroredPosition() {
        return getMirroredPosition(rightHand);
    }

    public PVector getJointPosition(int joint) {
        return KinectUtils.getPosition(skeleton.getJoints()[joint]);
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    private PVector getMirroredPosition(KJoint joint) {
        return VectorMath.reflectVertically(KinectUtils.getPosition(joint));
    }

    public boolean isHandExtended(KJoint hand, float threshold) {
        return ! VectorMath.isWithinZ(
                KinectUtils.getPosition(hand),
                KinectUtils.getPosition(torso),
                threshold);
    }

    public boolean isRightHandExtended(float threshold) {
        /*
        double distance = Math.sqrt(VectorMath.getZDistanceSquared(rightHand.position, centerOfMass));
        logSketch.logRounded("Left hand distance", distance);
        logSketch.logVector("Left Hand Pos", rightHand.position);
        logSketch.logVector("Torso", torso.position);
        */
        return isHandExtended(rightHand, threshold);
    }

    public boolean isLeftHandExtended(float threshold) {
        return isHandExtended(leftHand, threshold);
    }

    private KJoint findJoint(int which) {
        KJoint[] joints = skeleton.getJoints();
        return joints[which];
    }

}
