package com.daveclay.processing.api;

public class ColorUtils {
    public static int addAlpha(int color, float alpha) {
        return ((int) (alpha * 255.0f) << 24) | (color & 0x00ffffff);
    }

    public static int addAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }
}
