package com.daveclay.processing.gestures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GestureRecognizerTest {

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

        recognizer.addTemplate("Line", templateGestureData.getByName("RightToLeftLine2"));
        recognizer.addTemplate("Slash", templateGestureData.getByName("RightToLeftSlashDown"));
    }


    @Test
    public void shouldHaveUpdateBounds() {
        List<Point2D> template = actualGestureData.getByName("ActualGesture2");
        RecognitionResult result = recognizer.recognize(template);
        assertNotNull(result);
    }
}
