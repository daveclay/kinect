package com.daveclay.processing.kinect.api;

import com.daveclay.processing.api.SketchRunner;

public class TestUserTracking extends AbstractSingleUserTrackingSketch {

    public static void main(String[] args) {
        SketchRunner.run(new TestUserTracking());
    }

    @Override
    public void setupUserTrackingSketch() {
        size(640, 480, OPENGL);
    }

    @Override
    public void drawUserTrackingSketch() {
        background(100);
        fill(255, 0, 0);
        rect(10, 10, 200, 200);
    }
}
