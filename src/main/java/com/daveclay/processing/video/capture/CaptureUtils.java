package com.daveclay.processing.video.capture;

import processing.core.PApplet;
import processing.video.Capture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaptureUtils {
    List<VideoCaptureDevice> devices = new ArrayList<VideoCaptureDevice>();

    public CaptureUtils() {
        String[] cameras = Capture.list();
        for (int i = 0; i < cameras.length; i++) {
            devices.add(new VideoCaptureDevice(i, cameras[i]));
        }
    }

    public List<VideoCaptureDevice> getVideoCaptureDevices() {
        return Collections.unmodifiableList(devices);
    }

    public Capture openByName(PApplet pApplet, String name) {
        for (VideoCaptureDevice device : devices) {
            if (device.getInfo().equals(name)) {
                return device.open(pApplet);
            }
        }

        throw new IllegalArgumentException("No such device named " + name + ", available devices: " + devices);
    }
}
