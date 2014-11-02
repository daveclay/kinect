package com.daveclay.processing.gestures;

import com.daveclay.processing.gestures.utils.BoundingBox;
import com.daveclay.processing.gestures.utils.Centroid;
import com.daveclay.processing.gestures.utils.Score;

import java.util.List;

import static com.daveclay.processing.gestures.utils.Distance.findDistance;

public class SimpleGestureRecognizer {

    public RecognitionResult recognize(List<Point2D> points) {
        BoundingBox boundingBox = BoundingBox.find(points);
        Point2D centroid = Centroid.centroid(points);
        Point2D comparisonPoint = new Point2D(centroid.x, centroid.y);
        float distance = 0;
        float interval = boundingBox.width / points.size();
        for (int i = 0; i < points.size(); i++) {
            Point2D point = points.get(i);
            comparisonPoint.x = boundingBox.x + (interval * i);
            distance += findDistance(point, comparisonPoint);
        }

        float averageDistance = distance / points.size();
        float halfDiagonal = 0.5f * (float) Math.sqrt(
                Math.pow(boundingBox.width, 2) + Math.pow(boundingBox.height, 2));

        float score = Score.findScore(averageDistance, halfDiagonal);

        return new RecognitionResult("Line", score);
    }
}
