package com.daveclay.processing.gestures;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported from:
 * http://www-users.cs.umn.edu/~wetzel/
 * http://depts.washington.edu/aimgroup/proj/dollar/others/cpp.bw.zip
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

    public void loadDefaultTemplates() {
        SampleGestures samples = new SampleGestures();

        addTemplate("Arrow", samples.getGestureArrow());
        addTemplate("Caret", samples.getGestureCaret());
        addTemplate("CheckMark", samples.getGestureCheckMark());
        addTemplate("Circle", samples.getGestureCircle());
        addTemplate("Delete", samples.getGestureDelete());
        addTemplate("Diamond", samples.getGestureDiamond());
        //addTemplate("LeftCurlyBrace", samples.getGestureLeftCurlyBrace());
        addTemplate("LeftSquareBracket", samples.getGestureLeftSquareBracket());
        addTemplate("LeftToRightLine", samples.getGestureLeftToRightLine());
        addTemplate("LineDownDiagonal", samples.getGestureLineDownDiagonal());
        addTemplate("Pigtail", samples.getGesturePigtail());
        addTemplate("QuestionMark", samples.getGestureQuestionMark());
        addTemplate("Rectangle", samples.getGestureRectangle());
        //addTemplate("RightCurlyBrace", samples.getGestureRightCurlyBrace());
        addTemplate("RightSquareBracket", samples.getGestureRightSquareBracket());
        addTemplate("RightToLeftLine", samples.getGestureRightToLeftLine());
        addTemplate("RightToLeftLine2", samples.getGestureRightToLeftLine2());
        addTemplate("RightToLeftSlashDown", samples.getGestureRightToLeftSlashDown());
        addTemplate("Spiral", samples.getGestureSpiral());
        addTemplate("Star", samples.getGestureStar());
        addTemplate("Triangle", samples.getGestureTriangle());
        addTemplate("V", samples.getGestureV());
        addTemplate("X", samples.getGestureX());
    }

    private void addTemplate(String name, List<Point2D> points) {
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

    public static class GestureTemplate {
        String name;
        List<Point2D> points;

        public GestureTemplate(String name, List<Point2D> points) {
            this.name = name;
            this.points = points;
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

    public static class SampleGestures {
        List<Point2D> getGestureArrow() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(68, 222));
            path.add(new Point2D(70, 220));
            path.add(new Point2D(73, 218));
            path.add(new Point2D(75, 217));
            path.add(new Point2D(77, 215));
            path.add(new Point2D(80, 213));
            path.add(new Point2D(82, 212));
            path.add(new Point2D(84, 210));
            path.add(new Point2D(87, 209));
            path.add(new Point2D(89, 208));
            path.add(new Point2D(92, 206));
            path.add(new Point2D(95, 204));
            path.add(new Point2D(101, 201));
            path.add(new Point2D(106, 198));
            path.add(new Point2D(112, 194));
            path.add(new Point2D(118, 191));
            path.add(new Point2D(124, 187));
            path.add(new Point2D(127, 186));
            path.add(new Point2D(132, 183));
            path.add(new Point2D(138, 181));
            path.add(new Point2D(141, 180));
            path.add(new Point2D(146, 178));
            path.add(new Point2D(154, 173));
            path.add(new Point2D(159, 171));
            path.add(new Point2D(161, 170));
            path.add(new Point2D(166, 167));
            path.add(new Point2D(168, 167));
            path.add(new Point2D(171, 166));
            path.add(new Point2D(174, 164));
            path.add(new Point2D(177, 162));
            path.add(new Point2D(180, 160));
            path.add(new Point2D(182, 158));
            path.add(new Point2D(183, 156));
            path.add(new Point2D(181, 154));
            path.add(new Point2D(178, 153));
            path.add(new Point2D(171, 153));
            path.add(new Point2D(164, 153));
            path.add(new Point2D(160, 153));
            path.add(new Point2D(150, 154));
            path.add(new Point2D(147, 155));
            path.add(new Point2D(141, 157));
            path.add(new Point2D(137, 158));
            path.add(new Point2D(135, 158));
            path.add(new Point2D(137, 158));
            path.add(new Point2D(140, 157));
            path.add(new Point2D(143, 156));
            path.add(new Point2D(151, 154));
            path.add(new Point2D(160, 152));
            path.add(new Point2D(170, 149));
            path.add(new Point2D(179, 147));
            path.add(new Point2D(185, 145));
            path.add(new Point2D(192, 144));
            path.add(new Point2D(196, 144));
            path.add(new Point2D(198, 144));
            path.add(new Point2D(200, 144));
            path.add(new Point2D(201, 147));
            path.add(new Point2D(199, 149));
            path.add(new Point2D(194, 157));
            path.add(new Point2D(191, 160));
            path.add(new Point2D(186, 167));
            path.add(new Point2D(180, 176));
            path.add(new Point2D(177, 179));
            path.add(new Point2D(171, 187));
            path.add(new Point2D(169, 189));
            path.add(new Point2D(165, 194));
            path.add(new Point2D(164, 196));

            return path;
        }

        List<Point2D> getGestureCaret() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(79, 245));
            path.add(new Point2D(79, 242));
            path.add(new Point2D(79, 239));
            path.add(new Point2D(80, 237));
            path.add(new Point2D(80, 234));
            path.add(new Point2D(81, 232));
            path.add(new Point2D(82, 230));
            path.add(new Point2D(84, 224));
            path.add(new Point2D(86, 220));
            path.add(new Point2D(86, 218));
            path.add(new Point2D(87, 216));
            path.add(new Point2D(88, 213));
            path.add(new Point2D(90, 207));
            path.add(new Point2D(91, 202));
            path.add(new Point2D(92, 200));
            path.add(new Point2D(93, 194));
            path.add(new Point2D(94, 192));
            path.add(new Point2D(96, 189));
            path.add(new Point2D(97, 186));
            path.add(new Point2D(100, 179));
            path.add(new Point2D(102, 173));
            path.add(new Point2D(105, 165));
            path.add(new Point2D(107, 160));
            path.add(new Point2D(109, 158));
            path.add(new Point2D(112, 151));
            path.add(new Point2D(115, 144));
            path.add(new Point2D(117, 139));
            path.add(new Point2D(119, 136));
            path.add(new Point2D(119, 134));
            path.add(new Point2D(120, 132));
            path.add(new Point2D(121, 129));
            path.add(new Point2D(122, 127));
            path.add(new Point2D(124, 125));
            path.add(new Point2D(126, 124));
            path.add(new Point2D(129, 125));
            path.add(new Point2D(131, 127));
            path.add(new Point2D(132, 130));
            path.add(new Point2D(136, 139));
            path.add(new Point2D(141, 154));
            path.add(new Point2D(145, 166));
            path.add(new Point2D(151, 182));
            path.add(new Point2D(156, 193));
            path.add(new Point2D(157, 196));
            path.add(new Point2D(161, 209));
            path.add(new Point2D(162, 211));
            path.add(new Point2D(167, 223));
            path.add(new Point2D(169, 229));
            path.add(new Point2D(170, 231));
            path.add(new Point2D(173, 237));
            path.add(new Point2D(176, 242));
            path.add(new Point2D(177, 244));
            path.add(new Point2D(179, 250));
            path.add(new Point2D(181, 255));
            path.add(new Point2D(182, 257));

            return path;
        }

        List<Point2D> getGestureCheckMark() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(91, 185));
            path.add(new Point2D(93, 185));
            path.add(new Point2D(95, 185));
            path.add(new Point2D(97, 185));
            path.add(new Point2D(100, 188));
            path.add(new Point2D(102, 189));
            path.add(new Point2D(104, 190));
            path.add(new Point2D(106, 193));
            path.add(new Point2D(108, 195));
            path.add(new Point2D(110, 198));
            path.add(new Point2D(112, 201));
            path.add(new Point2D(114, 204));
            path.add(new Point2D(115, 207));
            path.add(new Point2D(117, 210));
            path.add(new Point2D(118, 212));
            path.add(new Point2D(120, 214));
            path.add(new Point2D(121, 217));
            path.add(new Point2D(122, 219));
            path.add(new Point2D(123, 222));
            path.add(new Point2D(124, 224));
            path.add(new Point2D(126, 226));
            path.add(new Point2D(127, 229));
            path.add(new Point2D(129, 231));
            path.add(new Point2D(130, 233));
            path.add(new Point2D(129, 231));
            path.add(new Point2D(129, 228));
            path.add(new Point2D(129, 226));
            path.add(new Point2D(129, 224));
            path.add(new Point2D(129, 221));
            path.add(new Point2D(129, 218));
            path.add(new Point2D(129, 212));
            path.add(new Point2D(129, 208));
            path.add(new Point2D(130, 198));
            path.add(new Point2D(132, 189));
            path.add(new Point2D(134, 182));
            path.add(new Point2D(137, 173));
            path.add(new Point2D(143, 164));
            path.add(new Point2D(147, 157));
            path.add(new Point2D(151, 151));
            path.add(new Point2D(155, 144));
            path.add(new Point2D(161, 137));
            path.add(new Point2D(165, 131));
            path.add(new Point2D(171, 122));
            path.add(new Point2D(174, 118));
            path.add(new Point2D(176, 114));
            path.add(new Point2D(177, 112));
            path.add(new Point2D(177, 114));
            path.add(new Point2D(175, 116));
            path.add(new Point2D(173, 118));

            return path;
        }

        List<Point2D> getGestureCircle() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(127, 141));
            path.add(new Point2D(124, 140));
            path.add(new Point2D(120, 139));
            path.add(new Point2D(118, 139));
            path.add(new Point2D(116, 139));
            path.add(new Point2D(111, 140));
            path.add(new Point2D(109, 141));
            path.add(new Point2D(104, 144));
            path.add(new Point2D(100, 147));
            path.add(new Point2D(96, 152));
            path.add(new Point2D(93, 157));
            path.add(new Point2D(90, 163));
            path.add(new Point2D(87, 169));
            path.add(new Point2D(85, 175));
            path.add(new Point2D(83, 181));
            path.add(new Point2D(82, 190));
            path.add(new Point2D(82, 195));
            path.add(new Point2D(83, 200));
            path.add(new Point2D(84, 205));
            path.add(new Point2D(88, 213));
            path.add(new Point2D(91, 216));
            path.add(new Point2D(96, 219));
            path.add(new Point2D(103, 222));
            path.add(new Point2D(108, 224));
            path.add(new Point2D(111, 224));
            path.add(new Point2D(120, 224));
            path.add(new Point2D(133, 223));
            path.add(new Point2D(142, 222));
            path.add(new Point2D(152, 218));
            path.add(new Point2D(160, 214));
            path.add(new Point2D(167, 210));
            path.add(new Point2D(173, 204));
            path.add(new Point2D(178, 198));
            path.add(new Point2D(179, 196));
            path.add(new Point2D(182, 188));
            path.add(new Point2D(182, 177));
            path.add(new Point2D(178, 167));
            path.add(new Point2D(170, 150));
            path.add(new Point2D(163, 138));
            path.add(new Point2D(152, 130));
            path.add(new Point2D(143, 129));
            path.add(new Point2D(140, 131));
            path.add(new Point2D(129, 136));
            path.add(new Point2D(126, 139));

            return path;
        }

        List<Point2D> getGestureDelete() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(123, 129));
            path.add(new Point2D(123, 131));
            path.add(new Point2D(124, 133));
            path.add(new Point2D(125, 136));
            path.add(new Point2D(127, 140));
            path.add(new Point2D(129, 142));
            path.add(new Point2D(133, 148));
            path.add(new Point2D(137, 154));
            path.add(new Point2D(143, 158));
            path.add(new Point2D(145, 161));
            path.add(new Point2D(148, 164));
            path.add(new Point2D(153, 170));
            path.add(new Point2D(158, 176));
            path.add(new Point2D(160, 178));
            path.add(new Point2D(164, 183));
            path.add(new Point2D(168, 188));
            path.add(new Point2D(171, 191));
            path.add(new Point2D(175, 196));
            path.add(new Point2D(178, 200));
            path.add(new Point2D(180, 202));
            path.add(new Point2D(181, 205));
            path.add(new Point2D(184, 208));
            path.add(new Point2D(186, 210));
            path.add(new Point2D(187, 213));
            path.add(new Point2D(188, 215));
            path.add(new Point2D(186, 212));
            path.add(new Point2D(183, 211));
            path.add(new Point2D(177, 208));
            path.add(new Point2D(169, 206));
            path.add(new Point2D(162, 205));
            path.add(new Point2D(154, 207));
            path.add(new Point2D(145, 209));
            path.add(new Point2D(137, 210));
            path.add(new Point2D(129, 214));
            path.add(new Point2D(122, 217));
            path.add(new Point2D(118, 218));
            path.add(new Point2D(111, 221));
            path.add(new Point2D(109, 222));
            path.add(new Point2D(110, 219));
            path.add(new Point2D(112, 217));
            path.add(new Point2D(118, 209));
            path.add(new Point2D(120, 207));
            path.add(new Point2D(128, 196));
            path.add(new Point2D(135, 187));
            path.add(new Point2D(138, 183));
            path.add(new Point2D(148, 167));
            path.add(new Point2D(157, 153));
            path.add(new Point2D(163, 145));
            path.add(new Point2D(165, 142));
            path.add(new Point2D(172, 133));
            path.add(new Point2D(177, 127));
            path.add(new Point2D(179, 127));
            path.add(new Point2D(180, 125));

            return path;
        }

        List<Point2D> getGestureDiamond() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(312, 205));
            path.add(new Point2D(311, 205));
            path.add(new Point2D(311, 206));
            path.add(new Point2D(309, 209));
            path.add(new Point2D(308, 212));
            path.add(new Point2D(302, 217));
            path.add(new Point2D(300, 218));
            path.add(new Point2D(294, 225));
            path.add(new Point2D(289, 229));
            path.add(new Point2D(278, 238));
            path.add(new Point2D(274, 243));
            path.add(new Point2D(263, 252));
            path.add(new Point2D(249, 263));
            path.add(new Point2D(233, 274));
            path.add(new Point2D(227, 280));
            path.add(new Point2D(214, 290));
            path.add(new Point2D(208, 296));
            path.add(new Point2D(200, 304));
            path.add(new Point2D(197, 308));
            path.add(new Point2D(188, 315));
            path.add(new Point2D(187, 317));
            path.add(new Point2D(186, 317));
            path.add(new Point2D(184, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 319));
            path.add(new Point2D(183, 322));
            path.add(new Point2D(183, 323));
            path.add(new Point2D(186, 330));
            path.add(new Point2D(190, 333));
            path.add(new Point2D(195, 338));
            path.add(new Point2D(198, 341));
            path.add(new Point2D(203, 345));
            path.add(new Point2D(213, 356));
            path.add(new Point2D(218, 362));
            path.add(new Point2D(233, 375));
            path.add(new Point2D(241, 380));
            path.add(new Point2D(255, 388));
            path.add(new Point2D(261, 392));
            path.add(new Point2D(266, 396));
            path.add(new Point2D(272, 401));
            path.add(new Point2D(273, 402));
            path.add(new Point2D(274, 402));
            path.add(new Point2D(276, 403));
            path.add(new Point2D(278, 403));
            path.add(new Point2D(278, 403));
            path.add(new Point2D(278, 403));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(279, 404));
            path.add(new Point2D(281, 404));
            path.add(new Point2D(282, 403));
            path.add(new Point2D(285, 400));
            path.add(new Point2D(287, 398));
            path.add(new Point2D(299, 391));
            path.add(new Point2D(308, 387));
            path.add(new Point2D(329, 378));
            path.add(new Point2D(341, 373));
            path.add(new Point2D(363, 364));
            path.add(new Point2D(374, 360));
            path.add(new Point2D(394, 349));
            path.add(new Point2D(410, 338));
            path.add(new Point2D(413, 337));
            path.add(new Point2D(426, 327));
            path.add(new Point2D(431, 326));
            path.add(new Point2D(436, 325));
            path.add(new Point2D(441, 321));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(442, 319));
            path.add(new Point2D(439, 318));
            path.add(new Point2D(435, 318));
            path.add(new Point2D(424, 313));
            path.add(new Point2D(422, 310));
            path.add(new Point2D(410, 301));
            path.add(new Point2D(399, 297));
            path.add(new Point2D(384, 286));
            path.add(new Point2D(377, 280));
            path.add(new Point2D(371, 275));
            path.add(new Point2D(366, 269));
            path.add(new Point2D(359, 263));
            path.add(new Point2D(352, 255));
            path.add(new Point2D(348, 251));
            path.add(new Point2D(341, 242));
            path.add(new Point2D(337, 238));
            path.add(new Point2D(334, 231));
            path.add(new Point2D(330, 226));
            path.add(new Point2D(328, 223));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(327, 221));
            path.add(new Point2D(325, 220));
            path.add(new Point2D(324, 219));
            path.add(new Point2D(322, 216));
            path.add(new Point2D(319, 213));
            path.add(new Point2D(316, 210));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));
            path.add(new Point2D(316, 209));

            return path;
        }

        List<Point2D> getGestureLeftCurlyBrace() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(150, 116));
            path.add(new Point2D(147, 117));
            path.add(new Point2D(145, 116));
            path.add(new Point2D(142, 116));
            path.add(new Point2D(139, 117));
            path.add(new Point2D(136, 117));
            path.add(new Point2D(133, 118));
            path.add(new Point2D(129, 121));
            path.add(new Point2D(126, 122));
            path.add(new Point2D(123, 123));
            path.add(new Point2D(120, 125));
            path.add(new Point2D(118, 127));
            path.add(new Point2D(115, 128));
            path.add(new Point2D(113, 129));
            path.add(new Point2D(112, 131));
            path.add(new Point2D(113, 134));
            path.add(new Point2D(115, 134));
            path.add(new Point2D(117, 135));
            path.add(new Point2D(120, 135));
            path.add(new Point2D(123, 137));
            path.add(new Point2D(126, 138));
            path.add(new Point2D(129, 140));
            path.add(new Point2D(135, 143));
            path.add(new Point2D(137, 144));
            path.add(new Point2D(139, 147));
            path.add(new Point2D(141, 149));
            path.add(new Point2D(140, 152));
            path.add(new Point2D(139, 155));
            path.add(new Point2D(134, 159));
            path.add(new Point2D(131, 161));
            path.add(new Point2D(124, 166));
            path.add(new Point2D(121, 166));
            path.add(new Point2D(117, 166));
            path.add(new Point2D(114, 167));
            path.add(new Point2D(112, 166));
            path.add(new Point2D(114, 164));
            path.add(new Point2D(116, 163));
            path.add(new Point2D(118, 163));
            path.add(new Point2D(120, 162));
            path.add(new Point2D(122, 163));
            path.add(new Point2D(125, 164));
            path.add(new Point2D(127, 165));
            path.add(new Point2D(129, 166));
            path.add(new Point2D(130, 168));
            path.add(new Point2D(129, 171));
            path.add(new Point2D(127, 175));
            path.add(new Point2D(125, 179));
            path.add(new Point2D(123, 184));
            path.add(new Point2D(121, 190));
            path.add(new Point2D(120, 194));
            path.add(new Point2D(119, 199));
            path.add(new Point2D(120, 202));
            path.add(new Point2D(123, 207));
            path.add(new Point2D(127, 211));
            path.add(new Point2D(133, 215));
            path.add(new Point2D(142, 219));
            path.add(new Point2D(148, 220));
            path.add(new Point2D(151, 221));

            return path;
        }

        List<Point2D> getGestureLeftSquareBracket() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(140, 124));
            path.add(new Point2D(138, 123));
            path.add(new Point2D(135, 122));
            path.add(new Point2D(133, 123));
            path.add(new Point2D(130, 123));
            path.add(new Point2D(128, 124));
            path.add(new Point2D(125, 125));
            path.add(new Point2D(122, 124));
            path.add(new Point2D(120, 124));
            path.add(new Point2D(118, 124));
            path.add(new Point2D(116, 125));
            path.add(new Point2D(113, 125));
            path.add(new Point2D(111, 125));
            path.add(new Point2D(108, 124));
            path.add(new Point2D(106, 125));
            path.add(new Point2D(104, 125));
            path.add(new Point2D(102, 124));
            path.add(new Point2D(100, 123));
            path.add(new Point2D(98, 123));
            path.add(new Point2D(95, 124));
            path.add(new Point2D(93, 123));
            path.add(new Point2D(90, 124));
            path.add(new Point2D(88, 124));
            path.add(new Point2D(85, 125));
            path.add(new Point2D(83, 126));
            path.add(new Point2D(81, 127));
            path.add(new Point2D(81, 129));
            path.add(new Point2D(82, 131));
            path.add(new Point2D(82, 134));
            path.add(new Point2D(83, 138));
            path.add(new Point2D(84, 141));
            path.add(new Point2D(84, 144));
            path.add(new Point2D(85, 148));
            path.add(new Point2D(85, 151));
            path.add(new Point2D(86, 156));
            path.add(new Point2D(86, 160));
            path.add(new Point2D(86, 164));
            path.add(new Point2D(86, 168));
            path.add(new Point2D(87, 171));
            path.add(new Point2D(87, 175));
            path.add(new Point2D(87, 179));
            path.add(new Point2D(87, 182));
            path.add(new Point2D(87, 186));
            path.add(new Point2D(88, 188));
            path.add(new Point2D(88, 195));
            path.add(new Point2D(88, 198));
            path.add(new Point2D(88, 201));
            path.add(new Point2D(88, 207));
            path.add(new Point2D(89, 211));
            path.add(new Point2D(89, 213));
            path.add(new Point2D(89, 217));
            path.add(new Point2D(89, 222));
            path.add(new Point2D(88, 225));
            path.add(new Point2D(88, 229));
            path.add(new Point2D(88, 231));
            path.add(new Point2D(88, 233));
            path.add(new Point2D(88, 235));
            path.add(new Point2D(89, 237));
            path.add(new Point2D(89, 240));
            path.add(new Point2D(89, 242));
            path.add(new Point2D(91, 241));
            path.add(new Point2D(94, 241));
            path.add(new Point2D(96, 240));
            path.add(new Point2D(98, 239));
            path.add(new Point2D(105, 240));
            path.add(new Point2D(109, 240));
            path.add(new Point2D(113, 239));
            path.add(new Point2D(116, 240));
            path.add(new Point2D(121, 239));
            path.add(new Point2D(130, 240));
            path.add(new Point2D(136, 237));
            path.add(new Point2D(139, 237));
            path.add(new Point2D(144, 238));
            path.add(new Point2D(151, 237));
            path.add(new Point2D(157, 236));
            path.add(new Point2D(159, 237));

            return path;
        }

        List<Point2D> getGestureLeftToRightLine() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(80, 160));
            path.add(new Point2D(82, 160));
            path.add(new Point2D(82, 160));
            path.add(new Point2D(83, 160));
            path.add(new Point2D(83, 160));
            path.add(new Point2D(85, 160));
            path.add(new Point2D(88, 160));
            path.add(new Point2D(90, 160));
            path.add(new Point2D(92, 160));
            path.add(new Point2D(94, 160));
            path.add(new Point2D(99, 160));
            path.add(new Point2D(102, 160));
            path.add(new Point2D(106, 160));
            path.add(new Point2D(109, 160));
            path.add(new Point2D(117, 160));
            path.add(new Point2D(123, 160));
            path.add(new Point2D(126, 160));
            path.add(new Point2D(135, 160));
            path.add(new Point2D(142, 160));
            path.add(new Point2D(145, 160));
            path.add(new Point2D(152, 160));
            path.add(new Point2D(154, 160));
            path.add(new Point2D(165, 160));
            path.add(new Point2D(174, 160));
            path.add(new Point2D(179, 160));
            path.add(new Point2D(186, 160));
            path.add(new Point2D(191, 160));
            path.add(new Point2D(195, 160));
            path.add(new Point2D(197, 160));
            path.add(new Point2D(201, 160));
            path.add(new Point2D(202, 160));

            return path;
        }

        List<Point2D> getGestureLineDownDiagonal() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(45, 215));
            path.add(new Point2D(47, 217));
            path.add(new Point2D(49, 220));
            path.add(new Point2D(82, 249));
            path.add(new Point2D(159, 292));
            path.add(new Point2D(207, 318));
            path.add(new Point2D(298, 374));
            path.add(new Point2D(346, 397));
            path.add(new Point2D(385, 416));
            path.add(new Point2D(427, 445));
            path.add(new Point2D(443, 454));
            path.add(new Point2D(446, 457));
            path.add(new Point2D(447, 457));
            path.add(new Point2D(447, 457));
            path.add(new Point2D(447, 457));
            path.add(new Point2D(450, 459));
            path.add(new Point2D(456, 465));
            path.add(new Point2D(461, 469));
            path.add(new Point2D(464, 473));
            path.add(new Point2D(464, 473));
            path.add(new Point2D(464, 473));
            path.add(new Point2D(464, 473));

            return path;
        }

        List<Point2D> getGesturePigtail() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(81, 219));
            path.add(new Point2D(84, 218));
            path.add(new Point2D(86, 220));
            path.add(new Point2D(88, 220));
            path.add(new Point2D(90, 220));
            path.add(new Point2D(92, 219));
            path.add(new Point2D(95, 220));
            path.add(new Point2D(97, 219));
            path.add(new Point2D(99, 220));
            path.add(new Point2D(102, 218));
            path.add(new Point2D(105, 217));
            path.add(new Point2D(107, 216));
            path.add(new Point2D(110, 216));
            path.add(new Point2D(113, 214));
            path.add(new Point2D(116, 212));
            path.add(new Point2D(118, 210));
            path.add(new Point2D(121, 208));
            path.add(new Point2D(124, 205));
            path.add(new Point2D(126, 202));
            path.add(new Point2D(129, 199));
            path.add(new Point2D(132, 196));
            path.add(new Point2D(136, 191));
            path.add(new Point2D(139, 187));
            path.add(new Point2D(142, 182));
            path.add(new Point2D(144, 179));
            path.add(new Point2D(146, 174));
            path.add(new Point2D(148, 170));
            path.add(new Point2D(149, 168));
            path.add(new Point2D(151, 162));
            path.add(new Point2D(152, 160));
            path.add(new Point2D(152, 157));
            path.add(new Point2D(152, 155));
            path.add(new Point2D(152, 151));
            path.add(new Point2D(152, 149));
            path.add(new Point2D(152, 146));
            path.add(new Point2D(149, 142));
            path.add(new Point2D(148, 139));
            path.add(new Point2D(145, 137));
            path.add(new Point2D(141, 135));
            path.add(new Point2D(139, 135));
            path.add(new Point2D(134, 136));
            path.add(new Point2D(130, 140));
            path.add(new Point2D(128, 142));
            path.add(new Point2D(126, 145));
            path.add(new Point2D(122, 150));
            path.add(new Point2D(119, 158));
            path.add(new Point2D(117, 163));
            path.add(new Point2D(115, 170));
            path.add(new Point2D(114, 175));
            path.add(new Point2D(117, 184));
            path.add(new Point2D(120, 190));
            path.add(new Point2D(125, 199));
            path.add(new Point2D(129, 203));
            path.add(new Point2D(133, 208));
            path.add(new Point2D(138, 213));
            path.add(new Point2D(145, 215));
            path.add(new Point2D(155, 218));
            path.add(new Point2D(164, 219));
            path.add(new Point2D(166, 219));
            path.add(new Point2D(177, 219));
            path.add(new Point2D(182, 218));
            path.add(new Point2D(192, 216));
            path.add(new Point2D(196, 213));
            path.add(new Point2D(199, 212));
            path.add(new Point2D(201, 211));

            return path;
        }

        List<Point2D> getGestureQuestionMark() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(104, 145));
            path.add(new Point2D(103, 142));
            path.add(new Point2D(103, 140));
            path.add(new Point2D(103, 138));
            path.add(new Point2D(103, 135));
            path.add(new Point2D(104, 133));
            path.add(new Point2D(105, 131));
            path.add(new Point2D(106, 128));
            path.add(new Point2D(107, 125));
            path.add(new Point2D(108, 123));
            path.add(new Point2D(111, 121));
            path.add(new Point2D(113, 118));
            path.add(new Point2D(115, 116));
            path.add(new Point2D(117, 116));
            path.add(new Point2D(119, 116));
            path.add(new Point2D(121, 115));
            path.add(new Point2D(124, 116));
            path.add(new Point2D(126, 115));
            path.add(new Point2D(128, 114));
            path.add(new Point2D(130, 115));
            path.add(new Point2D(133, 116));
            path.add(new Point2D(135, 117));
            path.add(new Point2D(140, 120));
            path.add(new Point2D(142, 121));
            path.add(new Point2D(144, 123));
            path.add(new Point2D(146, 125));
            path.add(new Point2D(149, 127));
            path.add(new Point2D(150, 129));
            path.add(new Point2D(152, 130));
            path.add(new Point2D(154, 132));
            path.add(new Point2D(156, 134));
            path.add(new Point2D(158, 137));
            path.add(new Point2D(159, 139));
            path.add(new Point2D(160, 141));
            path.add(new Point2D(160, 143));
            path.add(new Point2D(160, 146));
            path.add(new Point2D(160, 149));
            path.add(new Point2D(159, 153));
            path.add(new Point2D(158, 155));
            path.add(new Point2D(157, 157));
            path.add(new Point2D(155, 159));
            path.add(new Point2D(153, 161));
            path.add(new Point2D(151, 163));
            path.add(new Point2D(146, 167));
            path.add(new Point2D(142, 170));
            path.add(new Point2D(138, 172));
            path.add(new Point2D(134, 173));
            path.add(new Point2D(132, 175));
            path.add(new Point2D(127, 175));
            path.add(new Point2D(124, 175));
            path.add(new Point2D(122, 176));
            path.add(new Point2D(120, 178));
            path.add(new Point2D(119, 180));
            path.add(new Point2D(119, 183));
            path.add(new Point2D(119, 185));
            path.add(new Point2D(120, 190));
            path.add(new Point2D(121, 194));
            path.add(new Point2D(122, 200));
            path.add(new Point2D(123, 205));
            path.add(new Point2D(123, 211));
            path.add(new Point2D(124, 215));
            path.add(new Point2D(124, 223));
            path.add(new Point2D(124, 225));

            return path;
        }

        List<Point2D> getGestureRectangle() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(78, 149));
            path.add(new Point2D(78, 153));
            path.add(new Point2D(78, 157));
            path.add(new Point2D(78, 160));
            path.add(new Point2D(79, 162));
            path.add(new Point2D(79, 164));
            path.add(new Point2D(79, 167));
            path.add(new Point2D(79, 169));
            path.add(new Point2D(79, 173));
            path.add(new Point2D(79, 178));
            path.add(new Point2D(79, 183));
            path.add(new Point2D(80, 189));
            path.add(new Point2D(80, 193));
            path.add(new Point2D(80, 198));
            path.add(new Point2D(80, 202));
            path.add(new Point2D(81, 208));
            path.add(new Point2D(81, 210));
            path.add(new Point2D(81, 216));
            path.add(new Point2D(82, 222));
            path.add(new Point2D(82, 224));
            path.add(new Point2D(82, 227));
            path.add(new Point2D(83, 229));
            path.add(new Point2D(83, 231));
            path.add(new Point2D(85, 230));
            path.add(new Point2D(88, 232));
            path.add(new Point2D(90, 233));
            path.add(new Point2D(92, 232));
            path.add(new Point2D(94, 233));
            path.add(new Point2D(99, 232));
            path.add(new Point2D(102, 233));
            path.add(new Point2D(106, 233));
            path.add(new Point2D(109, 234));
            path.add(new Point2D(117, 235));
            path.add(new Point2D(123, 236));
            path.add(new Point2D(126, 236));
            path.add(new Point2D(135, 237));
            path.add(new Point2D(142, 238));
            path.add(new Point2D(145, 238));
            path.add(new Point2D(152, 238));
            path.add(new Point2D(154, 239));
            path.add(new Point2D(165, 238));
            path.add(new Point2D(174, 237));
            path.add(new Point2D(179, 236));
            path.add(new Point2D(186, 235));
            path.add(new Point2D(191, 235));
            path.add(new Point2D(195, 233));
            path.add(new Point2D(197, 233));
            path.add(new Point2D(200, 233));
            path.add(new Point2D(201, 235));
            path.add(new Point2D(201, 233));
            path.add(new Point2D(199, 231));
            path.add(new Point2D(198, 226));
            path.add(new Point2D(198, 220));
            path.add(new Point2D(196, 207));
            path.add(new Point2D(195, 195));
            path.add(new Point2D(195, 181));
            path.add(new Point2D(195, 173));
            path.add(new Point2D(195, 163));
            path.add(new Point2D(194, 155));
            path.add(new Point2D(192, 145));
            path.add(new Point2D(192, 143));
            path.add(new Point2D(192, 138));
            path.add(new Point2D(191, 135));
            path.add(new Point2D(191, 133));
            path.add(new Point2D(191, 130));
            path.add(new Point2D(190, 128));
            path.add(new Point2D(188, 129));
            path.add(new Point2D(186, 129));
            path.add(new Point2D(181, 132));
            path.add(new Point2D(173, 131));
            path.add(new Point2D(162, 131));
            path.add(new Point2D(151, 132));
            path.add(new Point2D(149, 132));
            path.add(new Point2D(138, 132));
            path.add(new Point2D(136, 132));
            path.add(new Point2D(122, 131));
            path.add(new Point2D(120, 131));
            path.add(new Point2D(109, 130));
            path.add(new Point2D(107, 130));
            path.add(new Point2D(90, 132));
            path.add(new Point2D(81, 133));
            path.add(new Point2D(76, 133));

            return path;
        }

        List<Point2D> getGestureRightSquareBracket() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(112, 138));
            path.add(new Point2D(112, 136));
            path.add(new Point2D(115, 136));
            path.add(new Point2D(118, 137));
            path.add(new Point2D(120, 136));
            path.add(new Point2D(123, 136));
            path.add(new Point2D(125, 136));
            path.add(new Point2D(128, 136));
            path.add(new Point2D(131, 136));
            path.add(new Point2D(134, 135));
            path.add(new Point2D(137, 135));
            path.add(new Point2D(140, 134));
            path.add(new Point2D(143, 133));
            path.add(new Point2D(145, 132));
            path.add(new Point2D(147, 132));
            path.add(new Point2D(149, 132));
            path.add(new Point2D(152, 132));
            path.add(new Point2D(153, 134));
            path.add(new Point2D(154, 137));
            path.add(new Point2D(155, 141));
            path.add(new Point2D(156, 144));
            path.add(new Point2D(157, 152));
            path.add(new Point2D(158, 161));
            path.add(new Point2D(160, 170));
            path.add(new Point2D(162, 182));
            path.add(new Point2D(164, 192));
            path.add(new Point2D(166, 200));
            path.add(new Point2D(167, 209));
            path.add(new Point2D(168, 214));
            path.add(new Point2D(168, 216));
            path.add(new Point2D(169, 221));
            path.add(new Point2D(169, 223));
            path.add(new Point2D(169, 228));
            path.add(new Point2D(169, 231));
            path.add(new Point2D(166, 233));
            path.add(new Point2D(164, 234));
            path.add(new Point2D(161, 235));
            path.add(new Point2D(155, 236));
            path.add(new Point2D(147, 235));
            path.add(new Point2D(140, 233));
            path.add(new Point2D(131, 233));
            path.add(new Point2D(124, 233));
            path.add(new Point2D(117, 235));
            path.add(new Point2D(114, 238));
            path.add(new Point2D(112, 238));

            return path;
        }

        List<Point2D> getGestureRightCurlyBrace() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(117, 132));
            path.add(new Point2D(115, 132));
            path.add(new Point2D(115, 129));
            path.add(new Point2D(117, 129));
            path.add(new Point2D(119, 128));
            path.add(new Point2D(122, 127));
            path.add(new Point2D(125, 127));
            path.add(new Point2D(127, 127));
            path.add(new Point2D(130, 127));
            path.add(new Point2D(133, 129));
            path.add(new Point2D(136, 129));
            path.add(new Point2D(138, 130));
            path.add(new Point2D(140, 131));
            path.add(new Point2D(143, 134));
            path.add(new Point2D(144, 136));
            path.add(new Point2D(145, 139));
            path.add(new Point2D(145, 142));
            path.add(new Point2D(145, 145));
            path.add(new Point2D(145, 147));
            path.add(new Point2D(145, 149));
            path.add(new Point2D(144, 152));
            path.add(new Point2D(142, 157));
            path.add(new Point2D(141, 160));
            path.add(new Point2D(139, 163));
            path.add(new Point2D(137, 166));
            path.add(new Point2D(135, 167));
            path.add(new Point2D(133, 169));
            path.add(new Point2D(131, 172));
            path.add(new Point2D(128, 173));
            path.add(new Point2D(126, 176));
            path.add(new Point2D(125, 178));
            path.add(new Point2D(125, 180));
            path.add(new Point2D(125, 182));
            path.add(new Point2D(126, 184));
            path.add(new Point2D(128, 187));
            path.add(new Point2D(130, 187));
            path.add(new Point2D(132, 188));
            path.add(new Point2D(135, 189));
            path.add(new Point2D(140, 189));
            path.add(new Point2D(145, 189));
            path.add(new Point2D(150, 187));
            path.add(new Point2D(155, 186));
            path.add(new Point2D(157, 185));
            path.add(new Point2D(159, 184));
            path.add(new Point2D(156, 185));
            path.add(new Point2D(154, 185));
            path.add(new Point2D(149, 185));
            path.add(new Point2D(145, 187));
            path.add(new Point2D(141, 188));
            path.add(new Point2D(136, 191));
            path.add(new Point2D(134, 191));
            path.add(new Point2D(131, 192));
            path.add(new Point2D(129, 193));
            path.add(new Point2D(129, 195));
            path.add(new Point2D(129, 197));
            path.add(new Point2D(131, 200));
            path.add(new Point2D(133, 202));
            path.add(new Point2D(136, 206));
            path.add(new Point2D(139, 211));
            path.add(new Point2D(142, 215));
            path.add(new Point2D(145, 220));
            path.add(new Point2D(147, 225));
            path.add(new Point2D(148, 231));
            path.add(new Point2D(147, 239));
            path.add(new Point2D(144, 244));
            path.add(new Point2D(139, 248));
            path.add(new Point2D(134, 250));
            path.add(new Point2D(126, 253));
            path.add(new Point2D(119, 253));
            path.add(new Point2D(115, 253));

            return path;
        }

        List<Point2D> getGestureRightToLeftLine() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(200, 160));
            path.add(new Point2D(195, 160));
            path.add(new Point2D(190, 160));
            path.add(new Point2D(185, 160));
            path.add(new Point2D(180, 160));
            path.add(new Point2D(175, 160));
            path.add(new Point2D(170, 160));
            path.add(new Point2D(165, 160));
            path.add(new Point2D(160, 160));
            path.add(new Point2D(155, 160));
            path.add(new Point2D(150, 160));
            path.add(new Point2D(160, 160));
            path.add(new Point2D(140, 160));
            path.add(new Point2D(160, 160));
            path.add(new Point2D(155, 160));
            path.add(new Point2D(150, 160));
            path.add(new Point2D(145, 160));
            path.add(new Point2D(140, 160));
            path.add(new Point2D(135, 160));
            path.add(new Point2D(130, 160));
            path.add(new Point2D(125, 160));
            path.add(new Point2D(120, 160));
            path.add(new Point2D(115, 160));
            path.add(new Point2D(110, 160));
            path.add(new Point2D(105, 160));
            path.add(new Point2D(100, 160));
            path.add(new Point2D(95, 160));
            path.add(new Point2D(85, 160));
            path.add(new Point2D(75, 160));
            path.add(new Point2D(70, 160));

            return path;
        }

        List<Point2D> getGestureRightToLeftLine2() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(200, 160));
            path.add(new Point2D(195, 170));
            path.add(new Point2D(190, 160));
            path.add(new Point2D(185, 170));
            path.add(new Point2D(180, 160));
            path.add(new Point2D(175, 170));
            path.add(new Point2D(170, 170));
            path.add(new Point2D(165, 160));
            path.add(new Point2D(160, 170));
            path.add(new Point2D(155, 160));
            path.add(new Point2D(150, 170));
            path.add(new Point2D(160, 160));
            path.add(new Point2D(140, 170));
            path.add(new Point2D(160, 160));
            path.add(new Point2D(155, 170));
            path.add(new Point2D(150, 160));
            path.add(new Point2D(145, 170));
            path.add(new Point2D(140, 160));
            path.add(new Point2D(135, 170));
            path.add(new Point2D(130, 160));
            path.add(new Point2D(125, 170));
            path.add(new Point2D(120, 160));
            path.add(new Point2D(115, 170));
            path.add(new Point2D(110, 160));
            path.add(new Point2D(105, 170));
            path.add(new Point2D(100, 160));
            path.add(new Point2D(95, 170));
            path.add(new Point2D(85, 170));
            path.add(new Point2D(75, 160));
            path.add(new Point2D(70, 170));

            return path;
        }

        List<Point2D> getGestureRightToLeftSlashDown() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(200, 170));
            path.add(new Point2D(195, 171));
            path.add(new Point2D(190, 172));
            path.add(new Point2D(185, 173));
            path.add(new Point2D(180, 174));
            path.add(new Point2D(175, 175));
            path.add(new Point2D(170, 176));
            path.add(new Point2D(165, 177));
            path.add(new Point2D(160, 178));
            path.add(new Point2D(155, 179));
            path.add(new Point2D(150, 180));
            path.add(new Point2D(160, 181));
            path.add(new Point2D(140, 182));
            path.add(new Point2D(160, 183));
            path.add(new Point2D(155, 184));
            path.add(new Point2D(150, 185));
            path.add(new Point2D(145, 186));
            path.add(new Point2D(140, 187));
            path.add(new Point2D(135, 188));
            path.add(new Point2D(130, 189));
            path.add(new Point2D(125, 190));
            path.add(new Point2D(120, 191));
            path.add(new Point2D(115, 192));
            path.add(new Point2D(110, 193));
            path.add(new Point2D(105, 194));
            path.add(new Point2D(100, 195));
            path.add(new Point2D(95, 196));
            path.add(new Point2D(85, 197));
            path.add(new Point2D(75, 198));
            path.add(new Point2D(70, 199));

            return path;
        }

        List<Point2D> getGestureSpiral() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(636, 414));
            path.add(new Point2D(634, 414));
            path.add(new Point2D(632, 412));
            path.add(new Point2D(630, 408));
            path.add(new Point2D(625, 403));
            path.add(new Point2D(619, 398));
            path.add(new Point2D(612, 393));
            path.add(new Point2D(606, 391));
            path.add(new Point2D(598, 391));
            path.add(new Point2D(590, 391));
            path.add(new Point2D(578, 391));
            path.add(new Point2D(561, 398));
            path.add(new Point2D(549, 406));
            path.add(new Point2D(537, 414));
            path.add(new Point2D(532, 419));
            path.add(new Point2D(525, 426));
            path.add(new Point2D(521, 438));
            path.add(new Point2D(519, 442));
            path.add(new Point2D(519, 445));
            path.add(new Point2D(519, 456));
            path.add(new Point2D(519, 464));
            path.add(new Point2D(520, 473));
            path.add(new Point2D(523, 479));
            path.add(new Point2D(530, 488));
            path.add(new Point2D(537, 494));
            path.add(new Point2D(545, 499));
            path.add(new Point2D(556, 502));
            path.add(new Point2D(570, 504));
            path.add(new Point2D(593, 504));
            path.add(new Point2D(625, 504));
            path.add(new Point2D(646, 501));
            path.add(new Point2D(666, 501));
            path.add(new Point2D(676, 501));
            path.add(new Point2D(687, 498));
            path.add(new Point2D(695, 490));
            path.add(new Point2D(697, 484));
            path.add(new Point2D(699, 474));
            path.add(new Point2D(699, 463));
            path.add(new Point2D(699, 450));
            path.add(new Point2D(695, 436));
            path.add(new Point2D(691, 426));
            path.add(new Point2D(676, 401));
            path.add(new Point2D(662, 381));
            path.add(new Point2D(650, 368));
            path.add(new Point2D(634, 352));
            path.add(new Point2D(623, 349));
            path.add(new Point2D(609, 340));
            path.add(new Point2D(600, 337));
            path.add(new Point2D(584, 334));
            path.add(new Point2D(568, 334));
            path.add(new Point2D(551, 334));
            path.add(new Point2D(530, 334));
            path.add(new Point2D(511, 338));
            path.add(new Point2D(498, 344));
            path.add(new Point2D(491, 348));
            path.add(new Point2D(480, 362));
            path.add(new Point2D(464, 382));
            path.add(new Point2D(449, 398));
            path.add(new Point2D(434, 413));
            path.add(new Point2D(426, 425));
            path.add(new Point2D(424, 435));
            path.add(new Point2D(424, 444));
            path.add(new Point2D(424, 465));
            path.add(new Point2D(424, 481));
            path.add(new Point2D(424, 494));
            path.add(new Point2D(431, 511));
            path.add(new Point2D(439, 524));
            path.add(new Point2D(447, 537));
            path.add(new Point2D(462, 550));
            path.add(new Point2D(481, 567));
            path.add(new Point2D(507, 579));
            path.add(new Point2D(544, 592));
            path.add(new Point2D(583, 593));
            path.add(new Point2D(633, 595));
            path.add(new Point2D(660, 595));
            path.add(new Point2D(702, 595));
            path.add(new Point2D(737, 595));
            path.add(new Point2D(765, 583));
            path.add(new Point2D(811, 561));
            path.add(new Point2D(858, 536));
            path.add(new Point2D(886, 525));
            path.add(new Point2D(915, 504));
            path.add(new Point2D(933, 478));
            path.add(new Point2D(937, 454));
            path.add(new Point2D(935, 424));
            path.add(new Point2D(927, 387));
            path.add(new Point2D(915, 357));
            path.add(new Point2D(902, 335));
            path.add(new Point2D(879, 304));
            path.add(new Point2D(861, 285));
            path.add(new Point2D(817, 243));
            path.add(new Point2D(762, 199));
            path.add(new Point2D(704, 155));
            path.add(new Point2D(659, 136));
            path.add(new Point2D(629, 132));
            path.add(new Point2D(600, 132));
            path.add(new Point2D(580, 132));
            path.add(new Point2D(538, 132));
            path.add(new Point2D(483, 132));
            path.add(new Point2D(442, 132));
            path.add(new Point2D(418, 137));
            path.add(new Point2D(390, 145));
            path.add(new Point2D(367, 155));
            path.add(new Point2D(337, 175));
            path.add(new Point2D(322, 190));
            path.add(new Point2D(307, 210));
            path.add(new Point2D(296, 231));
            path.add(new Point2D(284, 254));
            path.add(new Point2D(271, 278));
            path.add(new Point2D(261, 300));
            path.add(new Point2D(251, 319));
            path.add(new Point2D(246, 333));
            path.add(new Point2D(240, 353));
            path.add(new Point2D(236, 389));
            path.add(new Point2D(236, 418));
            path.add(new Point2D(236, 446));
            path.add(new Point2D(236, 474));
            path.add(new Point2D(236, 495));
            path.add(new Point2D(236, 524));
            path.add(new Point2D(236, 544));
            path.add(new Point2D(238, 558));
            path.add(new Point2D(241, 566));
            path.add(new Point2D(245, 577));
            path.add(new Point2D(250, 588));
            path.add(new Point2D(270, 611));
            path.add(new Point2D(291, 629));
            path.add(new Point2D(308, 644));
            path.add(new Point2D(328, 653));
            path.add(new Point2D(349, 668));
            path.add(new Point2D(374, 679));
            path.add(new Point2D(394, 689));
            path.add(new Point2D(419, 697));
            path.add(new Point2D(444, 697));
            path.add(new Point2D(468, 699));
            path.add(new Point2D(502, 699));
            path.add(new Point2D(517, 699));
            path.add(new Point2D(524, 699));
            path.add(new Point2D(530, 699));
            path.add(new Point2D(533, 699));
            path.add(new Point2D(535, 699));
            path.add(new Point2D(535, 699));
            path.add(new Point2D(535, 699));
            path.add(new Point2D(535, 699));
            path.add(new Point2D(535, 699));
            path.add(new Point2D(536, 699));
            path.add(new Point2D(536, 699));
            path.add(new Point2D(536, 699));
            path.add(new Point2D(536, 699));

            return path;
        }

        List<Point2D> getGestureStar() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(75, 250));
            path.add(new Point2D(75, 247));
            path.add(new Point2D(77, 244));
            path.add(new Point2D(78, 242));
            path.add(new Point2D(79, 239));
            path.add(new Point2D(80, 237));
            path.add(new Point2D(82, 234));
            path.add(new Point2D(82, 232));
            path.add(new Point2D(84, 229));
            path.add(new Point2D(85, 225));
            path.add(new Point2D(87, 222));
            path.add(new Point2D(88, 219));
            path.add(new Point2D(89, 216));
            path.add(new Point2D(91, 212));
            path.add(new Point2D(92, 208));
            path.add(new Point2D(94, 204));
            path.add(new Point2D(95, 201));
            path.add(new Point2D(96, 196));
            path.add(new Point2D(97, 194));
            path.add(new Point2D(98, 191));
            path.add(new Point2D(100, 185));
            path.add(new Point2D(102, 178));
            path.add(new Point2D(104, 173));
            path.add(new Point2D(104, 171));
            path.add(new Point2D(105, 164));
            path.add(new Point2D(106, 158));
            path.add(new Point2D(107, 156));
            path.add(new Point2D(107, 152));
            path.add(new Point2D(108, 145));
            path.add(new Point2D(109, 141));
            path.add(new Point2D(110, 139));
            path.add(new Point2D(112, 133));
            path.add(new Point2D(113, 131));
            path.add(new Point2D(116, 127));
            path.add(new Point2D(117, 125));
            path.add(new Point2D(119, 122));
            path.add(new Point2D(121, 121));
            path.add(new Point2D(123, 120));
            path.add(new Point2D(125, 122));
            path.add(new Point2D(125, 125));
            path.add(new Point2D(127, 130));
            path.add(new Point2D(128, 133));
            path.add(new Point2D(131, 143));
            path.add(new Point2D(136, 153));
            path.add(new Point2D(140, 163));
            path.add(new Point2D(144, 172));
            path.add(new Point2D(145, 175));
            path.add(new Point2D(151, 189));
            path.add(new Point2D(156, 201));
            path.add(new Point2D(161, 213));
            path.add(new Point2D(166, 225));
            path.add(new Point2D(169, 233));
            path.add(new Point2D(171, 236));
            path.add(new Point2D(174, 243));
            path.add(new Point2D(177, 247));
            path.add(new Point2D(178, 249));
            path.add(new Point2D(179, 251));
            path.add(new Point2D(180, 253));
            path.add(new Point2D(180, 255));
            path.add(new Point2D(179, 257));
            path.add(new Point2D(177, 257));
            path.add(new Point2D(174, 255));
            path.add(new Point2D(169, 250));
            path.add(new Point2D(164, 247));
            path.add(new Point2D(160, 245));
            path.add(new Point2D(149, 238));
            path.add(new Point2D(138, 230));
            path.add(new Point2D(127, 221));
            path.add(new Point2D(124, 220));
            path.add(new Point2D(112, 212));
            path.add(new Point2D(110, 210));
            path.add(new Point2D(96, 201));
            path.add(new Point2D(84, 195));
            path.add(new Point2D(74, 190));
            path.add(new Point2D(64, 182));
            path.add(new Point2D(55, 175));
            path.add(new Point2D(51, 172));
            path.add(new Point2D(49, 170));
            path.add(new Point2D(51, 169));
            path.add(new Point2D(56, 169));
            path.add(new Point2D(66, 169));
            path.add(new Point2D(78, 168));
            path.add(new Point2D(92, 166));
            path.add(new Point2D(107, 164));
            path.add(new Point2D(123, 161));
            path.add(new Point2D(140, 162));
            path.add(new Point2D(156, 162));
            path.add(new Point2D(171, 160));
            path.add(new Point2D(173, 160));
            path.add(new Point2D(186, 160));
            path.add(new Point2D(195, 160));
            path.add(new Point2D(198, 161));
            path.add(new Point2D(203, 163));
            path.add(new Point2D(208, 163));
            path.add(new Point2D(206, 164));
            path.add(new Point2D(200, 167));
            path.add(new Point2D(187, 172));
            path.add(new Point2D(174, 179));
            path.add(new Point2D(172, 181));
            path.add(new Point2D(153, 192));
            path.add(new Point2D(137, 201));
            path.add(new Point2D(123, 211));
            path.add(new Point2D(112, 220));
            path.add(new Point2D(99, 229));
            path.add(new Point2D(90, 237));
            path.add(new Point2D(80, 244));
            path.add(new Point2D(73, 250));
            path.add(new Point2D(69, 254));
            path.add(new Point2D(69, 252));

            return path;
        }

        List<Point2D> getGestureTriangle() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(137, 139));
            path.add(new Point2D(135, 141));
            path.add(new Point2D(133, 144));
            path.add(new Point2D(132, 146));
            path.add(new Point2D(130, 149));
            path.add(new Point2D(128, 151));
            path.add(new Point2D(126, 155));
            path.add(new Point2D(123, 160));
            path.add(new Point2D(120, 166));
            path.add(new Point2D(116, 171));
            path.add(new Point2D(112, 177));
            path.add(new Point2D(107, 183));
            path.add(new Point2D(102, 188));
            path.add(new Point2D(100, 191));
            path.add(new Point2D(95, 195));
            path.add(new Point2D(90, 199));
            path.add(new Point2D(86, 203));
            path.add(new Point2D(82, 206));
            path.add(new Point2D(80, 209));
            path.add(new Point2D(75, 213));
            path.add(new Point2D(73, 213));
            path.add(new Point2D(70, 216));
            path.add(new Point2D(67, 219));
            path.add(new Point2D(64, 221));
            path.add(new Point2D(61, 223));
            path.add(new Point2D(60, 225));
            path.add(new Point2D(62, 226));
            path.add(new Point2D(65, 225));
            path.add(new Point2D(67, 226));
            path.add(new Point2D(74, 226));
            path.add(new Point2D(77, 227));
            path.add(new Point2D(85, 229));
            path.add(new Point2D(91, 230));
            path.add(new Point2D(99, 231));
            path.add(new Point2D(108, 232));
            path.add(new Point2D(116, 233));
            path.add(new Point2D(125, 233));
            path.add(new Point2D(134, 234));
            path.add(new Point2D(145, 233));
            path.add(new Point2D(153, 232));
            path.add(new Point2D(160, 233));
            path.add(new Point2D(170, 234));
            path.add(new Point2D(177, 235));
            path.add(new Point2D(179, 236));
            path.add(new Point2D(186, 237));
            path.add(new Point2D(193, 238));
            path.add(new Point2D(198, 239));
            path.add(new Point2D(200, 237));
            path.add(new Point2D(202, 239));
            path.add(new Point2D(204, 238));
            path.add(new Point2D(206, 234));
            path.add(new Point2D(205, 230));
            path.add(new Point2D(202, 222));
            path.add(new Point2D(197, 216));
            path.add(new Point2D(192, 207));
            path.add(new Point2D(186, 198));
            path.add(new Point2D(179, 189));
            path.add(new Point2D(174, 183));
            path.add(new Point2D(170, 178));
            path.add(new Point2D(164, 171));
            path.add(new Point2D(161, 168));
            path.add(new Point2D(154, 160));
            path.add(new Point2D(148, 155));
            path.add(new Point2D(143, 150));
            path.add(new Point2D(138, 148));
            path.add(new Point2D(136, 148));

            return path;
        }

        List<Point2D> getGestureV() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(89, 164));
            path.add(new Point2D(90, 162));
            path.add(new Point2D(92, 162));
            path.add(new Point2D(94, 164));
            path.add(new Point2D(95, 166));
            path.add(new Point2D(96, 169));
            path.add(new Point2D(97, 171));
            path.add(new Point2D(99, 175));
            path.add(new Point2D(101, 178));
            path.add(new Point2D(103, 182));
            path.add(new Point2D(106, 189));
            path.add(new Point2D(108, 194));
            path.add(new Point2D(111, 199));
            path.add(new Point2D(114, 204));
            path.add(new Point2D(117, 209));
            path.add(new Point2D(119, 214));
            path.add(new Point2D(122, 218));
            path.add(new Point2D(124, 222));
            path.add(new Point2D(126, 225));
            path.add(new Point2D(128, 228));
            path.add(new Point2D(130, 229));
            path.add(new Point2D(133, 233));
            path.add(new Point2D(134, 236));
            path.add(new Point2D(136, 239));
            path.add(new Point2D(138, 240));
            path.add(new Point2D(139, 242));
            path.add(new Point2D(140, 244));
            path.add(new Point2D(142, 242));
            path.add(new Point2D(142, 240));
            path.add(new Point2D(142, 237));
            path.add(new Point2D(143, 235));
            path.add(new Point2D(143, 233));
            path.add(new Point2D(145, 229));
            path.add(new Point2D(146, 226));
            path.add(new Point2D(148, 217));
            path.add(new Point2D(149, 208));
            path.add(new Point2D(149, 205));
            path.add(new Point2D(151, 196));
            path.add(new Point2D(151, 193));
            path.add(new Point2D(153, 182));
            path.add(new Point2D(155, 172));
            path.add(new Point2D(157, 165));
            path.add(new Point2D(159, 160));
            path.add(new Point2D(162, 155));
            path.add(new Point2D(164, 150));
            path.add(new Point2D(165, 148));
            path.add(new Point2D(166, 146));

            return path;
        }

        List<Point2D> getGestureX() {
            List<Point2D> path = new ArrayList<Point2D>();
            path.add(new Point2D(87, 142));
            path.add(new Point2D(89, 145));
            path.add(new Point2D(91, 148));
            path.add(new Point2D(93, 151));
            path.add(new Point2D(96, 155));
            path.add(new Point2D(98, 157));
            path.add(new Point2D(100, 160));
            path.add(new Point2D(102, 162));
            path.add(new Point2D(106, 167));
            path.add(new Point2D(108, 169));
            path.add(new Point2D(110, 171));
            path.add(new Point2D(115, 177));
            path.add(new Point2D(119, 183));
            path.add(new Point2D(123, 189));
            path.add(new Point2D(127, 193));
            path.add(new Point2D(129, 196));
            path.add(new Point2D(133, 200));
            path.add(new Point2D(137, 206));
            path.add(new Point2D(140, 209));
            path.add(new Point2D(143, 212));
            path.add(new Point2D(146, 215));
            path.add(new Point2D(151, 220));
            path.add(new Point2D(153, 222));
            path.add(new Point2D(155, 223));
            path.add(new Point2D(157, 225));
            path.add(new Point2D(158, 223));
            path.add(new Point2D(157, 218));
            path.add(new Point2D(155, 211));
            path.add(new Point2D(154, 208));
            path.add(new Point2D(152, 200));
            path.add(new Point2D(150, 189));
            path.add(new Point2D(148, 179));
            path.add(new Point2D(147, 170));
            path.add(new Point2D(147, 158));
            path.add(new Point2D(147, 148));
            path.add(new Point2D(147, 141));
            path.add(new Point2D(147, 136));
            path.add(new Point2D(144, 135));
            path.add(new Point2D(142, 137));
            path.add(new Point2D(140, 139));
            path.add(new Point2D(135, 145));
            path.add(new Point2D(131, 152));
            path.add(new Point2D(124, 163));
            path.add(new Point2D(116, 177));
            path.add(new Point2D(108, 191));
            path.add(new Point2D(100, 206));
            path.add(new Point2D(94, 217));
            path.add(new Point2D(91, 222));
            path.add(new Point2D(89, 225));
            path.add(new Point2D(87, 226));
            path.add(new Point2D(87, 224));

            return path;
        }
    }
}
