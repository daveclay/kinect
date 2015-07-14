package com.daveclay.processing.gestures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import processing.core.PVector;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GeometricRecognizerTest {

    GeometricRecognizer recognizer;
    GestureDataStore actualGestureData;
    GestureDataStore templateGestureData;

    @Before
    public void setUp() {
        recognizer = new GeometricRecognizer();
        templateGestureData = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        templateGestureData.load();

        actualGestureData = new GestureDataStore(GestureDataStore.GESTURE_DIR + "../tests/");
        actualGestureData.load();

        //recognizer.addTemplate("Slash", templateGestureData.getPointsByName("RightToLeftSlashDown"));
    }


    @Test
    public void shouldMatchOwnReversedData() {
        recognizer.addTemplate("Line", templateGestureData.getPointsByName("RightToLeftLine2"));
        List<PVector> template = templateGestureData.getPointsByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched own reversed gesture data " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchOwnData() {
        recognizer.addTemplate("Line", templateGestureData.getPointsByName("LeftToRightLine2"));
        List<PVector> template = templateGestureData.getPointsByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(90));
        System.out.println("Matched own gesture data " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchReverseLineGesture() {
        recognizer.addTemplate("Line", templateGestureData.getPointsByName("LeftToRightLine2"));
        List<PVector> template = actualGestureData.getPointsByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchLineGesture() {
        recognizer.addTemplate("Line", templateGestureData.getPointsByName("RightToLeftLine2"));
        List<PVector> template = actualGestureData.getPointsByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(50));
        System.out.println("Matched " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }
}
