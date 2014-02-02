package com.daveclay.processing.kinect.api;

import com.daveclay.processing.api.VectorMath;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {

    private final StageBounds stageBounds = new StageBounds();
    private Map<String, StageZone> stageZoneById = new HashMap<String, StageZone>();
    private List<StageZone> stageZones = new ArrayList<StageZone>();

    public void addStageZone(StageZone stageZone) {
        this.stageZones.add(stageZone);
        this.stageZoneById.put(stageZone.getID(), stageZone);
    }

    public StageBounds getStageBounds() {
        return stageBounds;
    }

    public void setupDefaultStageZones() {
        addStageZone(new CenterZone());
        addStageZone(new LeftFrontZone());
        addStageZone(new RightFrontZone());
        addStageZone(new LeftBackZone());
        addStageZone(new RightBackZone());
    }

    public void updatePosition(PVector position) {
        stageBounds.updatePosition(position);
        for (StageZone stageZone : stageZones) {
            stageZone.updateStageBounds(stageBounds);
        }
    }

    public boolean isWithinZone(String zoneID, PVector position) {
        StageZone zone = stageZoneById.get(zoneID);
        return zone != null && zone.isWithinBounds(position);
    }

    public boolean isWithinCenterZone(PVector position) {
        return isWithinZone(CenterZone.ID, position);
    }

    public boolean isWithinLeftFront(PVector position) {
        return isWithinZone(LeftFrontZone.ID, position);
    }

    public boolean isWithinRightFront(PVector position) {
        return isWithinZone(RightFrontZone.ID, position);
    }

    public boolean isWithinLeftBack(PVector position) {
        return isWithinZone(LeftBackZone.ID, position);
    }

    public boolean isWithinRightBack(PVector position) {
        return isWithinZone(RightBackZone.ID, position);
    }

    public static interface StageZone {
        String getID();
        boolean isWithinBounds(PVector position);
        void updateStageBounds(StageBounds stageBounds);
    }

    public static class CenterZone implements StageZone {

        public static final String ID = "Center";

        private final PVector center = new PVector();
        private float centerRadius = 200;

        @Override
        public String getID() {
            return ID;
        }

        public float getCenterRadius() {
            return centerRadius;
        }

        public void setCenterRadius(float centerRadius) {
            this.centerRadius = centerRadius;
        }

        @Override
        public void updateStageBounds(StageBounds stageBounds) {
            center.set(stageBounds.getCenter());
        }

        @Override
        public boolean isWithinBounds(PVector position) {
            return VectorMath.isWithin(center, position, centerRadius);
        }
    }

    public static class LeftFrontZone extends RectStageZone {
        public static final String ID = "Left Front";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        void calculateBounds(StageBounds stageBounds, PVector stageCenter) {
            leftBottomFront.set(
                    stageBounds.getLeft(),
                    stageBounds.getBottom(),
                    stageBounds.getFront());

            rightTopBack.set(
                    stageCenter.x,
                    stageBounds.getTop(), // note that we don't bother with top/bottom. Todo: different types of zones that incorporate Y.
                    stageCenter.z);
        }
    }

    public static class RightFrontZone extends RectStageZone {
        public static final String ID = "Right Front";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        void calculateBounds(StageBounds stageBounds, PVector stageCenter) {
            leftBottomFront.set(
                    stageCenter.x,
                    stageBounds.getBottom(),
                    stageBounds.getFront());

            rightTopBack.set(
                    stageBounds.getRight(),
                    stageBounds.getTop(),
                    stageCenter.z);
        }
    }

    public static class LeftBackZone extends RectStageZone {
        public static final String ID = "Left Back";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        void calculateBounds(StageBounds stageBounds, PVector stageCenter) {
            leftBottomFront.set(
                    stageBounds.getLeft(),
                    stageBounds.getBottom(),
                    stageCenter.z);

            rightTopBack.set(
                    stageBounds.getRight(),
                    stageBounds.getTop(),
                    stageBounds.getBack());
        }
    }

    public static class RightBackZone extends RectStageZone {
        public static final String ID = "Right Back";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        void calculateBounds(StageBounds stageBounds, PVector stageCenter) {
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

    public abstract static class RectStageZone implements StageZone {

        final PVector leftBottomFront = new PVector();
        final PVector rightTopBack = new PVector();

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
            calculateBounds(stageBounds, stageCenter);
        }

        abstract void calculateBounds(StageBounds stageBounds, PVector stageCenter);

        PVector getLeftBottomFront() {
            return leftBottomFront;
        }

        PVector getRightTopBack() {
            return rightTopBack;
        }
    }
}
