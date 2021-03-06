package com.daveclay.processing.kinect.api;

import com.daveclay.processing.kinect.api.stage.Stage;
import com.daveclay.processing.kinect.api.stage.StageBounds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import processing.core.PVector;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StageZoneTest {

    private Stage.RectStageZone leftFrontStageZone;
    private Stage stage;
    private float front = 800;
    private float back = 2300;
    private float left = 600;
    private float right = -300;
    private float top = 400;
    private float bottom = 50;

    private PVector frontLeftBottom = new PVector();
    private PVector frontRightBottom = new PVector();
    private PVector backLeftTop = new PVector();
    private PVector backRightTop = new PVector();

    private PVector position;
    private StageBounds stageBounds;

    @Before
    public void setUp() {
        stage = new Stage();
        leftFrontStageZone = new Stage.LeftFrontZone();
        stageBounds = new StageBounds();

        // random space, somewhat like kinect numbers
        frontLeftBottom.set(left, bottom, front);
        frontRightBottom.set(right, bottom + 10, 850);
        backLeftTop.set(left - 50, top, back);
        backRightTop.set(right + 50, top - 10, 2250);

        position = new PVector();
    }

    @Test
    public void shouldHaveCorrectCorners() {
        givenTheStageIsCalibrated();
        PVector zoneLeftFrontBottom = leftFrontStageZone.getLeftBottomFront();
        assertThat(zoneLeftFrontBottom.x, equalTo(frontLeftBottom.x));
    }

    @Test
    public void shouldBeWithinFrontLeftZone() {
        givenTheStageIsCalibrated();
        position.set(left - 50, top - 50, front + 50);
        assertThat(leftFrontStageZone.isWithinBounds(position), equalTo(true));
    }

    @Test
    public void shouldNotBeWithinFrontLeftZoneWhenPositionIsInBack() {
        givenTheStageIsCalibrated();
        position.set(left - 50, top - 50, back - 10);
        assertThat(leftFrontStageZone.isWithinBounds(position), equalTo(false));
    }

    @Test
    public void shouldNotBeWithinFrontLeftZoneWhenPositionIsTooFarRight() {
        givenTheStageIsCalibrated();
        position.set(right + 50, top - 50, front + 50);
        assertThat(leftFrontStageZone.isWithinBounds(position), equalTo(false));
    }

    private void givenTheStageIsCalibrated() {
        for (PVector position: Arrays.asList(frontLeftBottom, frontRightBottom, backLeftTop, backRightTop)) {
            stageBounds.expandStageBounds(position);
        }
        leftFrontStageZone.updateStageBounds(stageBounds);
    }
}
