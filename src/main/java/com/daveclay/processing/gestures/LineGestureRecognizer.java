package com.daveclay.processing.gestures;

import com.daveclay.processing.gestures.utils.BoundingBox;
import com.daveclay.processing.gestures.utils.Centroid;
import com.daveclay.processing.gestures.utils.Rotate;
import com.daveclay.processing.gestures.utils.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.daveclay.processing.gestures.utils.Distance.findDistance;

public class LineGestureRecognizer implements GestureRecognizer {

    public static LeftToRightLineRecognizer LEFT_TO_RIGHT_LINE_RECOGNIZER = new LeftToRightLineRecognizer();
    public static RightToLeftLineRecognizer RIGHT_TO_LEFT_LINE_RECOGNIZER = new RightToLeftLineRecognizer();
    public static BottomToTopLineRecognizer BOTTOM_TO_TOP_LINE_RECOGNIZER = new BottomToTopLineRecognizer();
    public static TopToBottomLineRecognizer TOP_TO_BOTTOM_LINE_RECOGNIZER = new TopToBottomLineRecognizer();

    private Map<String, RecognizerAlgorithm> recognizerAlgorithmsByName = new HashMap<String, RecognizerAlgorithm>();

    public RecognitionResult recognize(GestureData gestureData) {
        return recognize(gestureData.getPoints());
    }

    public RecognitionResult recognize(List<Point2D> points) {
        float bestScore = 0;
        String bestName = null;
        for (Map.Entry<String, RecognizerAlgorithm> recognizerAlgorithmEntry : recognizerAlgorithmsByName.entrySet()) {
            RecognizerAlgorithm recognizerAlgorithm = recognizerAlgorithmEntry.getValue();
            float score = recognizerAlgorithm.recognize(points);
            if (score > bestScore) {
                bestScore = score;
                bestName = recognizerAlgorithmEntry.getKey();
            }
        }

        return new RecognitionResult(bestName, bestScore);
    }

    public void addRecognizerAlgorithm(String name, RecognizerAlgorithm recognizerAlgorithm) {
        this.recognizerAlgorithmsByName.put(name, recognizerAlgorithm);
    }

    public static interface RecognizerAlgorithm {
        public float recognize(List<Point2D> points);
    }

    public abstract static class LineRecognizerAlgorithm implements  RecognizerAlgorithm {

        public float recognize(List<Point2D> points) {

            points = rotatePoints(points);

            Point2D centroid = Centroid.centroid(points);
            BoundingBox boundingBox = BoundingBox.find(points);
            Point2D comparisonPoint = new Point2D(centroid.x, centroid.y);
            float distance = 0;
            float interval = boundingBox.width / points.size();
            float startX = getStartPoint(boundingBox);
            for (int i = 0; i < points.size(); i++) {
                Point2D point = points.get(i);
                comparisonPoint.x = getComparisonPoint(startX, (interval * i));
                distance += findDistance(point, comparisonPoint);
            }

            float averageDistance = distance / points.size();
            float maxDistanceDiagonal = (float) Math.sqrt(
                    Math.pow(boundingBox.width, 2) + Math.pow(boundingBox.height / 2, 2));

            float score = Score.findScore(averageDistance, maxDistanceDiagonal);

            // add the bounding box shape to the score?
            // That would not take into account the number of points. If you had two or three
            // outliers, they'd skew the bounding box.
            // furthest shape from a line is a square, closes is a 0-height line.

            return score;
        }

        protected List<Point2D> rotatePoints(List<Point2D> points) {
            return points;
        }

        abstract float getStartPoint(BoundingBox boundingBox);

        abstract float getComparisonPoint(float startX, float v);

    }

    public static class RightToLeftLineRecognizer extends LineRecognizerAlgorithm {
        float getStartPoint(BoundingBox boundingBox) {
            // By starting from the BoundingBox's x coordinate, we're testing the left-to-right line direction
            return boundingBox.x;
        }

        float getComparisonPoint(float startX, float amount) {
            return startX + amount;
        }
    }

    public static class LeftToRightLineRecognizer extends LineRecognizerAlgorithm {

        float getStartPoint(BoundingBox boundingBox) {
            return boundingBox.x + boundingBox.width;
        }

        float getComparisonPoint(float startX, float amount) {
            return startX - amount;
        }
    }

    public static class BottomToTopLineRecognizer extends RightToLeftLineRecognizer {

        @Override
        protected List<Point2D> rotatePoints(List<Point2D> points) {
            return Rotate.rotateBy(points, -90);
        }
    }

    public static class TopToBottomLineRecognizer extends LeftToRightLineRecognizer {

        @Override
        protected List<Point2D> rotatePoints(List<Point2D> points) {
            return Rotate.rotateBy(points, -90);
        }
    }
}
