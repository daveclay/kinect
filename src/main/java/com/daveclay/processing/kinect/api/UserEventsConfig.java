package com.daveclay.processing.kinect.api;

public class UserEventsConfig {

    protected float handExtensionThresholdRadius = .3f;

    public float getHandExtensionThresholdRadius() {
        return handExtensionThresholdRadius;
    }

    public void setHandExtensionThresholdRadius(int handExtensionThresholdRadius) {
        this.handExtensionThresholdRadius = handExtensionThresholdRadius;
    }
}
