package com.daveclay.processing.kinect.api;

import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

import java.util.Arrays;

public class Stage {

    static final int LEFT_FRONT = 1;
    static final int RIGHT_FRONT = 2;
    static final int LEFT_BACK = 3;
    static final int RIGHT_BACK = 4;

    private final StageBounds stageBounds = new StageBounds();
    private float centerRadius = 200;
    private StageZone leftFrontZone = new StageZone(LEFT_FRONT);
    private StageZone rightFrontZone = new StageZone(RIGHT_FRONT);
    private StageZone leftBackZone = new StageZone(LEFT_BACK);
    private StageZone rightBackZone = new StageZone(RIGHT_BACK);

    public StageBounds getStageBounds() {
        return stageBounds;
    }

    public void updatePosition(PVector position) {
        stageBounds.updatePosition(position);
        for (StageZone stageZone: Arrays.asList(leftFrontZone, rightFrontZone, leftBackZone, rightBackZone)) {
            stageZone.updateStageBounds(stageBounds);
        }
    }

    public float getCenterRadius() {
        return centerRadius;
    }

    public boolean isWithinCenterZone(PVector position) {
        PVector center = stageBounds.getCenter();
        return VectorMath.isWithin(center, position, centerRadius);
    }

    public boolean isWithinLeftFront(PVector position) {
        return leftFrontZone.isWithinBounds(position);
    }

    public boolean isWithinRightFront(PVector position) {
        return rightFrontZone.isWithinBounds(position);
    }

    public boolean isWithinLeftBack(PVector position) {
        return leftBackZone.isWithinBounds(position);
    }

    public boolean isWithinRightBack(PVector position) {
        return rightBackZone.isWithinBounds(position);
    }

    public static class StageZone {

        private final int stageZoneType;
        private final PVector leftBottomFront = new PVector();
        private final PVector rightTopBack = new PVector();

        public StageZone(int stageZoneType) {
            this.stageZoneType = stageZoneType;
        }

        public boolean isWithinBounds(PVector position) {
            if (position.x < leftBottomFront.x && position.x > rightTopBack.x) { // left-to-right
                if (position.y > leftBottomFront.y && position.y < rightTopBack.y) { // top-to-bottom
                    if (position.z < rightTopBack.z && position.z > leftBottomFront.z) { // front-to-back
                        return true;
                    }
                }
            }
            return false;
        }

        public void updateStageBounds(StageBounds stageBounds) {
            PVector stageCenter = stageBounds.getCenter();
            if (stageZoneType == LEFT_FRONT) {
                leftBottomFront.set(
                        stageBounds.getLeft(),
                        stageBounds.getBottom(),
                        stageBounds.getFront());

                rightTopBack.set(
                        stageCenter.x,
                        stageBounds.getTop(), // note that we don't bother with top/bottom. Todo: different types of zones that incorporate Y.
                        stageCenter.z);

            } else if (stageZoneType == RIGHT_FRONT) {
                leftBottomFront.set(
                        stageCenter.x,
                        stageBounds.getBottom(),
                        stageBounds.getFront());

                rightTopBack.set(
                        stageBounds.getRight(),
                        stageBounds.getTop(),
                        stageCenter.z);
            } else if (stageZoneType == LEFT_BACK) {
                leftBottomFront.set(
                        stageBounds.getLeft(),
                        stageBounds.getBottom(),
                        stageCenter.z);

                rightTopBack.set(
                        stageBounds.getRight(),
                        stageBounds.getTop(),
                        stageBounds.getBack());
            } else if (stageZoneType == RIGHT_BACK) {
                leftBottomFront.set(
                        stageCenter.x,
                        stageBounds.getBottom(),
                        stageCenter.z);

                rightTopBack.set(
                        stageBounds.getRight(),
                        stageBounds.getTop(),
                        stageBounds.getBack());
            }
        }

        PVector getLeftBottomFront() {
            return leftBottomFront;
        }

        PVector getRightTopBack() {
            return rightTopBack;
        }
    }
}
