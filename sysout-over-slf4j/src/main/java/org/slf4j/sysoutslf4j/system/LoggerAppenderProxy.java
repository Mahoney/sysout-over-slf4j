package org.slf4j.sysoutslf4j.system;

import java.lang.reflect.Method;

import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;

class LoggerAppenderProxy implements LoggerAppender {
	
	private final Object targetLoggerAppender;
	private final Method append;
	private final Method appendAndLog;

	LoggerAppenderProxy(Object targetLoggerAppender) {
		super();
		try {
			Class<?> loggerAppenderClass = targetLoggerAppender.getClass();
			this.targetLoggerAppender = targetLoggerAppender;
			this.append = loggerAppenderClass.getDeclaredMethod("append", String.class);
			this.appendAndLog = loggerAppenderClass.getDeclaredMethod("appendAndLog", String.class, String.class, boolean.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Must only be instantiated with a LoggerAppenderImpl instance, got a " + targetLoggerAppender.getClass(), e);
		}
	}

	public void append(String message) {
		ReflectionUtils.invokeMethod(append, targetLoggerAppender, message);
	}

	public void appendAndLog(String message, String className, boolean isStackTrace) {
		ReflectionUtils.invokeMethod(appendAndLog, targetLoggerAppender, message, className, isStackTrace);
	}
}
