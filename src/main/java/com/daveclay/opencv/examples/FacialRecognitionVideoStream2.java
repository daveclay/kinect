package com.daveclay.opencv.examples;

/*
  * Captures the camera stream with OpenCV
  * Search for the faces
  * Display a circle around the faces using Java
  */

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.daveclay.opencv.OpenCVConstants.*;
import static com.daveclay.opencv.OpenCVImageConverter.*;

public class FacialRecognitionVideoStream2 {

    private JFrame frame;
    private VideoCapture capture;
    private HaarCascadeFacialRecognitionProcessor processor = new HaarCascadeFacialRecognitionProcessor();
    private VideoStreamRecognitionPanel panel = new VideoStreamRecognitionPanel();
    private Mat sourceImage = new Mat();

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                captureAndProcess();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static void main(String arg[]) throws IOException {
        loadOpenCVNativeLibrary();
        FacialRecognitionVideoStream2 facialRecognitionVideoStream2 = new FacialRecognitionVideoStream2();
        facialRecognitionVideoStream2.start();
    }

    public void start() throws IOException {

        String window_name = "CaptureExample - Face detection";
        frame = new JFrame(window_name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setContentPane(panel);
        frame.setVisible(true);

        //-- 2. Read the video stream
        capture = new VideoCapture(0);
        if (capture.isOpened()) {
            timer.scheduleAtFixedRate(task, 0, 1000 / 30);
        }
    }

    private void captureAndProcess() throws IOException {
        capture.read(sourceImage);
        if (!sourceImage.empty()) {
            frame.setSize(sourceImage.width() + 40, sourceImage.height() + 60);
            sourceImage = processor.detect(sourceImage);
            BufferedImage image = toBufferedImage(sourceImage);
            panel.setImage(image);
            panel.repaint();
        } else {
            System.out.println(" --(!) No captured frame -- Break!");
        }
    }
}

