package com.daveclay.processing.gestures;

public class RecognitionResult {

    public String name;
    public double score;

    public RecognitionResult(String name, double score) {
        this.name = name;
        this.score = score;
    }
}
