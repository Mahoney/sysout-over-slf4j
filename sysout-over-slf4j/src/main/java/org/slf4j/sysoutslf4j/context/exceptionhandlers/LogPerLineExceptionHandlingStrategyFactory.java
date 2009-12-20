package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.sysoutslf4j.context.LogLevel;

public final class LogPerLineExceptionHandlingStrategyFactory implements ExceptionHandlingStrategyFactory {

	private static final ExceptionHandlingStrategyFactory INSTANCE = new LogPerLineExceptionHandlingStrategyFactory();
	public static ExceptionHandlingStrategyFactory getInstance() {
		return INSTANCE;
	}

	private LogPerLineExceptionHandlingStrategyFactory() {
		super();
	}
	
	public ExceptionHandlingStrategy makeExceptionHandlingStrategy(LogLevel logLevel, PrintStream originalPrintStream) {
		return new LogPerLineExceptionHandlingStrategy(logLevel, originalPrintStream);
	}
	
	private static final class LogPerLineExceptionHandlingStrategy implements ExceptionHandlingStrategy {

		private static final Marker MARKER = MarkerFactory.getMarker("stacktrace");
		
		private final LogLevel logLevel;

		private LogPerLineExceptionHandlingStrategy(LogLevel logLevel, PrintStream originalPrintStream) {
			super();
			this.logLevel = logLevel;
		}

		public void notifyNotStackTrace() {
			// Do nothing
		}

		public void handleExceptionLine(String line, Logger log) {
			logLevel.log(log, MARKER, line);
		}

	}
}
