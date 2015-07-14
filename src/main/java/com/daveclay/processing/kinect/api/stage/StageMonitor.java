package com.daveclay.processing.kinect.api.stage;

import com.daveclay.processing.api.ColorUtils;
import com.daveclay.processing.api.HUD;
import com.daveclay.processing.api.StageRect;
import com.daveclay.processing.gestures.RecognitionResult;
import com.daveclay.processing.kinect.api.User;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import static processing.core.PApplet.*;
import static processing.core.PApplet.max;

public class StageMonitor {

    private final Stage stage;
    private final HUD hud;
    private final int width;
    private final int height;
    private final float halfWidth;
    private final float halfHeight;
    private final int alpha = 100;
    private final StageBounds stageBounds;
    private final Stage.CenterZone centerZone;
    private final Stage.LeftFrontZone leftFrontZone;
    private final Stage.RightFrontZone rightFrontZone;
    private final Stage.LeftBackZone leftBackZone;
    private final Stage.RightBackZone rightBackZone;

    private PApplet currentCanvas;

    private List<UserPosition> currentUserPositions = new ArrayList<>();
    private WeakHashMap<User, Stage.StageZone> currentUserStageZones = new WeakHashMap<>();

    private float left;
    private float right;
    private float front;
    private float back;
    private float realWorldWidth;
    private float realWorldDepth;
    private float centerRadius;
    private float mappedHorizontalCenterRadius;
    private float mappedVerticalCenterRadius;
    private PVector center;

    private StageRect frontLeft;
    private StageRect frontRight;
    private StageRect backLeft;
    private StageRect backRight;

    private class UserPosition {
        public final User user;
        public final StagePosition stagePosition;

        public UserPosition(User user, StagePosition stagePosition) {
            this.user = user;
            this.stagePosition = stagePosition;
        }
    }

    public StageMonitor(Stage stage,
                        HUD hud,
                        int width,
                        int height) {
        this.width = width;
        this.height = height;
        this.hud = hud;
        this.stage = stage;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;

        stage.addListener(new BodyLocatorListener() {
            @Override
            public void gestureWasRecognized(User user, RecognitionResult gesture) {
            }

            @Override
            public void userDidEnteredZone(User user, Stage.StageZone stageZone) {
                currentUserStageZones.put(user, stageZone);
            }

            @Override
            public void userDidMove(User user, StagePosition stagePosition) {
                currentUserPositions.add(new UserPosition(user, stagePosition));
            }
        });

        centerZone = (Stage.CenterZone) stage.getStageZoneById(Stage.CenterZone.ID);
        leftFrontZone = (Stage.LeftFrontZone) stage.getStageZoneById(Stage.LeftFrontZone.ID);
        rightFrontZone = (Stage.RightFrontZone) stage.getStageZoneById(Stage.RightFrontZone.ID);
        leftBackZone = (Stage.LeftBackZone) stage.getStageZoneById(Stage.LeftBackZone.ID);
        rightBackZone = (Stage.RightBackZone) stage.getStageZoneById(Stage.RightBackZone.ID);
        stageBounds = stage.getStageBounds();
    }

    public StageMonitor(Stage stage,
                        HUD hud) {
        this(stage, hud, 400, 400);
    }

    public synchronized void draw(PApplet canvas) {
        this.currentCanvas = canvas;

        if ( ! stageBounds.initialized()) {
            return;
        }

        left = stageBounds.getLeft();
        right = stageBounds.getRight();
        front = stageBounds.getFront();
        back = stageBounds.getBack();
        realWorldWidth = stageBounds.getWidth();
        realWorldDepth = stageBounds.getDepth();
        centerRadius = centerZone.getCenterRadius();
        center = stageBounds.getCenter();

        mappedVerticalCenterRadius = min(
                height,
                map(centerRadius, 0, realWorldDepth, 0, height) * 2);

        mappedHorizontalCenterRadius = min(
                width,
                map(centerRadius, 0, realWorldWidth, 0, width) * 2);

        canvas.pushMatrix();
        canvas.pushStyle();
        canvas.translate(0, canvas.getHeight() - height);

        canvas.fill(100, alpha);
        canvas.noStroke();
        canvas.rect(0, 0, width, height);
        canvas.stroke(255, 255, 255);
        canvas.strokeWeight(2);

        frontLeft = new StageRect(canvas, StageRect.FRONT_LEFT);
        frontRight = new StageRect(canvas, StageRect.FRONT_RIGHT);
        backLeft = new StageRect(canvas, StageRect.BACK_LEFT);
        backRight = new StageRect(canvas, StageRect.BACK_RIGHT);

        drawMappedZone(leftFrontZone, frontLeft);
        drawMappedZone(rightFrontZone, frontRight);
        drawMappedZone(leftBackZone, backLeft);
        drawMappedZone(rightBackZone, backRight);
        drawCenterZone();

        currentUserPositions.forEach(this::drawPosition);
        currentUserPositions.clear();

        canvas.popMatrix();
        canvas.popStyle();
    }

    void drawPosition(UserPosition userPosition) {
        StagePosition stagePosition = userPosition.stagePosition;
        float mappedPositionX = map(stagePosition.getFromLeftPercent(), 0, 1f, 0, width);
        float mappedPositionZ = map(stagePosition.getFromFrontPercent(), 0, 1f, 0, height);
        currentCanvas.noStroke();
        currentCanvas.fill(userPosition.user.getColor());
        currentCanvas.ellipse(mappedPositionX, mappedPositionZ, 25, 25);
    }

    private void drawCenterZone() {
        float mappedCenterX = map(center.x, left, right, 0, width);
        float mappedCenterZ = map(center.z, front, back, 0, height);
        setFill(centerZone);
        currentCanvas.ellipse(mappedCenterX, mappedCenterZ, mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
    }

    public void drawMappedZone(Stage.RectStageZone stageZone, StageRect stageRect) {
        PVector leftBottomFront = stageZone.getLeftBottomFront();

        float mappedX = min(halfWidth,
                map(leftBottomFront.x, stageBounds.getLeft(), stageBounds.getRight(), 0, width - 2)); // leave room for the bounds.
        float mappedY = min(halfHeight,
                map(leftBottomFront.z, stageBounds.getFront(), stageBounds.getBack(), 0, height - 2));

        float mappedWidth = min(halfWidth,
                map(stageZone.getWidth(), 0, realWorldWidth, 0, width));

        setFill(stageZone);
        currentCanvas.pushMatrix();
        currentCanvas.translate(mappedX, mappedY);
        stageRect.size(max(mappedWidth, halfWidth));
        stageRect.intersect(mappedHorizontalCenterRadius, mappedVerticalCenterRadius);
        stageRect.draw();

        currentCanvas.popMatrix();
    }

    void setFill(Stage.StageZone stageZone) {
        Stream<Stage.StageZone> drawnZones = this.currentUserStageZones.entrySet().stream().map((entry) -> {
            if (stageZone == entry.getValue()) {
                currentCanvas.fill(userColor(entry.getKey()));
                return stageZone;
            } else {
                return null;
            }
        }).filter((zone) -> zone != null);

        if ( ! drawnZones.anyMatch((zone) -> stageZone == zone)) {
            currentCanvas.fill(100, alpha);
        }
    }

    int userColor(User user) {
        return ColorUtils.addAlpha(user.getColor(), .4f);
    }
}
