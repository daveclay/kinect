package com.daveclay.processing.api;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Field {
    PApplet sketch;
    Cell[][] fieldCells;
    int cols;
    int rows;
    int resolution;
    float initialFieldStrengthX = 0;
    float initialFieldStrengthY = 0;

    class Cell {
        PVector force;
        float theta;
        float xoff = initialFieldStrengthX;
        float yoff = initialFieldStrengthY;

        void calcTheta() {
            float noise = sketch.noise(xoff, yoff);
            theta = PApplet.map(noise, 0, 1, 0, PConstants.TWO_PI);
            force = new PVector(PApplet.cos(theta), PApplet.sin(theta));
            yoff += 0.01;
            xoff += 0.01;
        }

        PVector get() {
            return force.get();
        }
    }

    public Field(int resolution, PApplet sketch) {
        this.sketch = sketch;
        this.resolution = resolution;
        rows = sketch.height / resolution;
        cols = sketch.width / resolution;
        fieldCells = new Cell[cols][rows];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                fieldCells[x][y] = new Cell();
                initialFieldStrengthX += .05;
                initialFieldStrengthY += .05;
            }
        }
        initialize();
    }

    public void initialize() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                fieldCells[x][y].calcTheta();
            }
        }
    }

    public PVector lookup(PVector location) {
        int column = (int) PApplet.constrain(location.x / resolution, 0, cols - 1);
        int row = (int) PApplet.constrain(location.y / resolution, 0, rows - 1);
        return fieldCells[column][row].get();
    }

    public void draw() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Cell cell = fieldCells[col][row];
                int x = col * resolution;
                int y = row * resolution;
                sketch.stroke(sketch.color(60, 60, 40));
                sketch.rect(x, y, resolution, resolution);

                int centerX = x + (resolution / 2);
                int centerY = y + (resolution / 2);

                sketch.stroke(sketch.color(120, 0, 0));
                float endX = centerX + cell.force.x * (resolution / 2);
                float endY = centerY + cell.force.y * (resolution / 2);
                sketch.line(centerX, centerY, endX, endY);
            }
        }
    }
}
