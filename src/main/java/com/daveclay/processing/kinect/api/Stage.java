package com.daveclay.processing.kinect.api;

import com.daveclay.processing.api.VectorMath;
import com.daveclay.processing.kinect.bodylocator.BodyLocatorListener;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {

    private final List<BodyLocatorListener> listeners = new ArrayList<BodyLocatorListener>();
    private final StageBounds stageBounds = new StageBounds();
    private Map<String, StageZone> stageZoneById = new HashMap<String, StageZone>();
    private List<StageZone> stageZones = new ArrayList<StageZone>();
    private PVector position = new PVector();

    // Todo: ths should be its own listener, not a BodyLocator.Listener - the stage only sends
    // stage update events, doens't care about gestures.
    public void addListener(BodyLocatorListener listener) {
        this.listeners.add(listener);
    }

    public void addStageZone(StageZone stageZone) {
        this.stageZones.add(stageZone);
        this.stageZoneById.put(stageZone.getID(), stageZone);
    }

    public StageBounds getStageBounds() {
        return stageBounds;
    }

    public PVector getPosition() {
        return position;
    }

    public void setupDefaultStageZones() {
        addStageZone(new CenterZone());
        addStageZone(new LeftFrontZone());
        addStageZone(new RightFrontZone());
        addStageZone(new LeftBackZone());
        addStageZone(new RightBackZone());
    }

    private StageZone currentStageZone = null;

    public void updatePosition(PVector position) {
        this.position = position;
        stageBounds.expandStageBounds(position);
        boolean foundMatchingZone = false;
        for (StageZone stageZone : stageZones) {
            stageZone.updateStageBounds(stageBounds);
            if ( ! foundMatchingZone && stageZone.isWithinBounds(position)) {
                // We haven't found a matching zone yet, and this one matches.
                foundMatchingZone = true;
                if (stageZone != currentStageZone) {
                    // But, only fire the event if the user has changed zones.
                    fireUserDidEnterZoneEvent(stageZone);
                    currentStageZone = stageZone;
                }
            }
        }

        fireUserStagePositionEvent(position);
    }

    private void fireUserStagePositionEvent(PVector position) {
        StagePosition stagePosition = new StagePosition();

        float left = stageBounds.getLeft();
        float right = stageBounds.getRight();
        stagePosition.setFromLeftPercent(mapWithin1(position.x, left, right));

        float bottom = stageBounds.getBottom();
        float top = stageBounds.getTop();
        stagePosition.setFromBottomPercent(mapWithin1(position.y, bottom, top));

        float front = stageBounds.getFront();
        float back = stageBounds.getBack();
        stagePosition.setFromFrontPercent(mapWithin1(position.z, front, back));

        for (BodyLocatorListener listener : this.listeners) {
            listener.userDidMove(stagePosition);
        }
    }

    private float mapWithin1(float value, float min, float max) {
        return map(value, min, max, 0, 1);
    }

    private float map(float value,
                      float start1,
                      float stop1,
                      float start2,
                      float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    private void fireUserDidEnterZoneEvent(StageZone stageZone) {
        for (BodyLocatorListener listener : this.listeners) {
            listener.userDidEnteredZone(stageZone);
        }
    }

    public StageZone getStageZoneById(String ID) {
        return stageZoneById.get(ID);
    }

    public boolean isWithinZone(String zoneID, PVector position) {
        StageZone zone = stageZoneById.get(zoneID);
        return zone != null && zone.isWithinBounds(position);
    }

    public boolean isWithinCenter(PVector position) {
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
                    stageCenter.x,
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
        boolean ignoreYAxis = true;

        public boolean isWithinXAxis(PVector position) {
            return position.x < leftBottomFront.x && position.x > rightTopBack.x;
        }

        public boolean isWithinYAxis(PVector position) {
            return ignoreYAxis || (position.y < leftBottomFront.y && position.y > rightTopBack.y);
        }

        public boolean isWithinZAxis(PVector position) {
            return position.z < rightTopBack.z && position.z > leftBottomFront.z;
        }

        public boolean isWithinBounds(PVector position) {
            return isWithinXAxis(position) && isWithinYAxis(position) && isWithinZAxis(position);
        }

        public void updateStageBounds(StageBounds stageBounds) {
            PVector stageCenter = stageBounds.getCenter();
            calculateBounds(stageBounds, stageCenter);
        }

        abstract void calculateBounds(StageBounds stageBounds, PVector stageCenter);

        public PVector getLeftBottomFront() {
            return leftBottomFront;
        }

        public PVector getRightTopBack() {
            return rightTopBack;
        }

        public float getDepth() {
            return rightTopBack.z - leftBottomFront.z;
        }

        public float getWidth() {
            return Math.abs(leftBottomFront.x) + Math.abs(rightTopBack.x);
        }
    }

}
