package com.daveclay.processing.kinect.api;

public class LeftHandState extends HandState {
    private final User user;

    public LeftHandState(User user) {
        this.user = user;
    }

    @Override
    public boolean isHandExtended() {
        return user.isLeftHandExtended(handExtensionThresholdRadius);
    }
}
