package org.slf4j.sysoutslf4j.context;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

public class LoggerAppenderImpl implements LoggerAppender {

	private final LogLevel level;
	private final ExceptionHandlingStrategy exceptionHandlingStrategy;
	private final PrintStream originalPrintStream;
	private StringBuilder buffer = new StringBuilder();

	LoggerAppenderImpl(final LogLevel level, final ExceptionHandlingStrategy exceptionHandlingStrategy,
			final PrintStream originalPrintStream) {
		super();
		this.level = level;
		this.exceptionHandlingStrategy = exceptionHandlingStrategy;
		this.originalPrintStream = originalPrintStream;
	}

	public void append(final String message) {
		exceptionHandlingStrategy.notifyNotStackTrace();
		buffer.append(message);
	}

	public void appendAndLog(final String message, final String className, final boolean isStackTrace) {
		buffer.append(message);
		final String logStatement = flushBuffer();
		logOrPrint(logStatement, className, isStackTrace);
	}

	private String flushBuffer() {
		final String logStatement = buffer.toString();
		buffer = new StringBuilder();
		return logStatement;
	}

	private void logOrPrint(final String logStatement, final String className, final boolean isStackTrace) {
		if (SysOutOverSLF4J.isInLoggingSystem(className)) {
			originalPrintStream.println(logStatement);
		} else {
			log(logStatement, className, isStackTrace);
		}
	}

	private void log(final String logStatement, final String className, final boolean isStackTrace) {
		final Logger log = LoggerFactory.getLogger(className);
		if (isStackTrace) {
			exceptionHandlingStrategy.handleExceptionLine(logStatement, log);
		} else {
			exceptionHandlingStrategy.notifyNotStackTrace();
			level.log(log, logStatement);
		}
	}

}
