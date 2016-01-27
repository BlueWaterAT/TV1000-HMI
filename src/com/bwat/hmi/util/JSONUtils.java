package com.bwat.hmi.util;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility functions to handling JSON data
 *
 * @author Kareem El-Faramawi
 */
public final class JSONUtils {

    /**
     * Loads a JSON object from a file at a given path
     *
     * @param path Path to JSON file
     * @return JSON object
     */
    public static JSONObject loadObjectFromFile(String path) {
        return new JSONObject(FileUtils.readToString(FileUtils.getFile(path)));
    }

    /**
     * Loads a JSON array from a file at a given path
     *
     * @param path Path to JSON file
     * @return JSON array
     */
    public static JSONArray loadArrayFromFile(String path) {
        return new JSONArray(FileUtils.readToString(FileUtils.getFile(path)));
    }
}
