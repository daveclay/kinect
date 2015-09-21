package com.daveclay.processing.animation;

import com.daveclay.processing.CoordinateSystem;
import com.daveclay.processing.api.SketchRunner;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class Presentation extends CoordinateSystem {

    public static void main(String[] args) {
        PApplet.main(new String[] { "--display=1", Presentation.class.getName() });
        PApplet.main(new String[] { "--display=0", Manager.class.getName() });
    }

    @Override
    public boolean sketchFullScreen() {
        return true;
    }

    public void setup() {
        size(displayWidth, displayHeight, OPENGL);
        super.setup();
    }

    public class Sprite {

        public void draw() {
            fill(color(255, 255, 255));
            box(100);
        }
    }

    public static class Manager extends CoordinateSystem {
        public static void main(String[] args) {
            SketchRunner.run(new Manager());
        }

        public void setup() {
            size(1024, 768, OPENGL);
            super.setup();
        }
    }
}
