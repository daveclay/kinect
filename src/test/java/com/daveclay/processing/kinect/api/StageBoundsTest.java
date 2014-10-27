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
public class StageBoundsTest {

    private StageBounds stageBounds;

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
        stageBounds = new StageBounds();

        // random space, somewhat like kinect numbers
        frontLeftBottom.set(left, bottom, front);
        frontRightBottom.set(right, bottom + 10, 850);
        backLeftTop.set(left - 50, top, back);
        backRightTop.set(right + 50, top - 10, 2250);

        position = new PVector();
    }

    @Test
    public void shouldHaveUpdateBounds() {
        givenTheStageIsCalibrated();
        assertThat(stageBounds.getFront(), equalTo(front));
        assertThat(stageBounds.getBack(), equalTo(back));
        assertThat(stageBounds.getLeft(), equalTo(left));
        assertThat(stageBounds.getRight(), equalTo(right));
    }

    private void givenTheStageIsCalibrated() {
        for (PVector position: Arrays.asList(frontLeftBottom, frontRightBottom, backLeftTop, backRightTop)) {
            stageBounds.expandStageBounds(position);
        }
    }
}
