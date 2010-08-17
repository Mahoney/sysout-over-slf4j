package org.slf4j.testutils;

import org.powermock.reflect.Whitebox;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ExceptionUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LoggingUtils {

	public static void turnOffRootLogging() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.OFF);
		rootLogger.detachAndStopAllAppenders();
	}

	public static void turnOffRootLogging(ClassLoader classLoader) {
		try {
			Class<?> loggingUtils = classLoader.loadClass(LoggingUtils.class.getName());
			Whitebox.invokeMethod(loggingUtils, "turnOffRootLogging");
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}
	
	private LoggingUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
