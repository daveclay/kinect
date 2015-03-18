package com.daveclay.processing.kinect.api;

import KinectPV2.KJoint;
import KinectPV2.KinectPV2;

import java.util.List;

public class HandState {

    private final User user;
    private final int hand;
    private UserEventsConfig userEventsConfig = new UserEventsConfig();
    private boolean handWasPreviouslyExtended;

    public HandState(User user, int hand) {
        this.user = user;
        this.hand = hand;
    }

    public void setUserEventsConfig(UserEventsConfig userEventsConfig) {
        this.userEventsConfig = userEventsConfig;
    }

    public boolean isHandExtended() {
        return user.isHandExtended(hand, userEventsConfig.handExtensionThresholdRadius);
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

    private List<HandExtendedHandler> getHandExtendedHandlers() {
        if (hand == KinectPV2.JointType_HandLeft) {
            return this.userEventsConfig.getLeftHandExtendedHandlers();
        } else if (hand == KinectPV2.JointType_HandRight) {
            return this.userEventsConfig.getRightHandExtendedHandlers();
        } else {
            throw new IllegalStateException("Unknown hand joint type: " + hand);
        }
    }

    private void handWasExtended() {
        for (HandExtendedHandler handListeners : getHandExtendedHandlers()) {
            handListeners.onHandExtended();
        }
    }

    private void handWasRetracted() {
        for (HandExtendedHandler handListeners : getHandExtendedHandlers()) {
            handListeners.onHandRetracted();
        }
    }
}
