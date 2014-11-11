package com.daveclay.processing.kinect.api;

import java.util.ArrayList;
import java.util.List;

public abstract class HandState {
    private boolean handWasPreviouslyExtended;
    private List<HandExtendedHandler> handExtendedHandlers = new ArrayList<HandExtendedHandler>();

    protected int handExtensionThresholdRadius = 350;

    abstract boolean isHandExtended();

    public void addHandExtendedHandler(HandExtendedHandler handExtendedHandler) {
        this.handExtendedHandlers.add(handExtendedHandler);
    }

    public void setHandExtensionThresholdRadius(int handExtensionThresholdRadius) {
        this.handExtensionThresholdRadius = handExtensionThresholdRadius;
    }

    public void triggerHandStateEvents() {
        boolean handCurrentlyExtended = isHandExtended();

        if (handWasPreviouslyExtended && !handCurrentlyExtended) {
            handWasRetracted();
        } else if (!handWasPreviouslyExtended && handCurrentlyExtended) {
            handWasExtended();
        }

        handWasPreviouslyExtended = handCurrentlyExtended;

    }

    private void handWasExtended() {
        for (HandExtendedHandler handListeners : this.handExtendedHandlers) {
            handListeners.onHandExtended();
        }
    }

    private void handWasRetracted() {
        for (HandExtendedHandler handListeners : this.handExtendedHandlers) {
            handListeners.onHandRetracted();
        }
    }
}
