package com.daveclay.processing.gestures;

import processing.core.PVector;

import java.util.List;

public interface GestureRecognizer {
    public RecognitionResult recognize(List<PVector> points);
}
