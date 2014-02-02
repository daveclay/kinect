package com.daveclay.processing.kinect.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import processing.core.PVector;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StageTest {

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

    @Before
    public void setUp() {
        stage = new Stage();

        // random space, somewhat like kinect numbers
        frontLeftBottom.set(left, bottom, front);
        frontRightBottom.set(right, bottom + 10, 850);
        backLeftTop.set(left - 50, top, back);
        backRightTop.set(right + 50, top - 10, 2250);

        position = new PVector();
    }

    @Test
    public void shouldBeWithinFrontLeftZone() {
        givenTheStageIsCalibrated();
        position.set(left - 50, top - 50, front + 50);
        assertThat(stage.isWithinLeftFront(position), equalTo(true));
    }

    @Test
    public void shouldBeWithinFrontRightZone() {
        givenTheStageIsCalibrated();
        position.set(right + 50, top - 50, front + 10);
        assertThat(stage.isWithinLeftFront(position), equalTo(false));
        assertThat(stage.isWithinRightFront(position), equalTo(true));
    }

    @Test
    public void shouldBeWithinBackLeftZone() {
        givenTheStageIsCalibrated();
        position.set(left - 50, top - 50, back - 50);
        assertThat(stage.isWithinLeftBack(position), equalTo(true));
    }

    @Test
    public void shouldBeWithinBackRightZone() {
        givenTheStageIsCalibrated();
        position.set(right + 50, top - 50, back - 50);
        assertThat(stage.isWithinRightBack(position), equalTo(true));
        assertThat(stage.isWithinCenterZone(position), equalTo(false));
    }

    @Test
    public void shouldBeWithinCenter() {
        givenTheStageIsCalibrated();
        position.set(left + right, top - 50, (back + front) / 2);
        assertThat(stage.isWithinCenterZone(position), equalTo(true));
    }

    private void givenTheStageIsCalibrated() {
        for (PVector position: Arrays.asList(frontLeftBottom, frontRightBottom, backLeftTop, backRightTop)) {
            stage.updatePosition(position);
        }
    }
}
