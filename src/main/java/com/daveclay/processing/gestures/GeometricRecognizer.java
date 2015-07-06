package com.daveclay.processing.gestures;

import com.daveclay.processing.gestures.utils.BoundingBox;
import com.daveclay.processing.gestures.utils.Centroid;
import com.daveclay.processing.gestures.utils.Score;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static com.daveclay.processing.gestures.utils.Distance.*;
import static com.daveclay.processing.gestures.utils.Rotate.*;

/**
 * Ported from:
 * http://www-users.cs.umn.edu/~wetzel/
 * http://depts.washington.edu/aimgroup/proj/dollar/others/cpp.bw.zip
 *
 * For a list of gesture diagrams:
 * https://depts.washington.edu/aimgroup/proj/dollar/
 */
public class GeometricRecognizer implements GestureRecognizer {

    public static void main(String[] args) {
        GestureDataStore gestureDataStore = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        GeometricRecognizer recognizer = new GeometricRecognizer();
        gestureDataStore.load();
        recognizer.addTemplate("LineTest", gestureDataStore.getPointsByName("LeftToRightLine"));
    }

    private float halfDiagonal;
    private float angleRange;
    private float anglePrecision;
    private float goldenRatio;

    //--- How many points we use to define a shape
    private int numPointsInGesture;
    //---- Square we resize the shapes to
    private int squareSize;

    private boolean shouldIgnoreRotation;

    //--- What we match the input shape against
    private List<GestureData> templates = new ArrayList<GestureData>();

    public GeometricRecognizer() {
        //--- How many templates do we have to compare the user's gesture against?
        //--- Can get ~97% accuracy with just one template per symbol to recognize
        //numTemplates = 16;
        //--- How many points do we use to represent a gesture
        //--- Best results between 32-256
        numPointsInGesture = 128;
        //--- Before matching, we stretch the symbol across a square
        //--- That way we don't have to worry about the symbol the user drew
        //---  being smaller or larger than the one in the template
        squareSize = 250;
        //--- 1/2 max distance across a square, which is the maximum distance
        //---  a point can be from the center of the gesture
        halfDiagonal = 0.5f * (float) Math.sqrt((squareSize * squareSize) + (squareSize * squareSize));
        //--- Before matching, we rotate the symbol the user drew so that the
        //---  start point is at degree 0 (right side of symbol). That's how
        //---  the templates are rotated so it makes matching easier
        //--- Note: this assumes we want symbols to be rotation-invariant,
        //---  which we might not want. Using this, we can't tell the difference
        //---  between squares and diamonds (which is just a rotated square)
        setRotationInvariance(false);
        anglePrecision = 2f;
        //--- A magic number used in pre-processing the symbols
        goldenRatio = 0.5f * (-1f + (float)Math.sqrt(5f));
    }

    public RecognitionResult recognize(List<PVector> points) {
        //--- Make sure we have some templates to compare this to
        //---  or else recognition will be impossible
        if (templates.isEmpty()) {
            System.out.println("No templates loaded so no symbols to match.");
            return new RecognitionResult("Unknown", -1d);
        }

        points = normalizePath(points);

        //--- Initialize best distance to the largest possible number
        //--- That way everything will be better than that
        float bestDistance = Float.MAX_VALUE;

        GestureData bestMatchTemplate = null;

        //--- Check the shape passed in against every shape in our database
        for (GestureData template : templates) {
            //--- Calculate the total distance of each point in the passed in
            //---  shape against the corresponding point in the template
            //--- We'll rotate the shape a few degrees in each direction to
            //---  see if that produces a better match
            float distance = distanceAtBestAngle(points, template);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatchTemplate = template;
            }
        }

        //--- Turn the distance into a percentage by dividing it by
        //---  half the maximum possible distance (across the diagonal
        //---  of the square we scaled everything too)
        //--- Distance = hwo different they are
        //--- Subtract that from 1 (100%) to get the similarity
        float score = Score.findScore(bestDistance, halfDiagonal);

        //--- Make sure we actually found a good match
        //--- Sometimes we don't, like when the user doesn't draw enough points
        if (bestMatchTemplate == null) {
            System.out.println("Couldn't find a good match.");
            return new RecognitionResult("Unknown", 1);
        }

        return new RecognitionResult(bestMatchTemplate.name, score);
    }

    public void loadDefaultGestures() {
    }

    public List<GestureData> getNormalizedTemplates() {
        return templates;
    }

    public void addTemplates(List<GestureData> all) {
        for (GestureData gestureData : all) {
            addTemplate(gestureData.name, gestureData.points);
        }
    }

    public void addTemplate(String name, GestureData gestureData) {
        addTemplate(name, gestureData.getPoints());
    }

    public void addTemplate(String name, List<PVector> points) {
        points = normalizePath(points);
        templates.add(new GestureData(name, points));
    }

    private float distanceAtAngle(List<PVector> points, GestureData aTemplate, float rotation) {
        List<PVector> newPoints = rotateBy(points, rotation);
        return pathDistance(newPoints, aTemplate.points);
    }

    private float distanceAtBestAngle(List<PVector> points, GestureData aTemplate) {
        float startRange = -angleRange;
        float endRange = angleRange;
        float x1 = goldenRatio * startRange + (1f - goldenRatio) * endRange;
        float f1 = distanceAtAngle(points, aTemplate, x1);
        float x2 = (1f - goldenRatio) * startRange + goldenRatio * endRange;
        float f2 = distanceAtAngle(points, aTemplate, x2);
        while (Math.abs(endRange - startRange) > anglePrecision) {
            if (f1 < f2) {
                endRange = x2;
                x2 = x1;
                f2 = f1;
                x1 = goldenRatio * startRange + (1f - goldenRatio) * endRange;
                f1 = distanceAtAngle(points, aTemplate, x1);
            } else {
                startRange = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1f - goldenRatio) * startRange + goldenRatio * endRange;
                f2 = distanceAtAngle(points, aTemplate, x2);
            }
        }
        return Math.min(f1, f2);
    }

    private List<PVector> normalizePath(List<PVector> points) {
        /* Recognition algorithm from
            http://faculty.washington.edu/wobbrock/pubs/uist-07.1.pdf
            Step 1: Resample the Point Path
            Step 2: Rotate Once Based on the "Indicative Angle"
            Step 3: Scale and Translate
            Step 4: Find the Optimal Angle for the Best Score
        */
        // TODO: Switch to $N algorithm so can handle 1D shapes

        //--- Make everyone have the same number of points (anchor points)
        points = resample(points);
        //--- Pretend that all gestures began moving from right hand side
        //---  (degree 0). Makes matching two items easier if they're
        //---  rotated the same
        if (shouldIgnoreRotation) {
            points = rotateToZero(points);
        }
        //--- Pretend all shapes are the same size.
        //--- Note that since this is a square, our new shape probably
        //---  won't be the same aspect ratio
        points = scaleToSquare(points);
        //--- Move the shape until its center is at 0,0 so that everyone
        //---  is in the same coordinate system
        points = Centroid.translateToOrigin(points);

        return points;
    }

    private float pathLength(List<PVector> points) {
        float distance = 0;
        for (int i = 1; i < points.size(); i++) {
            distance += findDistance(points.get(i - 1), points.get(i));
        }
        return distance;
    }

    private List<PVector> resample(List<PVector> points) {
        float interval = pathLength(points) / (numPointsInGesture - 1); // interval length
        float D = 0f;
        List<PVector> newPoints = new ArrayList<PVector>();

        //--- Store first point since we'll never resample it out of existence
        newPoints.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            PVector currentPoint = points.get(i);
            PVector previousPoint = points.get(i - 1);
            float d = findDistance(previousPoint, currentPoint);
            if ((D + d) >= interval) {
                float qx = previousPoint.x + ((interval - D) / d) * (currentPoint.x - previousPoint.x);
                float qy = previousPoint.y + ((interval - D) / d) * (currentPoint.y - previousPoint.y);
                PVector point = new PVector(qx, qy);
                newPoints.add(point);
                points.add(i, point);
                D = 0f;
            } else {
                D += d;
            }
        }

        // somtimes we fall a rounding-error short of adding the last point, so add it if so
        if (newPoints.size() == (numPointsInGesture - 1)) {
            newPoints.add(points.get(points.size() - 1));
        }

        return newPoints;
    }

    /**
     * This probably becomes a problem for line gestures. The incoming actual gesture contains
     * a lot of noise, and scaling the line to a box amplifies that noise.
     */
    private List<PVector> scaleToSquare(List<PVector> points) {
        //--- Figure out the smallest box that can contain the path
        BoundingBox box = BoundingBox.find(points);
        if (box.width == 0) {
            box.width = this.squareSize;
        }
        if (box.height == 0) {
            box.height = this.squareSize;
        }
        List<PVector> newPoints = new ArrayList<>();
        for (PVector point : points) {
            //--- Scale the points to fit the main box
            //--- So if we wanted everything 100x100 and this was 50x50,
            //---  we'd multiply every point by 2
            float scaledX = point.x * (this.squareSize / box.width);
            float scaledY = point.y * (this.squareSize / box.height);
            //--- Why are we adding them to a new list rather than
            //---  just scaling them in-place?
            // TODO: try scaling in place (once you know this way works)
            newPoints.add(new PVector(scaledX, scaledY));
        }
        return newPoints;
    }

    private void setRotationInvariance(boolean ignoreRotation) {
        shouldIgnoreRotation = ignoreRotation;

        if (shouldIgnoreRotation) {
            angleRange = 45f;
        } else {
            angleRange = 10f;
        }
    }

}
