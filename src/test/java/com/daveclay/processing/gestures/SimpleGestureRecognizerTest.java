package com.daveclay.processing.gestures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SimpleGestureRecognizerTest {

    SimpleGestureRecognizer recognizer;
    GestureData actualGestureData;
    GestureData templateGestureData;

    @Before
    public void setUp() {
        recognizer = new SimpleGestureRecognizer();
        templateGestureData = new GestureData(GestureData.GESTURE_DIR);
        templateGestureData.load();

        actualGestureData = new GestureData(GestureData.GESTURE_DIR + "../tests/");
        actualGestureData.load();
    }

    @Test
    public void shouldNotMatchCirlceGesture() {
        List<Point2D> template = templateGestureData.getByName("Circle");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), lessThan(60));
        System.out.println("Matched Cicle template " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchLeftToRightLineGesture() {
        List<Point2D> template = templateGestureData.getByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched Left-to-right line " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldNotMatchReversedLineGesture() {
        List<Point2D> template = templateGestureData.getByName("RightToLeftLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), lessThan(50));
        System.out.println("Matched right to left " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchActualLineGesture() {
        List<Point2D> template = actualGestureData.getByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched ActualGesture2 " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }
}
