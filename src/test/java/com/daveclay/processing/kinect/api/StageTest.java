package com.daveclay.processing.kinect.api;

import com.daveclay.processing.kinect.bodylocator.BodyLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import processing.core.PVector;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StageTest {

    private Stage stage;
    private float front = 800;
    private float back = 2300;
    private float left = 600;
    private float right = -300;
    private float top = -80;
    private float bottom = -300;
    private float centerX = (left + right) / 2;
    private float centerZ = (front + back) / 2;

    private PVector frontLeftBottom = new PVector();
    private PVector frontRightBottom = new PVector();
    private PVector backLeftTop = new PVector();
    private PVector backRightTop = new PVector();

    private PVector position;

    @Before
    public void setUp() {
        stage = new Stage();
        stage.setupDefaultStageZones();

        // random space, somewhat like kinect numbers
        frontLeftBottom.set(left, bottom, front);
        frontRightBottom.set(right, bottom + 10, 850);
        backLeftTop.set(left - 50, top, back);
        backRightTop.set(right + 50, top - 10, 2250);

        position = new PVector();
    }

    @Test
    public void shouldUpdateWhichZoneTheUserIsIn() {
        givenTheStageIsCalibrated();

        BodyLocator.Listener listener = mock(BodyLocator.Listener.class);
        stage.addListener(listener);

        position.set(centerX + 100, top - 50, centerZ + 100);
        stage.updatePosition(position);
        verify(listener, times(1)).userDidEnteredZone(stage.getStageZoneById("Center"));
        reset(listener);
        stage.updatePosition(position);
        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Center"));
        reset(listener);
        position.set(left - 50, top - 50, back - 50);
        stage.updatePosition(position);
        verify(listener, times(1)).userDidEnteredZone(stage.getStageZoneById("Left Back"));
        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Center"));
    }

    @Test
    public void shouldNotPingPongBetweenZones() {
        givenTheStageIsCalibrated();

        BodyLocator.Listener listener = mock(BodyLocator.Listener.class);
        stage.addListener(listener);

        position.set(centerX + 100, top - 50, centerZ + 100);
        stage.updatePosition(position);
        assertThat("Should be within center zone", stage.isWithinCenter(position), equalTo(true));
        assertThat("Should be within left back zone", stage.isWithinLeftBack(position), equalTo(true));

        verify(listener, times(1)).userDidEnteredZone(stage.getStageZoneById("Center"));
        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Left Back"));

        reset(listener);

        stage.updatePosition(position);
        assertThat("Should be within center zone", stage.isWithinCenter(position), equalTo(true));
        assertThat("Should be within left back zone", stage.isWithinLeftBack(position), equalTo(true));

        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Center"));
        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Left Back"));
    }

    @Test
    public void shouldBeInOnlyOneZone() {
        givenTheStageIsCalibrated();

        BodyLocator.Listener listener = mock(BodyLocator.Listener.class);
        stage.addListener(listener);

        position.set(centerX + 100, top - 50, centerZ + 100);
        stage.updatePosition(position);
        assertThat("Should be within center zone", stage.isWithinCenter(position), equalTo(true));
        assertThat("Should be within left back zone", stage.isWithinLeftBack(position), equalTo(true));

        verify(listener, times(1)).userDidEnteredZone(stage.getStageZoneById("Center"));
        verify(listener, never()).userDidEnteredZone(stage.getStageZoneById("Left Back"));
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
        assertThat(stage.isWithinLeftBack(position), equalTo(false));
        assertThat(stage.isWithinCenter(position), equalTo(false));
    }

    @Test
    public void shouldBeWithinCenter() {
        givenTheStageIsCalibrated();
        position.set(left + right, top - 50, centerZ);
        assertThat(stage.isWithinCenter(position), equalTo(true));
    }

    private void givenTheStageIsCalibrated() {
        for (PVector position: Arrays.asList(frontLeftBottom, frontRightBottom, backLeftTop, backRightTop)) {
            stage.updatePosition(position);
        }
    }
}
