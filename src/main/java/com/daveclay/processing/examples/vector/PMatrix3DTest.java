package com.daveclay.processing.examples.vector;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class PMatrix3DTest extends PApplet {

    public static void main(String[] args) {
        PApplet.main(PMatrix3DTest.class.getName());
    }

    private static final boolean moveCamera = true;

    private StringConstrainer stringConstrainer = new StringConstrainer();

    public void setup() {
        size(800, 800, P3D); //OPENGL);
    }


    public static class HUD {

        private PApplet parent;
        private int x;
        private int y;

        public HUD(PApplet parent) {
            this(parent, 10, 10);
        }

        public HUD(PApplet parent, int x, int y) {
            this.parent = parent;
            this.x = x;
            this.y = y;
        }

        public void addLine(String line) {

        }
    }

    public static class StringConstrainer {
        private int max = 6;

        public void setMaxStringLength(int max) {
            this.max = max;
        }

        public String constrain(String text) {
            return text.substring(0, max);
        }
    }
}
