package com.daveclay.opencv;

import org.opencv.core.Core;

public class OpenCVConstants {

    public static final String HAAR_CASCADES_PATH = "/Users/daveclay/work/opencv-2.4.7/data/haarcascades/haarcascade_frontalface_alt.xml";

    public static void loadNativeLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
