package com.daveclay.processing.video.capture;

public class VideoCaptureDevice {
    private int index;
    private String info;

    public VideoCaptureDevice(int i, String info) {
        this.index = i;
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }
}
