package com.daveclay.processing.kinect.api;

import processing.core.PVector;

public class Joint {
    public final PVector position = new PVector();
    public final int id;

    public Joint(int id) {
        this.id = id;
    }
}
