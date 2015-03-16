package com.daveclay.processing.gestures;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestureDataStore {
    public static final String GESTURE_DIR = "/Users/daveclay/work/kinect/src/main/resources/gestures/";

    private final String gestureDirectory;
    private final List<GestureData> gestures = new ArrayList<GestureData>();
    private final Map<String, GestureData> gestureDataByName = new HashMap<String, GestureData>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GestureDataStore(String gestureDirectory) {
        this.gestureDirectory = gestureDirectory;
    }

    public void load() {
        ObjectMapper objectMapper = new ObjectMapper();
        File dir = new File(gestureDirectory);
        for (File file : dir.listFiles()) {
            try {
                GestureData gestureData = objectMapper.readValue(file, GestureData.class);
                gestures.add(gestureData);
                gestureDataByName.put(gestureData.getName(), gestureData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        for (GestureData gestureData : getAll()) {
            save(gestureData);
        }
    }

    public void save(GestureData gestureData) {
        try {
            objectMapper.writeValue(new File(gestureDirectory + gestureData.name + ".json"), gestureData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GestureData getGestureByName(String name) {
        GestureData data = gestureDataByName.get(name);
        if (data == null) {
            throw new IllegalArgumentException("No such gesture '" + name + "'");
        }
        return data;
    }

    public List<Point2D> getPointsByName(String name) {
        return getGestureByName(name).getPoints();
    }

    public List<GestureData> getAll() {
        return gestures;
    }
}
