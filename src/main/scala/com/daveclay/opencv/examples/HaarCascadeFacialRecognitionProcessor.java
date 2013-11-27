package com.daveclay.opencv.examples;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static com.daveclay.opencv.OpenCVConstants.HAAR_CASCADES_PATH;

public class HaarCascadeFacialRecognitionProcessor {

    private CascadeClassifier face_cascade;

    public HaarCascadeFacialRecognitionProcessor() {
        face_cascade = new CascadeClassifier(HAAR_CASCADES_PATH);
        if (face_cascade.empty()) {
            throw new IllegalStateException("Could not load haarcascades file from " + HAAR_CASCADES_PATH);
        }
    }

    public Mat detect(Mat inputframe) {
        Mat mRgba = new Mat();
        Mat mGrey = new Mat();
        MatOfRect faces = new MatOfRect();
        inputframe.copyTo(mRgba);
        inputframe.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(mGrey, mGrey);
        face_cascade.detectMultiScale(mGrey, faces);
        System.out.println(String.format("Detected %s faces", faces.toArray().length));
        for (Rect rect : faces.toArray()) {
            Point center = new Point(rect.x + rect.width * 0.5, rect.y + rect.height * 0.5);
            Core.ellipse(mRgba, center, new Size(rect.width * 0.5, rect.height * 0.5), 0, 0, 360, new Scalar(255, 0, 255), 4, 8, 0);
        }
        return mRgba;
    }
}
