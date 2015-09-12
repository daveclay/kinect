package com.daveclay.processing.kinect.api;

import KinectPV2.KJoint;
import processing.core.PVector;

public class KinectUtils {

    public static PVector jointToPVector(KJoint joint) {
        return new PVector(joint.getX(), joint.getY(), joint.getZ());
    }
}
