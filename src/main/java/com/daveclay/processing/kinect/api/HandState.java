package com.daveclay.processing.kinect.api;

import java.util.ArrayList;
import java.util.List;

public class HandState {

    private final User user;
    private final int hand;
    private UserEventsConfig userEventsConfig = new UserEventsConfig();
    // TODO: WeakReference - when a user is lost, the corresponding handler should go away.
    private List<HandExtendedHandler> handExtendedHandlers = new ArrayList<>();
    private boolean handWasPreviouslyExtended;

    public HandState(User user, int hand) {
        this.user = user;
        this.hand = hand;
    }

    public void addHandExtendedHandler(HandExtendedHandler handExtendedHandler) {
        this.handExtendedHandlers.add(handExtendedHandler);
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

    private void handWasExtended() {
        for (HandExtendedHandler handListeners : handExtendedHandlers) {
            handListeners.onHandExtended(user);
        }
    }

    private void handWasRetracted() {
        for (HandExtendedHandler handListeners : handExtendedHandlers) {
            handListeners.onHandRetracted(user);
        }
    }
}
