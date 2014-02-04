package com.daveclay.processing.gestures;

public class RecognitionResult {
    String name;
    double score;

    RecognitionResult(String name, double score) {
        this.name = name;
        this.score = score;
    }
}
