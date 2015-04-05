package com.daveclay.processing.gestures;

import java.util.ArrayList;
import java.util.List;

public class AggregateGestureRecognizer implements GestureRecognizer {

    private List<GestureRecognizer> recognizers = new ArrayList<GestureRecognizer>();

    public void addRecognizer(GestureRecognizer gestureRecognizer) {
        this.recognizers.add(gestureRecognizer);
    }

    public RecognitionResult recognize(List<Point2D> points) {
        RecognitionResult bestResult = null;
        for (GestureRecognizer gestureRecognizer : recognizers) {
            // Todo: we could implement some sort of "decider filter" that would say, for example, if the point's
            // bounding box isn't thin, do not try the line GestureRecognizer.
            RecognitionResult result = gestureRecognizer.recognize(points);
            if (bestResult == null || result.score > bestResult.score) {
                bestResult = result;
            }
        }

        if (bestResult == null) {
            System.out.println("Couldn't find a good match.");
            return new RecognitionResult("Unknown", 1);
        }
        return bestResult;
    }
}
