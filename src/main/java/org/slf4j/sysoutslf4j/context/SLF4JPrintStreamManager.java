package org.slf4j.sysoutslf4j.context;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.common.SystemOutput;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;

class SLF4JPrintStreamManager {

	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	void sendSystemOutAndErrToSLF4J(final LogLevel outLevel, final LogLevel errLevel,
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		makeSystemOutputsSLF4JPrintStreamsIfNecessary();
		sendSystemOutAndErrToSLF4JForThisContext(outLevel, errLevel, exceptionHandlingStrategyFactory);
		LOG.info("Redirected System.out and System.err to SLF4J for this context");
	}

	private void makeSystemOutputsSLF4JPrintStreamsIfNecessary() {
		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
			LOG.debug("System.out and System.err are already SLF4JPrintStreams");
		} else {
			makeSystemOutputsSLF4JPrintStreams();
			LOG.info("Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams");
		}
	}

	private void makeSystemOutputsSLF4JPrintStreams() {
		ReflectionUtils.invokeMethod("replaceSystemOutputsWithSLF4JPrintStreams", SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass());
	}

	private void sendSystemOutAndErrToSLF4JForThisContext(final LogLevel outLevel, final LogLevel errLevel, 
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		registerNewLoggerAppender(exceptionHandlingStrategyFactory, ReflectionUtils.wrap(SystemOutput.OUT.get(), SLF4JPrintStream.class), outLevel);
		registerNewLoggerAppender(exceptionHandlingStrategyFactory, ReflectionUtils.wrap(SystemOutput.ERR.get(), SLF4JPrintStream.class), errLevel);
	}

	private void registerNewLoggerAppender(
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory,
			final SLF4JPrintStream slf4jPrintStream, final LogLevel logLevel) {

		final PrintStream originalPrintStream = slf4jPrintStream.getOriginalPrintStream();
		final ExceptionHandlingStrategy exceptionHandlingStrategy = 
			exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(logLevel, originalPrintStream);
		final Object loggerAppender = 
			new LoggerAppenderImpl(logLevel, exceptionHandlingStrategy, originalPrintStream);
		ReferenceHolder.preventGarbageCollectionForLifeOfClassLoader(loggerAppender);
		slf4jPrintStream.registerLoggerAppender(loggerAppender);
	}

	void stopSendingSystemOutAndErrToSLF4J() {
		try {
			for (SystemOutput systemOutput : SystemOutput.values()) {
				final SLF4JPrintStream slf4jPrintStream = ReflectionUtils.wrap(systemOutput.get(), SLF4JPrintStream.class);
				slf4jPrintStream.deregisterLoggerAppender();
			}
		} catch (IllegalArgumentException iae) {
			LOG.warn("Cannot stop sending System.out and System.err to SLF4J - they are not being sent there at the moment");
		}
	}

	void restoreOriginalSystemOutputsIfNecessary() {
		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
			restoreOriginalSystemOutputs();
			LOG.info("Restored original System.out and System.err");
		} else {
			LOG.warn("System.out and System.err are not SLF4JPrintStreams - cannot restore");
		}
	}

	private void restoreOriginalSystemOutputs() {
		ReflectionUtils.invokeMethod("restoreOriginalSystemOutputs", SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass());
	}

}
