package com.daveclay.processing.gestures;

import java.util.List;

public interface GestureRecognizer {
    public RecognitionResult recognize(List<Point2D> points);
}
