package com.bwat.hmi.util;

import java.util.Arrays;

/**
 * Utility functions for dealing with Java Arrays
 *
 * @author Kareem El-Faramawi
 */
public class ArrayUtils {

    /**
     * Appends a list of values to the end of an array
     *
     * @param arr  Original array
     * @param vals New values
     * @return A new array containing the original with the new values appended to the end
     */
    public static <T> T[] append(T[] arr, T... vals) {
        if (arr == null) {
            return vals;
        }
        if (vals == null) {
            return arr;
        }
        T[] app = Arrays.copyOf(arr, arr.length + vals.length);
        for (int i = arr.length; i < app.length; i++) {
            app[i] = vals[i - arr.length];
        }
        return app;
    }

    /**
     * Prepends a list of values to the end of an array
     *
     * @param arr  Original array
     * @param vals New values
     * @return A new array containing the original with the new values prepended
     */
    public static <T> T[] prepend(T[] arr, T... vals) {
        return append(vals, arr);
    }

    public static <T> String join(T[] arr, String sep) {
        String out = "";
        for (T val : arr) {
            out += val.toString() + sep;
        }
        return out.substring(0, out.lastIndexOf(sep));
    }

    public static <T> T[] reverse(T[] arr) {
        T[] rev = Arrays.copyOf(arr, arr.length);
        for (int i = 0; i < rev.length; i++) {
            rev[i] = arr[arr.length - i - 1];
        }
        return rev;
    }
}
