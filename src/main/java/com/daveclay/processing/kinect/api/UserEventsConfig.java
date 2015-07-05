package com.daveclay.processing.kinect.api;

import java.util.ArrayList;
import java.util.List;

public class UserEventsConfig {

    protected float handExtensionThresholdRadius = .3f;

    private List<HandExtendedHandler> leftHandExtendedHandlers = new ArrayList<HandExtendedHandler>();
    private List<HandExtendedHandler> rightHandExtendedHandlers = new ArrayList<HandExtendedHandler>();

    public List<HandExtendedHandler> getLeftHandExtendedHandlers() {
        return leftHandExtendedHandlers;
    }

    public void setLeftHandExtendedHandlers(List<HandExtendedHandler> leftHandExtendedHandlers) {
        this.leftHandExtendedHandlers = leftHandExtendedHandlers;
    }

    public List<HandExtendedHandler> getRightHandExtendedHandlers() {
        return rightHandExtendedHandlers;
    }

    public void setRightHandExtendedHandlers(List<HandExtendedHandler> rightHandExtendedHandlers) {
        this.rightHandExtendedHandlers = rightHandExtendedHandlers;
    }

    public float getHandExtensionThresholdRadius() {
        return handExtensionThresholdRadius;
    }

    public void setHandExtensionThresholdRadius(int handExtensionThresholdRadius) {
        this.handExtensionThresholdRadius = handExtensionThresholdRadius;
    }

    public void addLeftHandExtendedHandler(HandExtendedHandler handExtendedHandler) {
        this.leftHandExtendedHandlers.add(handExtendedHandler);
    }

    public void addRightHandExtendedHandler(HandExtendedHandler handExtendedHandler) {
        this.rightHandExtendedHandlers.add(handExtendedHandler);
    }
}
