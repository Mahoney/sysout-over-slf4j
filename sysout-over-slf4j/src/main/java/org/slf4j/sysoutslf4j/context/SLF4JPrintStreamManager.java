package org.slf4j.sysoutslf4j.context;

import static java.lang.ClassLoader.getSystemClassLoader;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ClassLoaderUtils;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SystemOutput;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;

class SLF4JPrintStreamManager {

	private final Logger log = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	void sendSystemOutAndErrToSLF4J(final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		synchronized (System.class) {
			makeSystemOutputsSLF4JPrintStreamsIfNecessary();
			sendSystemOutAndErrToSLF4JForThisContext(exceptionHandlingStrategyFactory);
			log.info("Redirected System.out and System.err to SLF4J");
		}
	}

	private void makeSystemOutputsSLF4JPrintStreamsIfNecessary() {
		if (systemOutputsAreSLF4JPrintStreams()) {
			log.info("System.out and System.err are already SLF4JPrintStreams");
		} else {
			makeSystemOutputsSLF4JPrintStreams();
			log.info("Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams");
		}
	}

	private boolean systemOutputsAreSLF4JPrintStreams() {
		return System.out.getClass().getName().equals(SLF4JPrintStream.class.getName());
	}

	private void makeSystemOutputsSLF4JPrintStreams() {
		final ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(SLF4JPrintStreamConfigurator.class);
		final Class<?> slf4jPrintStreamConfiguratorClass =
			ClassLoaderUtils.loadClass(classLoader, SLF4JPrintStreamConfigurator.class);
		ReflectionUtils.invokeStaticMethod("replaceSystemOutputsWithSLF4JPrintStreams", slf4jPrintStreamConfiguratorClass);
	}

	private void sendSystemOutAndErrToSLF4JForThisContext(
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			registerNewLoggerAppender(exceptionHandlingStrategyFactory, systemOutput);
		}
	}

	private void registerNewLoggerAppender(
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory, final SystemOutput systemOutput) {
		final PrintStream originalPrintStream = getOriginalPrintStream(systemOutput.get());
		final ExceptionHandlingStrategy exceptionHandlingStrategy = 
			exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(systemOutput.getLogLevel(), originalPrintStream);
		final LoggerAppenderImpl loggerAppender = 
			new LoggerAppenderImpl(systemOutput.getLogLevel(), exceptionHandlingStrategy, originalPrintStream);
		registerLoggerAppender(systemOutput, loggerAppender);
	}

	private PrintStream getOriginalPrintStream(final PrintStream slf4jPrintStream) {
		return (PrintStream) ReflectionUtils.invokeMethod("getOriginalPrintStream", slf4jPrintStream);
	}

	private void registerLoggerAppender(final SystemOutput systemOutput, final LoggerAppenderImpl loggerAppender) {
		ReflectionUtils.invokeMethod("registerLoggerAppender", systemOutput.get(), Object.class, loggerAppender);
	}

	void sendSystemOutAndErrToOriginalsIfNecessary() {
		if (systemOutputsAreSLF4JPrintStreams()) {
			sendSystemOutAndErrToOriginals();
			log.info("Restored original System.out and System.err");
		} else {
			log.warn("System.out and System.err are not SLF4JPrintStreams - cannot restore");
		}
	}

	private void sendSystemOutAndErrToOriginals() {
		final ClassLoader classLoader = 
			ClassLoaderUtils.makeNewClassLoaderForJar(SLF4JPrintStreamConfigurator.class, getSystemClassLoader());
		final Class<?> wirerClass = ClassLoaderUtils.loadClass(classLoader, SLF4JPrintStreamConfigurator.class);
		ReflectionUtils.invokeStaticMethod("restoreOriginalSystemOutputsIfNecessary", wirerClass);
	}
}