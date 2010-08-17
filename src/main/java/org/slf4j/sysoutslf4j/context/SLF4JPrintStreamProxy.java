package org.slf4j.sysoutslf4j.context;

import java.io.PrintStream;
import java.lang.reflect.Method;

import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

final class SLF4JPrintStreamProxy implements SLF4JPrintStream {
	
	private final Object targetSLF4JPrintStream;
	private final Method getOriginalPrintStreamMethod;
	private final Method registerLoggerAppenderMethod;
	private final Method deregisterLoggerAppenderMethod;

	private SLF4JPrintStreamProxy(final Object targetSLF4JPrintStream) {
		super();
		try {
			final Class<?> loggerAppenderClass = targetSLF4JPrintStream.getClass();
			this.targetSLF4JPrintStream = targetSLF4JPrintStream;
			this.getOriginalPrintStreamMethod = loggerAppenderClass.getDeclaredMethod("getOriginalPrintStream");
			this.registerLoggerAppenderMethod = loggerAppenderClass.getDeclaredMethod("registerLoggerAppender", Object.class);
			this.deregisterLoggerAppenderMethod = loggerAppenderClass.getDeclaredMethod("deregisterLoggerAppender");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Must only be instantiated with an SLF4JPrintStream instance, got a " + targetSLF4JPrintStream.getClass(), e);
		}
	}

	public PrintStream getOriginalPrintStream() {
		return (PrintStream) ReflectionUtils.invokeMethod(getOriginalPrintStreamMethod, targetSLF4JPrintStream);
	}
	
	public void registerLoggerAppender(final Object loggerAppender) {
		ReflectionUtils.invokeMethod(registerLoggerAppenderMethod, targetSLF4JPrintStream, loggerAppender);
	}
	
	public void deregisterLoggerAppender() {
		ReflectionUtils.invokeMethod(deregisterLoggerAppenderMethod, targetSLF4JPrintStream);
	}
	
	static SLF4JPrintStream wrap(final Object targetPrintStream) {
		final SLF4JPrintStream result;
		if (targetPrintStream instanceof SLF4JPrintStream) {
			result = (SLF4JPrintStream) targetPrintStream;
		} else {
			result = new SLF4JPrintStreamProxy(targetPrintStream);
		}
		return result;
	}
}
