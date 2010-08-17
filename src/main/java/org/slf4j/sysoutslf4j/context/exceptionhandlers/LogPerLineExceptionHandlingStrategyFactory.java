package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.sysoutslf4j.context.LogLevel;

/**
 * Implementation of {@link LogPerLineExceptionHandlingStrategyFactory} which returns an
 * {@link ExceptionHandlingStrategy} that simply logs each line of the stack trace as a separate logging event.
 */
public final class LogPerLineExceptionHandlingStrategyFactory implements ExceptionHandlingStrategyFactory {

	private static final ExceptionHandlingStrategyFactory INSTANCE = new LogPerLineExceptionHandlingStrategyFactory();
	public static ExceptionHandlingStrategyFactory getInstance() {
		return INSTANCE;
	}

	private LogPerLineExceptionHandlingStrategyFactory() {
		super();
	}
	
	public ExceptionHandlingStrategy makeExceptionHandlingStrategy(
			final LogLevel logLevel, final PrintStream originalPrintStream) {
		return new LogPerLineExceptionHandlingStrategy(logLevel);
	}

	private static final class LogPerLineExceptionHandlingStrategy implements ExceptionHandlingStrategy {

		private static final Marker MARKER = MarkerFactory.getMarker("stacktrace");
		
		private final LogLevel logLevel;

		LogPerLineExceptionHandlingStrategy(final LogLevel logLevel) {
			super();
			this.logLevel = logLevel;
		}

		/**
		 * This method is not used since nothing is buffered
		 */
		public void notifyNotStackTrace() {
			// Do nothing
		}

		public void handleExceptionLine(final String line, final Logger log) {
			logLevel.log(log, MARKER, line);
		}

	}
}
