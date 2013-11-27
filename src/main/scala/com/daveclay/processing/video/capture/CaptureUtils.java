package com.daveclay.processing.video.capture;

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
}
