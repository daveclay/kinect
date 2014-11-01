package com.daveclay.processing.gestures;

import com.daveclay.processing.kinect.api.Gesture;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestureData {
    public static final String GESTURE_DIR = "/Users/daveclay/work/kinect/projects/kinect/src/main/resources/gestures/";

    private final String gestureDirectory;
    private final List<GestureTemplate> templates = new ArrayList<GestureTemplate>();
    private final Map<String, GestureTemplate> templatesByName = new HashMap<String, GestureTemplate>();

    public GestureData(String gestureDirectory) {
        this.gestureDirectory = gestureDirectory;
    }

    public void load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File dir = new File(gestureDirectory);
        for (File file : dir.listFiles()) {
            try {
                GestureTemplate gestureTemplate = objectMapper.readValue(file, GestureTemplate.class);
                templates.add(gestureTemplate);
                templatesByName.put(gestureTemplate.getName(), gestureTemplate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        for (GestureTemplate gestureTemplate : getAll()) {
            try {
                objectMapper.writeValue(new File(gestureDirectory + gestureTemplate.name + ".json"), gestureTemplate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Point2D> getByName(String name) {
        GestureTemplate data = templatesByName.get(name);
        if (data == null) {
            throw new IllegalArgumentException("No such gesture '" + name + "'");
        }
        return data.getPoints();
    }

    public List<GestureTemplate> getAll() {
        return templates;
    }
}
