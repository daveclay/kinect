package com.daveclay.processing.kinect.api;

import processing.core.PVector;

public class Joint {
    final PVector position = new PVector();
    final int id;

    public Joint(int id) {
        this.id = id;
    }
}
