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

	LoggerAppenderImpl(LogLevel level, ExceptionHandlingStrategy exceptionHandlingStrategy,
			PrintStream originalPrintStream) {
		super();
		this.level = level;
		this.exceptionHandlingStrategy = exceptionHandlingStrategy;
		this.originalPrintStream = originalPrintStream;
	}

	public void append(String message) {
		exceptionHandlingStrategy.notifyNotStackTrace();
		buffer.append(message);
	}

	public void appendAndLog(String message, String className, boolean isStackTrace) {
		buffer.append(message);
		String logStatement = flushBuffer();
		logOrPrint(logStatement, className, isStackTrace);
	}

	private String flushBuffer() {
		String logStatement = buffer.toString();
		buffer = new StringBuilder();
		return logStatement;
	}

	private void logOrPrint(String logStatement, String className, boolean isStackTrace) {
		if (SysOutOverSLF4J.isInLoggingSystem(className)) {
			originalPrintStream.println(logStatement);
		} else {
			log(logStatement, className, isStackTrace);
		}
	}

	private void log(String logStatement, String className, boolean isStackTrace) {
		Logger log = LoggerFactory.getLogger(className);
		if (isStackTrace) {
			exceptionHandlingStrategy.handleExceptionLine(logStatement, log);
		} else {
			exceptionHandlingStrategy.notifyNotStackTrace();
			level.log(log, logStatement);
		}
	}

}
