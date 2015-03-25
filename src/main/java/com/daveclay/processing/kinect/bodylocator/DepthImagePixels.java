package com.daveclay.processing.kinect.bodylocator;

import KinectPV2.KinectPV2;
import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.kinect.api.UserTrackingSketch;

public class DepthImagePixels extends UserTrackingSketch {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();

        DepthImagePixels bodyLocator = new DepthImagePixels(logSketch);

        SketchRunner.run(logSketch, bodyLocator);

        logSketch.frame.setLocation(0, 100);
        bodyLocator.frame.setLocation(logSketch.getWidth() + 10, 100);
    }

    int size = KinectPV2.WIDTHDepth * KinectPV2.HEIGHTDepth;
    IntValueMeasurement xValues = new IntValueMeasurement();
    IntValueMeasurement yValues = new IntValueMeasurement();
    IntValueMeasurement zValues = new IntValueMeasurement();

    public DepthImagePixels(LogSketch logSketch) {
        super();
        setSketchCallback(new SketchCallback() {
            @Override
            public void draw() {
                drawBodyLocator();
            }

            @Override
            public void setup(KinectPV2 kinect) {
                kinect.enableDepthImg(true);
                kinect.activateRawDepth(true);
                kinect.setLowThresholdPC(2);
                kinect.setHighThresholdPC(3);
            }
        });

        this.logSketch = logSketch;
    }

    private void drawBodyLocator() {
        background(0, 0, 255);
        smooth();
        logSketch.logRounded("FPS", frameRate);
        int [] depthRaw = getKinect().getRawDepth();


        logSketch.log("R", xValues.getCurrent() + " " + xValues.getMin() + " / " + xValues.getMax());
        logSketch.log("G", yValues.getCurrent() + " " + yValues.getMin() + " / " + yValues.getMax());
        logSketch.log("B", zValues.getCurrent() + " " + zValues.getMin() + " / " + zValues.getMax());

        int count = 0;
        int divisor = 10;
        int loop = depthRaw.length / divisor;
        for (int i = 0; i < loop; i++) {
            int index = i * divisor;
            int x = index % KinectPV2.WIDTHDepth;
            int y = index / KinectPV2.HEIGHTDepth;
            int origX = depthRaw[index];

            int a = origX >> 24 & 0xff;
            int g = origX >> 16 & 0xff;
            int b = origX >> 8 & 0xff;
            int r = origX & 0xff;

            if (x > 200 && x < 300 && y > 200 && y < 300) {
                xValues.add(r);
                yValues.add(g);
                zValues.add(a);
            }

            float d = xValues.mapValues(origX, 255, 0);

            noStroke();
            fill(b, g, r, a);
            ellipse(x, y, 2f, 2f);
            count++;
        }

        stroke(255, 255, 0);
        noFill();
        rect(200, 200, 100, 100);
        logSketch.log("Loop", count + "");

    }

    /*
    private void redDuplicateOddities() {
        background(0);
        smooth();
        logSketch.logRounded("FPS", frameRate);
        FloatBuffer buffer = getKinect().getPointCloudColorPos();

        logSketch.log("X", xValues.getMin() + " / " + xValues.getMax());
        logSketch.log("Y", yValues.getMin() + " / " + yValues.getMax());
        logSketch.log("Z", zValues.getMin() + " / " + zValues.getMax());

        int count = 0;
        int divisor = 20;
        int loop = size / divisor;
        for (int i = 0; i < loop; i++) {
            int index = i * divisor;
            float origX = buffer.get(index);
            float origY = buffer.get(index + 1);
            float origZ = buffer.get(index + 2);

            xValues.add(origX);
            yValues.add(origY);
            zValues.add(origZ);

            float x = xValues.mapValues(origX, 0, width);
            float y = yValues.mapValues(origY, 0, height);
            float z = zValues.mapValues(origZ, 0, 255);

            noStroke();
            fill(255, 0, 0, z);
            ellipse(x, y, 2f, 2f);
        }
        logSketch.log("Loop: ", count + "");
    }
    */

    public static class IntValueMeasurement {
        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private int current = 0;

        public void add(int value) {
            min = min(min, value);
            max = max(max, value);
            current = value;
        }

        public int getCurrent() {
            return current;
        }

        public int getRange() {
            return max - min;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public float mapValues(int value, int start, int stop) {

            return map(value, min, max, start, stop);
        }
    }

}


