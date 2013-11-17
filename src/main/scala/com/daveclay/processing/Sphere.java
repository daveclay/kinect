package com.daveclay.processing;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Sphere { // { extends PApplet {

    // Sphere Variables
    private float radius = 66;
    int xDetail = 40;
    int yDetail = 30;
    float[] xGrid = new float[xDetail+1];
    float[] yGrid = new float[yDetail+1];
    float[][][] allPoints = new float[xDetail+1][yDetail+1][3];

    PImage textureMapImage = null;

    public Sphere(PApplet processing, PImage texmap){
        processing.noStroke();
        this.textureMapImage = texmap;

        // Create a 2D grid of standardized mercator coordinates
        for(int i = 0; i <= xDetail; i++){
            xGrid[i]= i / (float) xDetail;
        }
        for(int i = 0; i <= yDetail; i++){
            yGrid[i]= i / (float) yDetail;
        }

        processing.textureMode(PConstants.NORMAL);
    }

    float[] mercatorPoint(float R, float x, float y){

        float[] thisPoint = new float[3];
        float phi = x*2* PConstants.PI;
        float theta = PConstants.PI - y* PConstants.PI;

        thisPoint[0] = R* PApplet.sin(theta)* PApplet.cos(phi);
        thisPoint[1] = R* PApplet.sin(theta)* PApplet.sin(phi);
        thisPoint[2] = R* PApplet.cos(theta);

        return thisPoint;
    }

    public void radius(float radius) {
        this.radius = radius;
    }

    public void drawSphere(PApplet processing){

        // Transform the 2D grid into a grid of points on the sphere, using the inverse mercator projection
        for(int i = 0; i <= xDetail; i++){
            for(int j = 0; j <= yDetail; j++){
                allPoints[i][j] = mercatorPoint(radius, xGrid[i], yGrid[j]);
            }
        }


        for(int j = 0; j < yDetail; j++){
            processing.beginShape(PConstants.TRIANGLE_STRIP);
            processing.texture(textureMapImage);
            for(int i = 0; i <= xDetail; i++){
                processing.vertex(allPoints[i][j + 1][0], allPoints[i][j + 1][1], allPoints[i][j + 1][2], xGrid[i], yGrid[j + 1]);
                processing.vertex(allPoints[i][j][0], allPoints[i][j][1], allPoints[i][j][2], xGrid[i], yGrid[j]);
            }
            processing.endShape(PConstants.CLOSE);
        }
    }

}
