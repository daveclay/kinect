package com.daveclay.processing.kinect.api;

public class RightHandState extends HandState {
    private final User user;

    public RightHandState(User user) {
        this.user = user;
    }

    @Override
    public boolean isHandExtended() {
        return user.isRightHandExtended(handExtensionThresholdRadius);
    }
}
