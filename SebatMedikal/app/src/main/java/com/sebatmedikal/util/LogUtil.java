package com.sebatmedikal.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

	public static void logMessage(Class<?> clazz, String message) {
		Logger.getLogger(clazz.getName()).log(Level.INFO, "BYYPIPO----------> " + message);
	}

	public static void logMessage(Class<?> clazz, Level level, String message) {
		Logger.getLogger(clazz.getName()).log(level, "BYYPIPO----------> " + message);
	}
}
