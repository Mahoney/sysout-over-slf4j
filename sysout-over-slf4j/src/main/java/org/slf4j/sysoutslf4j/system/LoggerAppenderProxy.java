package org.slf4j.sysoutslf4j.system;

import java.lang.reflect.Method;

import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;

class LoggerAppenderProxy implements LoggerAppender {
	
	private final Object targetLoggerAppender;
	private final Method appendMethod;
	private final Method appendAndLogMethod;

	LoggerAppenderProxy(final Object targetLoggerAppender) {
		super();
		try {
			final Class<?> loggerAppenderClass = targetLoggerAppender.getClass();
			this.targetLoggerAppender = targetLoggerAppender;
			this.appendMethod = loggerAppenderClass.getDeclaredMethod("append", String.class);
			this.appendAndLogMethod =
				loggerAppenderClass.getDeclaredMethod("appendAndLog", String.class, String.class, boolean.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Must only be instantiated with a LoggerAppenderImpl instance, got a " + targetLoggerAppender.getClass(), e);
		}
	}

	public void append(final String message) {
		ReflectionUtils.invokeMethod(appendMethod, targetLoggerAppender, message);
	}

	public void appendAndLog(final String message, final String className, final boolean isStackTrace) {
		ReflectionUtils.invokeMethod(appendAndLogMethod, targetLoggerAppender, message, className, isStackTrace);
	}
}