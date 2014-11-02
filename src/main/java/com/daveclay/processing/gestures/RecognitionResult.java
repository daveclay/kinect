package com.daveclay.processing.gestures;

public class RecognitionResult {

    public String name;
    public double score;

    public RecognitionResult(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public int getScorePercent() {
        return (int) Math.round(this.score * 100);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
