package com.daveclay.opencv;

import org.opencv.core.Core;

public class OpenCVConstants {

    public static final String CASCADES_PATH = "/Users/daveclay/work/opencv-2.4.7/data/";

    public static final String HAAR_CASCADES_BASE_PATH = CASCADES_PATH + "haarcascades/";
    public static final String HAAR_CASCADES_PATH = HAAR_CASCADES_BASE_PATH + "/haarcascade_frontalface_alt.xml";

    public static final String LBP_CASCADES_BASE_PATH = CASCADES_PATH + "lbpcascades/";
    public static final String LBP_CASCADES_FRONTAL_FACE_PATH = LBP_CASCADES_BASE_PATH + "lbpcascade_frontalface.xml";

    public static void loadOpenCVNativeLibrary() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
