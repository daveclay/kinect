package com.daveclay.processing.kinect.api;

import KinectPV2.*;
import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

public class User {

    private final int id;
    private final KinectPV2 kinect;
    private final Skeleton skeleton3D;
    private final Skeleton colorSkeleton;
    private final HandState leftHandState;
    private final HandState rightHandState;

    public User(KinectPV2 kinect,
                Skeleton skeleton3D,
                Skeleton colorSkeleton,
                UserEventsConfig userEventsConfig,
                int index) {
        Skeleton[] hi = kinect.getSkeletonColorMap();
        this.id = index;
        this.kinect = kinect;
        this.skeleton3D = skeleton3D;
        this.colorSkeleton = colorSkeleton;
        this.leftHandState = new HandState(this, KinectPV2.JointType_HandLeft);
        this.rightHandState = new HandState(this, KinectPV2.JointType_HandRight);
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
        return skeleton3D.isTracked();
    }

    public PVector getLeftHandPosition2D() {
        return KinectUtils.getPosition(colorSkeleton.getJoints()[KinectPV2.JointType_HandLeft]);
    }

    public PVector getRightHandPosition2D() {
        return KinectUtils.getPosition(colorSkeleton.getJoints()[KinectPV2.JointType_HandRight]);
    }

    public PVector getJointPosition(int joint) {
        return KinectUtils.getPosition(skeleton3D.getJoints()[joint]);
    }

    public Skeleton getSkeleton3D() {
        return skeleton3D;
    }

    public PVector getRightHandPosition() {
        return getJointPosition(KinectPV2.JointType_HandRight);
    }

    public PVector getLeftHandPosition() {
        return getJointPosition(KinectPV2.JointType_HandLeft);
    }

    private PVector getMirroredPosition(PVector position) {
        return VectorMath.reflectVertically(position);
    }

    public boolean isHandExtended(int hand, float threshold) {
        PVector handPosition = getJointPosition(hand);
        PVector torsoPosition = getJointPosition(KinectPV2.JointType_SpineBase);
        return ! VectorMath.isWithinZ(
                handPosition,
                torsoPosition,
                threshold);
    }

    public boolean isRightHandExtended(float threshold) {
        /*
        double distance = Math.sqrt(VectorMath.getZDistanceSquared(rightHand.position, centerOfMass));
        logSketch.logRounded("Left hand distance", distance);
        logSketch.logVector("Left Hand Pos", rightHand.position);
        logSketch.logVector("Torso", torso.position);
        */
        return isHandExtended(KinectPV2.JointType_HandRight, threshold);
    }

    public boolean isLeftHandExtended(float threshold) {
        return isHandExtended(KinectPV2.JointType_HandLeft, threshold);
    }

    private KJoint findJoint(int which) {
        KJoint[] joints = skeleton3D.getJoints();
        return joints[which];
    }

}
