package org.slf4j.sysoutslf4j.system;

import java.lang.reflect.Method;

import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;

final class LoggerAppenderProxy implements LoggerAppender {
	
	private final Object targetLoggerAppender;
	private final Method appendMethod;
	private final Method appendAndLogMethod;

	private LoggerAppenderProxy(final Object targetLoggerAppender) {
		super();
		try {
			final Class<?> loggerAppenderClass = targetLoggerAppender.getClass();
			this.targetLoggerAppender = targetLoggerAppender;
			this.appendMethod = loggerAppenderClass.getDeclaredMethod("append", String.class);
			this.appendAndLogMethod =
				loggerAppenderClass.getDeclaredMethod("appendAndLog", String.class, String.class, boolean.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Must only be instantiated with a LoggerAppender instance, got a " + targetLoggerAppender.getClass(), e);
		}
	}

	public void append(final String message) {
		ReflectionUtils.invokeMethod(appendMethod, targetLoggerAppender, message);
	}

	public void appendAndLog(final String message, final String className, final boolean isStackTrace) {
		ReflectionUtils.invokeMethod(appendAndLogMethod, targetLoggerAppender, message, className, isStackTrace);
	}
	
	static LoggerAppender wrap(final Object targetLoggerAppender) {
		final LoggerAppender result;
		if (targetLoggerAppender instanceof LoggerAppender) {
			result = (LoggerAppender) targetLoggerAppender;
		} else {
			result = new LoggerAppenderProxy(targetLoggerAppender);
		}
		return result;
	}
}
