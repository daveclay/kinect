package com.daveclay.processing.openprocessing;

import com.daveclay.processing.api.*;
import peasy.PeasyCam;
import processing.core.PApplet;

import java.util.stream.Collector;
import java.util.stream.IntStream;

public class CPUDesigner extends PApplet {

    private PeasyCam cam;

    public static void main(String[] args) {
        SketchRunner.run(new CPUDesigner());
    }

    Noise2D snoise = new Noise2D(this, .00001f);
    HSVNoiseColor cnoise = new HSVNoiseColor(this, .0001f);
    int patchSize;

    public void setup() {
        size(1400, 900, P3D);
        background(0);
        patchSize = width/6;
        cam = new PeasyCam(this, 200);
        cam.setFreeRotationMode();
    }

    public void draw() {
        cnoise.sNoise.setScale(snoise.next());
        patchSize = width/((int)random(10) + 1);
        IntStream patchSeq = createPatchSequence(patchSize);
        final int[] last = new int[] { 0 };
        patchSeq.forEach((i) -> {
            if (i != 0) {
                int color = cnoise.nextColor(70);
                fill(color);
                stroke(0, 20);
                int o = last[0];
                int size = i - o;
                pushMatrix();
                translate((width / 2) - random(width), (height / 2) - random(height), -1 * random(1000) - 500);
                box(size);
                fill(0, 200);
                noStroke();
                text("C" + i, size + 2, 0);
                popMatrix();
            }
            last[0] = i;
        });
        //text(patchSeq.boxed().collect(intsToString()), 20, 20);
    }

    public void lines() {
        for(int i = 0; i < width*height; i++) {
            // int x = (int) xnoise.next() / width;
            // int y = (int) ynoise.next() % height;
            set(i / width, i % height, cnoise.nextColor(255));
        }
        //image(canvas, 0, 0);
        text(frameRate, 20, 20);
    }

    public IntStream createPatchSequence(int patchSize) {
        int x = 0;
        IntStream.Builder builder = IntStream.builder();
        builder.add(x);
        while (true) {
            x += random(patchSize);
            if (x > width) {
                x = width;
            }
            builder.add(x);
            if (x == width) {
                break;
            }
        }
        return builder.build();
    }

    public static Collector<Integer, ?, String> intsToString() {
        return Collector.of(
                StringBuilder::new,
                (stringBuilder, i) -> {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(i);
                },
                StringBuilder::append,
                StringBuilder::toString);
    }
}
