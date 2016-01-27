package com.bwat.hmi.util;

/**
 * String utility functions
 *
 * @author Kareem El-Faramawi
 */
public class StringUtils {
    static final String REG_NUM = "-?\\d+"; // Regex for a number
    static final String REG_COLR_HEX = "#[0-9A-Fa-f]{6}"; // Regex for a hex color

    /**
     * @param str String to verify
     * @return If the given String is a number
     */
    public static boolean isNumber(String str) {
        return str != null && str.trim().matches(REG_NUM);
    }

    public static boolean isHexColor(String str) {
        return str != null && str.trim().matches(REG_COLR_HEX);
    }
}
