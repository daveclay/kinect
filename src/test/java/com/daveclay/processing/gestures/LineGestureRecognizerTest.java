package com.daveclay.processing.gestures;

import com.daveclay.processing.gestures.utils.Rotate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import processing.core.PVector;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LineGestureRecognizerTest {

    LineGestureRecognizer recognizer;
    GestureDataStore actualGestureData;
    GestureDataStore recordedGestureData;
    GestureDataStore templateGestureData;

    @Before
    public void setUp() {
        recognizer = new LineGestureRecognizer();
        recognizer.addRecognizerAlgorithm("Line", new LineGestureRecognizer.LeftToRightLineRecognizer());


        templateGestureData = new GestureDataStore(GestureDataStore.GESTURE_DIR);
        templateGestureData.load();

        actualGestureData = new GestureDataStore(GestureDataStore.GESTURE_DIR + "../tests/");
        actualGestureData.load();

        recordedGestureData = new GestureDataStore(GestureDataStore.GESTURE_DIR + "../recorded/");
        recordedGestureData.load();
    }

    @Test
    public void shouldMatchVerticalLine() {
        recognizer.addRecognizerAlgorithm("BottomToTopLine", LineGestureRecognizer.BOTTOM_TO_TOP_LINE_RECOGNIZER);

        GestureData gestureData = actualGestureData.getGestureByName("LeftToRightActualGesture");
        List<PVector> rotated = Rotate.rotateBy(gestureData.getPoints(), 90);

        RecognitionResult result = recognizer.recognize(rotated);
        logMatch(gestureData, result);
        assertThat(result.name, equalTo("BottomToTopLine"));
    }

    @Test
    public void shouldNotHaveNegativeScore() {
        List<PVector> template = recordedGestureData.getPointsByName("ActualGesture19");
        RecognitionResult result = recognizer.recognize(template);
        assertThat(result.getScorePercent(), greaterThan(0));
        System.out.println("Matched template " + result.name + " with " + result.getScorePercent() + "%");
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldRunThroughAllTemplateGestures() {

        for (GestureData template : templateGestureData.getAll()) {
            RecognitionResult result = recognizer.recognize(template.getPoints());
            System.out.println("Template " + template.name + " scored " + result.name + " with " + result.getScorePercent() + "%");
            assertThat(result.name, equalTo("Line"));
        }
    }

    @Test
    public void shouldRunThroughAllActualGestures() {

        for (GestureData template : recordedGestureData.getAll()) {
            RecognitionResult result = recognizer.recognize(template.getPoints());
            System.out.println("Template " + template.name + " scored " + result.name + " with " + result.getScorePercent() + "%");
            assertThat(result.name, equalTo("Line"));
        }
    }

    @Test
    public void shouldNotMatchCirlceGesture() {
        GestureData gestureData = templateGestureData.getGestureByName("Circle");
        RecognitionResult result = recognizer.recognize(gestureData);
        logNotMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldNotMatchRightToLeftLineActualGesture() {
        GestureData gestureData = actualGestureData.getGestureByName("RightToLeftActualGesture");
        RecognitionResult result = recognizer.recognize(gestureData);
        logNotMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchLeftToRightLineActualGesture() {
        GestureData gestureData = actualGestureData.getGestureByName("LeftToRightActualGesture");
        RecognitionResult result = recognizer.recognize(gestureData);
        logMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchLeftToRightLineTemplateGesture() {
        GestureData gestureData = templateGestureData.getGestureByName("LeftToRightLine2");
        RecognitionResult result = recognizer.recognize(gestureData);
        logMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldNotMatchReversedLineTemplateGesture() {
        GestureData gestureData = templateGestureData.getGestureByName("RightToLeftLine2");
        RecognitionResult result = recognizer.recognize(gestureData);
        logNotMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    @Test
    public void shouldMatchActualLineGesture() {
        GestureData gestureData = actualGestureData.getGestureByName("ActualGesture3");
        RecognitionResult result = recognizer.recognize(gestureData);
        logMatch(gestureData, result);
        assertThat(result.name, equalTo("Line"));
    }

    void logNotMatch(GestureData gestureData, RecognitionResult result) {
        log(gestureData, false, result);
        assertThat(result.getScorePercent(), lessThan(60));
    }

    void logMatch(GestureData gestureData, RecognitionResult result) {
        log(gestureData, true, result);
        assertThat(result.getScorePercent(), greaterThan(59));
    }

    void log(GestureData gestureData, boolean should, RecognitionResult result) {
        System.out.println("Should " + (should ? "" : "NOT ") + "match " + gestureData.name + ": gesture matched: " + result.name + " with " + result.getScorePercent() + "%");
    }
}
