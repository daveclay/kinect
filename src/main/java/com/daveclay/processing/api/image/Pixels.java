package com.daveclay.processing.api.image;

public interface Pixels {
    int get(int x, int y);
    void set(int x, int y, int color);
}
