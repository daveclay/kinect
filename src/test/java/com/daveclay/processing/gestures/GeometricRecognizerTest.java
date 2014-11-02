package com.daveclay.processing.gestures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GeometricRecognizerTest {

    GeometricRecognizer recognizer;
    GestureData actualGestureData;
    GestureData templateGestureData;

    @Before
    public void setUp() {
        recognizer = new GeometricRecognizer();
        templateGestureData = new GestureData(GestureData.GESTURE_DIR);
        templateGestureData.load();

        actualGestureData = new GestureData(GestureData.GESTURE_DIR + "../tests/");
        actualGestureData.load();

        //recognizer.addTemplate("Slash", templateGestureData.getByName("RightToLeftSlashDown"));
    }


    @Test
    public void shouldMatchOwnReversedData() {
        recognizer.addTemplate("Line", templateGestureData.getByName("RightToLeftLine2"));
        List<Point2D> template = templateGestureData.getByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched own reversed gesture data " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchOwnData() {
        recognizer.addTemplate("Line", templateGestureData.getByName("LeftToRightLine2"));
        List<Point2D> template = templateGestureData.getByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(90));
        System.out.println("Matched own gesture data " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchReverseLineGesture() {
        recognizer.addTemplate("Line", templateGestureData.getByName("LeftToRightLine2"));
        List<Point2D> template = actualGestureData.getByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchLineGesture() {
        recognizer.addTemplate("Line", templateGestureData.getByName("RightToLeftLine2"));
        List<Point2D> template = actualGestureData.getByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }
}
