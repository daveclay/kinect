package com.daveclay.processing.kinect.api;

import KinectPV2.*;
import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

import java.awt.*;
import java.awt.geom.Dimension2D;

public class User {

    public static final float EIGHTH = 1f / 8f;

    private final int id;
    private final Skeleton skeleton3D;
    private final Skeleton colorSkeleton;
    private final HandState leftHandState;
    private final HandState rightHandState;
    private final int color;

    public User(Skeleton skeleton3D,
                Skeleton colorSkeleton,
                UserEventsConfig userEventsConfig,
                int index) {
        this.id = index;
        this.skeleton3D = skeleton3D;
        this.colorSkeleton = colorSkeleton;
        this.leftHandState = new HandState(this, KinectPV2.JointType_HandLeft);
        this.rightHandState = new HandState(this, KinectPV2.JointType_HandRight);
        this.leftHandState.setUserEventsConfig(userEventsConfig);
        this.rightHandState.setUserEventsConfig(userEventsConfig);
        this.color = Color.HSBtoRGB(EIGHTH * index, 1f, .9f);
    }

    public int getID() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public void onRightHandExtended(HandExtendedHandler handExtendedHandler) {
        rightHandState.addHandExtendedHandler(handExtendedHandler);
    }

    public void onLeftHandExtended(HandExtendedHandler handExtendedHandler) {
        leftHandState.addHandExtendedHandler(handExtendedHandler);
    }

    public void update() {
        leftHandState.triggerHandStateEvents();
        rightHandState.triggerHandStateEvents();
    }

    public boolean isCurrentlyTracking() {
        return skeleton3D.isTracked();
    }

    public PVector getLeftHandPosition2D() {
        return getJointPosition2D(KinectPV2.JointType_HandLeft);
    }

    public PVector getRightHandPosition2D() {
        return getJointPosition2D(KinectPV2.JointType_HandRight);
    }

    private PVector getJointPositionForSkeleton(int joint, Skeleton skeleton) {
        return KinectUtils.jointToPVector(skeleton.getJoints()[joint]);
    }

    public PVector getJointPosition2D(int joint) {
        return getJointPositionForSkeleton(joint, colorSkeleton);
    }

    public PVector getJointPosition3D(int joint) {
        return getJointPositionForSkeleton(joint, skeleton3D);
    }

    public Skeleton getSkeleton3D() {
        return skeleton3D;
    }

    public PVector getRightHandPosition() {
        return getJointPosition3D(KinectPV2.JointType_HandRight);
    }

    public PVector getLeftHandPosition() {
        return getJointPosition3D(KinectPV2.JointType_HandLeft);
    }

    private PVector getMirroredPosition(PVector position) {
        return VectorMath.reflectVertically(position);
    }

    boolean isHandExtended(int hand, float threshold) {
        PVector handPosition = getJointPosition3D(hand);
        PVector torsoPosition = getJointPosition3D(KinectPV2.JointType_SpineBase);
        return ! VectorMath.isWithinZ(
                handPosition,
                torsoPosition,
                threshold);
    }

    private KJoint findJoint(int which) {
        KJoint[] joints = skeleton3D.getJoints();
        return joints[which];
    }

}
