package com.daveclay.processing.gestures;

import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

import java.util.List;

public interface GestureRecognizer {
    RecognitionResult recognize(List<PVector> points);

    class Factory {
        public static AggregateGestureRecognizer defaultInstance(GestureDataStore gestureDataStore) {
            AggregateGestureRecognizer gestureRecognizer = new AggregateGestureRecognizer();

            GeometricRecognizer geometricRecognizer = new GeometricRecognizer();
            geometricRecognizer.addTemplate("Circle", gestureDataStore.getPointsByName("Circle"));
            geometricRecognizer.addTemplate("Circle", VectorMath.reflectVertically(gestureDataStore.getPointsByName("Circle")));

            LineGestureRecognizer lineGestureRecognizer = new LineGestureRecognizer();
            lineGestureRecognizer.addRecognizerAlgorithm("LeftToRightLine", LineGestureRecognizer.LEFT_TO_RIGHT_LINE_RECOGNIZER);
            lineGestureRecognizer.addRecognizerAlgorithm("RightToLeftLine", LineGestureRecognizer.RIGHT_TO_LEFT_LINE_RECOGNIZER);
            lineGestureRecognizer.addRecognizerAlgorithm("BottomToTopLine", LineGestureRecognizer.BOTTOM_TO_TOP_LINE_RECOGNIZER);
            lineGestureRecognizer.addRecognizerAlgorithm("TopToBottomLine", LineGestureRecognizer.TOP_TO_BOTTOM_LINE_RECOGNIZER);

            gestureRecognizer.addRecognizer(geometricRecognizer);
            gestureRecognizer.addRecognizer(lineGestureRecognizer);

            return gestureRecognizer;
        }
    }
}
