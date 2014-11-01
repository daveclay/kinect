package com.daveclay.processing.gestures;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported from:
 * http://www-users.cs.umn.edu/~wetzel/
 * http://depts.washington.edu/aimgroup/proj/dollar/others/cpp.bw.zip
 *
 * For a list of gesture diagrams:
 * https://depts.washington.edu/aimgroup/proj/dollar/
 */
public class GeometricRecognizer {

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
    private List<GestureTemplate> templates = new ArrayList<GestureTemplate>();

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
        halfDiagonal = 0.5f * (float) Math.sqrt((250f * 250f) + (250f * 250f));
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

    public RecognitionResult recognize(List<Point2D> points) {
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

        GestureTemplate bestMatchTemplate = null;

        //--- Check the shape passed in against every shape in our database
        for (GestureTemplate template : templates) {
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
        float score = 1f - (bestDistance / halfDiagonal);

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

    public List<GestureTemplate> getNormalizedTemplates() {
        return templates;
    }

    public void addTemplates(List<GestureTemplate> all) {
        for (GestureTemplate gestureTemplate : all) {
            addTemplate(gestureTemplate.name, gestureTemplate.points);
        }
    }

    public void addTemplate(String name, GestureTemplate gestureTemplate) {
        addTemplate(name, gestureTemplate.getPoints());
    }

    public void addTemplate(String name, List<Point2D> points) {
        points = normalizePath(points);
        templates.add(new GestureTemplate(name, points));
    }

    private Rectangle boundingBox(List<Point2D> points) {
        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (Point2D point : points) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        Rectangle bounds = new Rectangle(minX, minY, (maxX - minX), (maxY - minY));
        return bounds;
    }

    private Point2D centroid(List<Point2D> points) {
        float x = 0f, y = 0f;
        for (Point2D point : points) {
            x += point.x;
            y += point.y;
        }
        x /= points.size();
        y /= points.size();
        return new Point2D(x, y);
    }

    private float getDistance(Point2D p1, Point2D p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        float distance = (float) Math.sqrt((dx * dx) + (dy * dy));
        return distance;
    }

    private float distanceAtAngle(List<Point2D> points, GestureTemplate aTemplate, float rotation) {
        List<Point2D> newPoints = rotateBy(points, rotation);
        return pathDistance(newPoints, aTemplate.points);
    }

    private float distanceAtBestAngle(List<Point2D> points, GestureTemplate aTemplate) {
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

    private List<Point2D> normalizePath(List<Point2D> points) {
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
        points = translateToOrigin(points);

        return points;
    }

    private float pathDistance(List<Point2D> pts1, List<Point2D> pts2) {
        // assumes pts1.size == pts2.size

        float distance = 0f;
        for (int i = 0; i < pts1.size(); i++) {
            distance += getDistance(pts1.get(i), pts2.get(i));
        }
        return (distance / pts1.size());
    }

    private float pathLength(List<Point2D> points) {
        float distance = 0;
        for (int i = 1; i < points.size(); i++) {
            distance += getDistance(points.get(i - 1), points.get(i));
        }
        return distance;
    }

    private List<Point2D> resample(List<Point2D> points) {
        float interval = pathLength(points) / (numPointsInGesture - 1); // interval length
        float D = 0f;
        List<Point2D> newPoints = new ArrayList<Point2D>();

        //--- Store first point since we'll never resample it out of existence
        newPoints.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Point2D currentPoint = points.get(i);
            Point2D previousPoint = points.get(i - 1);
            float d = getDistance(previousPoint, currentPoint);
            if ((D + d) >= interval) {
                float qx = previousPoint.x + ((interval - D) / d) * (currentPoint.x - previousPoint.x);
                float qy = previousPoint.y + ((interval - D) / d) * (currentPoint.y - previousPoint.y);
                Point2D point = new Point2D(qx, qy);
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

    private List<Point2D> rotateBy(List<Point2D> points, float rotation) {
        Point2D c = centroid(points);
        //--- can't name cos; creates compiler error since VC++ can't
        //---  tell the difference between the variable and function
        float cosine = (float)Math.cos(rotation);
        float sine = (float) Math.sin(rotation);

        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            float qx = (point.x - c.x) * cosine - (point.y - c.y) * sine + c.x;
            float qy = (point.x - c.x) * sine + (point.y - c.y) * cosine + c.y;
            newPoints.add(new Point2D(qx, qy));
        }
        return newPoints;
    }

    private List<Point2D> rotateToZero(List<Point2D> points) {
        Point2D centroid = centroid(points);
        Point2D point2D = points.get(0);
        float rotation = (float)Math.atan2(centroid.y - point2D.y, centroid.x - point2D.x);
        return rotateBy(points, -rotation);
    }

    private List<Point2D> scaleToSquare(List<Point2D> points) {
        //--- Figure out the smallest box that can contain the path
        Rectangle box = boundingBox(points);
        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            //--- Scale the points to fit the main box
            //--- So if we wanted everything 100x100 and this was 50x50,
            //---  we'd multiply every point by 2
            float scaledX = point.x * (this.squareSize / box.width);
            float scaledY = point.y * (this.squareSize / box.height);
            //--- Why are we adding them to a new list rather than
            //---  just scaling them in-place?
            // TODO: try scaling in place (once you know this way works)
            newPoints.add(new Point2D(scaledX, scaledY));
        }
        return newPoints;
    }

    private void setRotationInvariance(boolean ignoreRotation) {
        shouldIgnoreRotation = ignoreRotation;

        if (shouldIgnoreRotation) {
            angleRange = 45f;
        } else {
            angleRange = 15f;
        }
    }

    /**
     * Shift the points so that the center is at 0,0.
     * That way, if everyone centers at the same place, we can measure
     * the distance between each pair of points without worrying about
     * where each point was originally drawn
     * If we didn't do this, shapes drawn at the top of the screen
     * would have a hard time matching shapes drawn at the bottom
     * of the screen
     */
    private List<Point2D> translateToOrigin(List<Point2D> points) {
        Point2D c = centroid(points);
        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            float qx = point.x - c.x;
            float qy = point.y - c.y;
            newPoints.add(new Point2D(qx, qy));
        }
        return newPoints;
    }

    private static class Rectangle {
        float x, y, width, height;

        Rectangle(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public static class PathWriter {

        String fileName = "savedPath.txt";
        String gestureName = "DefaultName";

        public static boolean writeToFile(List<Point2D> path, String fileName, String gestureName) {
        /*
            fstream file(fileName.c_str(), ios::out);

            file << "List<Point2D> getGesture" << gestureName << "()" << endl;
            file << "{" << endl;
            file << "\t" << "List<Point2D> path;" << endl;
            
            List<Point2D>::const_iterator i;
            for (i = path.begin(); i != path.end(); i++)
            {
                Point2D point = *i;
                file << "\t" << "path.add(Point2D(" << point.x << ","
                     << point.y << "));" << endl;
            }

            file << endl;
            file << "\t" << "return path;" << endl;
            file << "}" << endl;

            file.close();

            */
            return true;
        }
    }
}
