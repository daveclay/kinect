package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;
import com.daveclay.processing.api.LogSketch;

public abstract class AbstractSingleUserTrackingSketch extends SingleUserTrackingSketch {

    protected User user;
    protected LogSketch logSketch;

    public AbstractSingleUserTrackingSketch(User user, UserTrackingKinectConfig userTrackingKinectConfig) {
        super(user, userTrackingKinectConfig);
    }

    public User getUser() {
        return user;
    }

}
