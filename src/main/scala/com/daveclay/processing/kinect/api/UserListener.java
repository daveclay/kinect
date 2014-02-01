package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;

public interface UserListener {
    public void onNewUser(SimpleOpenNI curContext, int userId);
    public void onLostUser(SimpleOpenNI kinect, int userId);
}
