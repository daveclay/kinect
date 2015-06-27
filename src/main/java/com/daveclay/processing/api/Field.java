package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Field {
    public static final float PI_OVER_180 = .0174532925f;

    public static void main(String[] args) {
        for (int degrees = 0; degrees <= 360; degrees++) {
            double radians = Math.toRadians(degrees);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            System.out.println(degrees + "\t" + radians + "\t\t" + sin + "\t\t" + cos);
        }
    }

    PApplet sketch;
    Cell[][] fieldCells;
    int cols;
    int rows;
    int resolution;
    float initialFieldStrengthX;
    float initialFieldStrengthY;
    float initNoise;

    class Cell {
        PVector force = new PVector();
        float xoff = sketch.noise(initNoise += 1) * 360;
        //float xoff = sketch.random(360);
        // initialFieldStrengthX;
        float yoff = initialFieldStrengthY;

        void calcTheta() {
            //float theta = sketch.noise(xoff, yoff) * 360;
            //float theta = sketch.random(360);
            float theta = xoff % 360;
            float radians = PApplet.radians(theta);
            force.set(PApplet.sin(radians), PApplet.cos(radians));
        }

        PVector get() {
            return force.get();
        }
    }

    public Field(int resolution, PApplet sketch) {
        this.sketch = sketch;
        this.resolution = resolution;
        initNoise = sketch.random(51233f);
        initialFieldStrengthX = sketch.random(53210);
        initialFieldStrengthY = sketch.random(76433);
        rows = sketch.height / resolution;
        cols = sketch.width / resolution;
        fieldCells = new Cell[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                fieldCells[x][y] = new Cell();
                initialFieldStrengthX += .2;
                initialFieldStrengthY += .2;
            }
        }
        initialize();
    }

    public void initialize() {
        float xoff = 1f;
        for (int x = 0; x < cols; x++) {
            float yoff = .2f;
            for (int y = 0; y < rows; y++) {
                fieldCells[x][y].calcTheta();
                fieldCells[x][y].yoff += yoff;
                fieldCells[x][y].xoff += xoff;
            }
        }
    }

    public PVector lookup(PVector location) {
        int xCell = (int) (location.x / resolution);
        int yCell = (int) (location.y / resolution);

        if (xCell < 0 || xCell > cols || yCell < 0 || yCell > rows) {
            return new PVector(0, 0);
        }

        int column = PApplet.constrain(xCell, 0, cols - 1);
        int row = PApplet.constrain(yCell, 0, rows - 1);

        return fieldCells[column][row].get();
    }

    public void draw(int color) {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Cell cell = fieldCells[col][row];
                int x = col * resolution;
                int y = row * resolution;

                int centerX = x + (resolution / 2);
                int centerY = y + (resolution / 2);

                float endX = centerX + (cell.force.x * (resolution / 2));
                float endY = centerY + (cell.force.y * (resolution / 2));

                sketch.stroke(sketch.color(60, 60, 60, 60));
                sketch.rect(x, y, resolution, resolution);

                sketch.noStroke();
                sketch.fill(sketch.color(0, 255, 0));
                sketch.ellipse(centerX, centerY, 2, 2);
                sketch.noFill();

                sketch.stroke(color);
                sketch.line(centerX, centerY, endX, endY);
                sketch.noFill();
            }
        }
    }
}
