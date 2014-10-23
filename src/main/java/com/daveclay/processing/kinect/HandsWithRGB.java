package com.daveclay.processing.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* --------------------------------------------------------------------------
 * SimpleOpenNI Hands3d Test
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect 2 library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / Zhdk / http://iad.zhdk.ch/
 * date:  12/12/2012 (m/d/y)
 * ----------------------------------------------------------------------------
 * This demos shows how to use the gesture/hand generator.
 * It's not the most reliable yet, a two hands example will follow
 * ----------------------------------------------------------------------------
 */

public class HandsWithRGB extends PApplet {

    public static void main(String[] args) {
        PApplet.main(HandsWithRGB.class.getName());
    }

    SimpleOpenNI context;
    float        zoomF = .4f;
    float        rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis,
    // the data from openni comes upside down
    float        rotY = radians(0);
    int          handVecListSize = 30;
    Map<Integer,ArrayList<PVector>> handPaths = new HashMap<Integer,ArrayList<PVector>>();
    int[]       userClr = new int[]{
            color(255,0,0),
            color(0,255,0),
            color(0,0,255),
            color(255,255,0),
            color(255,0,255),
            color(0,255,255)
    };
    public void setup()
    {

        //kinect = new SimpleOpenNI(this);
        context = new SimpleOpenNI(this);
        if(!context.isInit())
        {
            println("Can't init SimpleOpenNI, maybe the camera is not connected!");
            exit();
            return;
        }

        context.enableRGB();
        size(context.rgbWidth(), context.rgbHeight(), OPENGL);

        // disable mirror
        context.setMirror(true);

        // enable depthMap generation
        context.enableDepth();
        context.alternativeViewPointDepthToImage();

        // enable hands + gesture generation
        context.enableHand();
        context.startGesture(SimpleOpenNI.GESTURE_WAVE);
    }

    public void draw()
    {
        // update the cam
        context.update();

        background(0, 0, 0);
        image(context.rgbImage(), 0, 0);

        translate(width/2, height/2, 0);
        rotate(radians(180));
        scale(.4f);
        translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

        // draw the tracked hands
        if(handPaths.size() > 0)
        {
            for (Map.Entry<Integer, ArrayList<PVector>> handPathAndId : handPaths.entrySet()) {
                int handId = handPathAndId.getKey();
                ArrayList<PVector> handPaths = handPathAndId.getValue();

                pushStyle();
                int rgb = userClr[(handId - 1) % userClr.length];
                stroke(rgb);
                noFill();
                beginShape();
                for (PVector handPath : handPaths) {
                    vertex(handPath.x, handPath.y, handPath.z);
                }
                endShape();

                stroke(rgb);
                strokeWeight(4);
                PVector handPath = handPaths.get(0);

                translate(handPath.x, handPath.y, handPath.z);
                lights();
                sphere(10);

                popStyle();
            }
        }

        // draw the kinect cam
        // kinect.drawCamFrustum();
    }


// -----------------------------------------------------------------
// hand events

    public void onNewHand(SimpleOpenNI curContext,int handId,PVector pos)
    {
        println("onNewHand - handId: " + handId + ", pos: " + pos);

        ArrayList<PVector> vecList = new ArrayList<PVector>();
        vecList.add(pos);

        handPaths.put(handId, vecList);
    }

    public void onTrackedHand(SimpleOpenNI curContext,int handId,PVector pos)
    {
        // println("onTrackedHand - handId: " + handId + ", pos: " + pos );

        ArrayList<PVector> vecList = handPaths.get(handId);
        if(vecList != null)
        {
            vecList.add(0,pos);
            if(vecList.size() >= handVecListSize)
                // remove the last point
                vecList.remove(vecList.size()-1);
        }
    }

    public void onLostHand(SimpleOpenNI curContext,int handId)
    {
        println("onLostHand - handId: " + handId);

        handPaths.remove(handId);
    }

// -----------------------------------------------------------------
// gesture events

    public void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
    {
        println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);

        context.startTrackingHand(pos);

        int handId = context.startTrackingHand(pos);
        println("Gesture completed for hand " + handId);
    }
}
