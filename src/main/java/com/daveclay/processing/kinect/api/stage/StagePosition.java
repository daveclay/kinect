package com.daveclay.processing.kinect.api.stage;

public class StagePosition {

    public static StagePosition reflected(StagePosition stagePosition) {
        StagePosition reflected = new StagePosition();
        reflected.fromLeftPercent = 1f - stagePosition.fromLeftPercent;
        reflected.fromBottomPercent = stagePosition.fromBottomPercent;
        reflected.fromFrontPercent = stagePosition.fromFrontPercent;
        return reflected;
    }

    private float fromLeftPercent;
    private float fromBottomPercent;
    private float fromFrontPercent;

    public StagePosition() {
    }

    public StagePosition(float fromLeftPercent, float fromBottomPercent, float fromFrontPercent) {
        this.fromLeftPercent = fromLeftPercent;
        this.fromBottomPercent = fromBottomPercent;
        this.fromFrontPercent = fromFrontPercent;
    }

    public float getFromLeftPercent() {
        return fromLeftPercent;
    }

    public void setFromLeftPercent(float fromLeftPercent) {
        this.fromLeftPercent = fromLeftPercent;
    }

    public float getFromBottomPercent() {
        return fromBottomPercent;
    }

    public void setFromBottomPercent(float fromBottomPercent) {
        this.fromBottomPercent = fromBottomPercent;
    }

    public float getFromFrontPercent() {
        return fromFrontPercent;
    }

    public void setFromFrontPercent(float fromFrontPercent) {
        this.fromFrontPercent = fromFrontPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StagePosition that = (StagePosition) o;

        if (Float.compare(that.fromBottomPercent, fromBottomPercent) != 0) return false;
        if (Float.compare(that.fromFrontPercent, fromFrontPercent) != 0) return false;
        if (Float.compare(that.fromLeftPercent, fromLeftPercent) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (fromLeftPercent != +0.0f ? Float.floatToIntBits(fromLeftPercent) : 0);
        result = 31 * result + (fromBottomPercent != +0.0f ? Float.floatToIntBits(fromBottomPercent) : 0);
        result = 31 * result + (fromFrontPercent != +0.0f ? Float.floatToIntBits(fromFrontPercent) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StagePosition{" +
                "fromLeftPercent=" + fromLeftPercent +
                ", fromBottomPercent=" + fromBottomPercent +
                ", fromFrontPercent=" + fromFrontPercent +
                '}';
    }
}
