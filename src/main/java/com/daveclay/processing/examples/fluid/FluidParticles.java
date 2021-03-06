package com.daveclay.processing.examples.fluid;

import com.daveclay.processing.api.LogSketch;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImgProc;
import com.daveclay.processing.api.FloatValueMeasurement;
import processing.core.PApplet;

public class FluidParticles extends PApplet {

    public static void main(String[] args) {
        LogSketch logSketch = new LogSketch();
        SketchRunner.run(new FluidParticles(logSketch), logSketch);
    }


    private final LogSketch logSketch;
    FloatValueMeasurement f = new FloatValueMeasurement();

    NavierStokesSolver fluidSolver;
    double visc, diff, limitVelocity, vScale, velocityScale;
    int oldMouseX = 1, oldMouseY = 1;
    int numParticles;
    Particle[] particles;
    java.util.Random rnd = new java.util.Random();
    float cellHeight;
    float cellWidth;

    ImgProc imgProc;

    public FluidParticles(LogSketch logSketch) {
        this.logSketch = logSketch;
    }

    public void setup() {
        size(1200, 800);

        imgProc = new ImgProc(this);
        fluidSolver = new NavierStokesSolver();
        frameRate(60);

        numParticles = 50000;
        particles = new Particle[numParticles];
        visc = 0.0005f;
        diff = .25f;
        velocityScale = 16;

        limitVelocity = 200;
        colorMode(HSB, 255);

        /*
        stroke(color(0));
        fill(color(0));
        smooth();
        */

        cellHeight = height / NavierStokesSolver.N;
        cellWidth = width / NavierStokesSolver.N;
        imgProc.setupPixelFrames();
        background(color(0));
        initParticles();
    }

    private void initParticles() {
        for (int i = 0; i < numParticles; i++) {
            particles[i] = new Particle();
            particles[i].x = rnd.nextFloat() * width;
            particles[i].y = rnd.nextFloat() * height;
        }
    }

    public void draw() {
        imgProc.blur();
        imgProc.copyFrame();

        handleMouseMotion();

        double dt = 1 / frameRate;
        fluidSolver.tick(dt, visc, diff);

        /*
        stroke(color(64));
        paintGrid();
        stroke(color(96));
        paintMotionVector((float) vScale * 2);
        */

        vScale = velocityScale * 60 / frameRate;
        paintParticles();

        imgProc.draw();
    }

    private void paintParticles() {
        for (Particle p : particles) {
            p.update();
        }
    }

    private void handleMouseMotion() {
        mouseX = max(1, mouseX);
        mouseY = max(1, mouseY);

        int n = NavierStokesSolver.N;
        float cellHeight = height / n;
        float cellWidth = width / n;

        double mouseDx = mouseX - oldMouseX;
        double mouseDy = mouseY - oldMouseY;
        int cellX = floor(mouseX / cellWidth);
        int cellY = floor(mouseY / cellHeight);

        mouseDx = (abs((float) mouseDx) > limitVelocity) ? Math.signum(mouseDx) * limitVelocity : mouseDx;
        mouseDy = (abs((float) mouseDy) > limitVelocity) ? Math.signum(mouseDy) * limitVelocity : mouseDy;

        fluidSolver.applyForce(cellX, cellY, mouseDx, mouseDy);

        oldMouseX = mouseX;
        oldMouseY = mouseY;
    }

    private void paintMotionVector(float scale) {
        int n = NavierStokesSolver.N;
        float cellHeight = height / n;
        float cellWidth = width / n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                float dx = (float) fluidSolver.getDx(i, j);
                float dy = (float) fluidSolver.getDy(i, j);

                float x = cellWidth / 2 + cellWidth * i;
                float y = cellHeight / 2 + cellHeight * j;
                dx *= scale;
                dy *= scale;

                line(x, y, x + dx, y + dy);
            }
        }
    }

    private void paintGrid() {
        int n = NavierStokesSolver.N;
        float cellHeight = height / n;
        float cellWidth = width / n;
        for (int i = 1; i < n; i++) {
            line(0, cellHeight * i, width, cellHeight * i);
            line(cellWidth * i, 0, cellWidth * i, height);
        }
    }

    public class Particle {
        public float x;
        public float y;

        private float previousX;
        private float previousY;

        void update() {
            int n = NavierStokesSolver.N;

            int cellX = floor(this.x / cellWidth);
            int cellY = floor(this.y / cellHeight);
            float dx = (float) fluidSolver.getDx(cellX, cellY);
            float dy = (float) fluidSolver.getDy(cellX, cellY);

            float lX = this.x - cellX * cellWidth - cellWidth / 2;
            float lY = this.y - cellY * cellHeight - cellHeight / 2;

            int v, h, vf, hf;

            if (lX > 0) {
                v = Math.min(n, cellX + 1);
                vf = 1;
            } else {
                v = Math.min(n, cellX - 1);
                vf = -1;
            }

            if (lY > 0) {
                h = Math.min(n, cellY + 1);
                hf = 1;
            } else {
                h = Math.min(n, cellY - 1);
                hf = -1;
            }

            float dxv = (float) fluidSolver.getDx(v, cellY);
            float dxh = (float) fluidSolver.getDx(cellX, h);
            float dxvh = (float) fluidSolver.getDx(v, h);

            float dyv = (float) fluidSolver.getDy(v, cellY);
            float dyh = (float) fluidSolver.getDy(cellX, h);
            float dyvh = (float) fluidSolver.getDy(v, h);

            dx = lerp(lerp(dx, dxv, hf * lY / cellWidth), lerp(dxh, dxvh, hf * lY / cellWidth), vf * lX / cellHeight);

            dy = lerp(lerp(dy, dyv, hf * lY / cellWidth), lerp(dyh, dyvh, hf * lY / cellWidth), vf * lX / cellHeight);

            this.previousX = this.x;
            this.previousY = this.y;

            this.x += dx * vScale;
            this.y += dy * vScale;

            if (this.x < 0 || this.x >= width) {
                this.x = random(width);
            }
            if (this.y < 0 || this.y >= height) {
                this.y = random(height);
            }

            paint();
        }

        void paint() {
            float dx = Math.abs(previousX - this.x);
            float dy = Math.abs(previousY - this.y);

            if (Math.abs(dx + dy) > 50) {
                return;
            }

            float h = Math.abs(map(dx + dy, 0, 20, 160, 50)) % 200;
            float s = 40;
            float v = min(254, Math.abs(map(dx + dy, 0, 20, 0, 200)));

            int color = color(h, s, v);
            int px = (int) x;
            int py = (int) y;

            pixel(px, py, color);
        }

        void pixel(int x, int y, int color) {
            x = min(max(x, 0), width - 1);
            y = min(max(y, 0), height - 1);
            imgProc.pixel(x, y, color);
        }
    }
}
