package com.rwtema.denseores.utils;

import net.minecraft.util.MathHelper;

public class ColorHelper {
    public static int multiply(int col, float mult) {
        return makeCol(
                clamp(getRed(col) * mult),
                clamp(getGreen(col)*mult),
                clamp(getBlue(col)*mult),
                getAlpha(col));
    }

    public static int clamp(float c) {
        return (int) MathHelper.clamp_float(c, 0, 255);
    }

    public static int getAlpha(int col) {
        return (col & 0xff000000) >>> 24;
    }

    public static int getRed(int col) {
        return (col & 0x00ff0000) >> 16;
    }

    public static int getGreen(int col) {
        return (col & 0x0000ff00) >> 8;
    }

    public static int getBlue(int col) {
        return col & 0x000000ff;
    }

    public static int makeCol(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // check to see if the two pixels are not exactly the same but 'close'
    public static boolean areColorsClose(int a, int b) {
        return areColorsClose(a, b, 20);
    }

    // check to see if the two pixels are not exactly the same but 'close'
    public static boolean areColorsClose(int a, int b, int threshold){
        if(a == b) return true;
        int dr = Math.abs(getRed(a) - getRed(b));
        int dg = Math.abs(getGreen(a) - getGreen(b));
        int db = Math.abs(getBlue(a) - getBlue(b));

        return (dr + dg + db) < threshold;
    }
}
