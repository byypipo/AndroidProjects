package com.sebatmedikal.util;

public class NullUtil {

    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof String) {
            String string = (String) object;

            if (string.equals("")) {
                return true;
            }

            return (string.length() == 0);
        }

        return false;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isAnyNull(Object... objects) {
        if (isNull(objects)) {
            return true;
        }

        for (Object object : objects) {
            if (isNull(object)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAllNull(Object... objects) {
        if (isNull(objects)) {
            return true;
        }

        for (Object object : objects) {
            if (isNotNull(object)) {
                return false;
            }
        }

        return true;
    }
}
