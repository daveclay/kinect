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

    private double halfDiagonal;
    private double angleRange;
    private double anglePrecision;
    private double goldenRatio;

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
        halfDiagonal = 0.5 * Math.sqrt((250.0 * 250.0) + (250.0 * 250.0));
        //--- Before matching, we rotate the symbol the user drew so that the
        //---  start point is at degree 0 (right side of symbol). That's how
        //---  the templates are rotated so it makes matching easier
        //--- Note: this assumes we want symbols to be rotation-invariant,
        //---  which we might not want. Using this, we can't tell the difference
        //---  between squares and diamonds (which is just a rotated square)
        setRotationInvariance(false);
        anglePrecision = 2.0;
        //--- A magic number used in pre-processing the symbols
        goldenRatio = 0.5 * (-1.0 + Math.sqrt(5.0));
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
        double bestDistance = Double.MAX_VALUE;

        GestureTemplate bestMatchTemplate = null;

        //--- Check the shape passed in against every shape in our database
        for (GestureTemplate template : templates) {
            //--- Calculate the total distance of each point in the passed in
            //---  shape against the corresponding point in the template
            //--- We'll rotate the shape a few degrees in each direction to
            //---  see if that produces a better match
            double distance = distanceAtBestAngle(points, template);
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
        double score = 1.0 - (bestDistance / halfDiagonal);

        //--- Make sure we actually found a good match
        //--- Sometimes we don't, like when the user doesn't draw enough points
        if (bestMatchTemplate == null) {
            System.out.println("Couldn't find a good match.");
            return new RecognitionResult("Unknown", 1);
        }

        return new RecognitionResult(bestMatchTemplate.name, score);
    }

    public void loadDefaultGestures() {
        for (GestureTemplate gestureTemplate : GestureData.getAll()) {
            addTemplate(gestureTemplate.name, gestureTemplate.points);
        }
    }

    public List<GestureTemplate> getNormalizedTemplates() {
        return templates;
    }

    public void addTemplate(String name, List<Point2D> points) {
        points = normalizePath(points);
        templates.add(new GestureTemplate(name, points));
    }

    private Rectangle boundingBox(List<Point2D> points) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

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
        double x = 0.0, y = 0.0;
        for (Point2D point : points) {
            x += point.x;
            y += point.y;
        }
        x /= points.size();
        y /= points.size();
        return new Point2D(x, y);
    }

    private double getDistance(Point2D p1, Point2D p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double distance = Math.sqrt((dx * dx) + (dy * dy));
        return distance;
    }

    private double distanceAtAngle(List<Point2D> points, GestureTemplate aTemplate, double rotation) {
        List<Point2D> newPoints = rotateBy(points, rotation);
        return pathDistance(newPoints, aTemplate.points);
    }

    private double distanceAtBestAngle(List<Point2D> points, GestureTemplate aTemplate) {
        double startRange = -angleRange;
        double endRange = angleRange;
        double x1 = goldenRatio * startRange + (1.0 - goldenRatio) * endRange;
        double f1 = distanceAtAngle(points, aTemplate, x1);
        double x2 = (1.0 - goldenRatio) * startRange + goldenRatio * endRange;
        double f2 = distanceAtAngle(points, aTemplate, x2);
        while (Math.abs(endRange - startRange) > anglePrecision) {
            if (f1 < f2) {
                endRange = x2;
                x2 = x1;
                f2 = f1;
                x1 = goldenRatio * startRange + (1.0 - goldenRatio) * endRange;
                f1 = distanceAtAngle(points, aTemplate, x1);
            } else {
                startRange = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1.0 - goldenRatio) * startRange + goldenRatio * endRange;
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

    private double pathDistance(List<Point2D> pts1, List<Point2D> pts2) {
        // assumes pts1.size == pts2.size

        double distance = 0.0;
        for (int i = 0; i < pts1.size(); i++) {
            distance += getDistance(pts1.get(i), pts2.get(i));
        }
        return (distance / pts1.size());
    }

    private double pathLength(List<Point2D> points) {
        double distance = 0;
        for (int i = 1; i < points.size(); i++) {
            distance += getDistance(points.get(i - 1), points.get(i));
        }
        return distance;
    }

    private List<Point2D> resample(List<Point2D> points) {
        double interval = pathLength(points) / (numPointsInGesture - 1); // interval length
        double D = 0.0;
        List<Point2D> newPoints = new ArrayList<Point2D>();

        //--- Store first point since we'll never resample it out of existence
        newPoints.add(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Point2D currentPoint = points.get(i);
            Point2D previousPoint = points.get(i - 1);
            double d = getDistance(previousPoint, currentPoint);
            if ((D + d) >= interval) {
                double qx = previousPoint.x + ((interval - D) / d) * (currentPoint.x - previousPoint.x);
                double qy = previousPoint.y + ((interval - D) / d) * (currentPoint.y - previousPoint.y);
                Point2D point = new Point2D(qx, qy);
                newPoints.add(point);
                points.add(i, point);
                D = 0.0;
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

    private List<Point2D> rotateBy(List<Point2D> points, double rotation) {
        Point2D c = centroid(points);
        //--- can't name cos; creates compiler error since VC++ can't
        //---  tell the difference between the variable and function
        double cosine = Math.cos(rotation);
        double sine = Math.sin(rotation);

        List<Point2D> newPoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            double qx = (point.x - c.x) * cosine - (point.y - c.y) * sine + c.x;
            double qy = (point.x - c.x) * sine + (point.y - c.y) * cosine + c.y;
            newPoints.add(new Point2D(qx, qy));
        }
        return newPoints;
    }

    private List<Point2D> rotateToZero(List<Point2D> points) {
        Point2D centroid = centroid(points);
        Point2D point2D = points.get(0);
        double rotation = Math.atan2(centroid.y - point2D.y, centroid.x - point2D.x);
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
            double scaledX = point.x * (this.squareSize / box.width);
            double scaledY = point.y * (this.squareSize / box.height);
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
            angleRange = 45.0;
        } else {
            angleRange = 15.0;
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
            double qx = point.x - c.x;
            double qy = point.y - c.y;
            newPoints.add(new Point2D(qx, qy));
        }
        return newPoints;
    }

    private static class Rectangle {
        double x, y, width, height;

        Rectangle(double x, double y, double width, double height) {
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
