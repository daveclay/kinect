package com.daveclay.processing.gestures.utils;

public class Score {

    public static float findScore(float distance, float max) {
        return 1f - (distance / max);
    }
}
