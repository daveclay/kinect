package com.daveclay.opencv.examples;

/*
  * Captures the camera stream with OpenCV
  * Search for the faces
  * Display a circle around the faces using Java
  */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import static com.daveclay.opencv.OpenCVConstants.*;

public class FacialRecognitionVideoStream {

    public static void main(String arg[]){
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String window_name = "Capture - Face detection";
        JFrame frame = new JFrame(window_name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        processor my_processor=new processor();
        My_Panel my_panel = new My_Panel();
        frame.setContentPane(my_panel);
        frame.setVisible(true);
        //-- 2. Read the video stream
        Mat webcam_image=new Mat();
        VideoCapture capture =new VideoCapture(0);
        if( capture.isOpened())
        {
            while( true )
            {
                capture.read(webcam_image);
                if( !webcam_image.empty() )
                {
                    frame.setSize(webcam_image.width()+40,webcam_image.height()+60);
                    //-- 3. Apply the classifier to the captured image
                    webcam_image=my_processor.detect(webcam_image);
                    //-- 4. Display the image
                    my_panel.matToBufferedImage(webcam_image); // We could look at the error...
                    my_panel.repaint();
                }
                else
                {
                    System.out.println(" --(!) No captured frame -- Break!");
                    break;
                }
            }
        }
    }

    static class My_Panel extends JPanel{
        private static final long serialVersionUID = 1L;
        private BufferedImage image;
        // Create a constructor method
        public My_Panel(){
            super();
        }
        /*
         * Converts/writes a Mat into a BufferedImage.
         *
         * @param matrix Mat of type CV_8UC3 or CV_8UC1
         * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
         */
        public boolean matToBufferedImage(Mat matrix) {
            MatOfByte mb=new MatOfByte();
            Highgui.imencode(".jpg", matrix, mb);
            try {
                this.image = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false; // Error
            }
            return true; // Successful
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (this.image==null) return;
            g.drawImage(this.image,10,10,this.image.getWidth(),this.image.getHeight(), null);
            //g.drawString("This is my custom Panel!",10,20);
        }
    }
    public static class processor {
        private CascadeClassifier face_cascade;
        // Create a constructor method
        public processor(){
            face_cascade=new CascadeClassifier(HAAR_CASCADES_PATH);
            if(face_cascade.empty())
            {
                System.out.println("--(!)Error loading A\n");
                return;
            }
            else
            {
                System.out.println("Face classifier loooaaaaaded up");
            }
        }
        public Mat detect(Mat inputframe){
            Mat mRgba=new Mat();
            Mat mGrey=new Mat();
            MatOfRect faces = new MatOfRect();
            inputframe.copyTo(mRgba);
            inputframe.copyTo(mGrey);
            Imgproc.cvtColor( mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist( mGrey, mGrey );
            face_cascade.detectMultiScale(mGrey, faces);
            System.out.println(String.format("Detected %s faces", faces.toArray().length));
            for(Rect rect:faces.toArray())
            {
                Point center= new Point(rect.x + rect.width*0.5, rect.y + rect.height*0.5 );
                Core.ellipse( mRgba, center, new Size( rect.width*0.5, rect.height*0.5), 0, 0, 360, new Scalar( 255, 0, 255 ), 4, 8, 0 );
            }
            return mRgba;
        }
    }
}

