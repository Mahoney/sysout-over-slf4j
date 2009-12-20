package org.slf4j.sysoutslf4j.context;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ClassLoaderUtils;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SystemOutput;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.system.Wirer;

class SLF4JPrintStreamManager {

	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	void sendSystemOutAndErrToSLF4J(ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		synchronized (System.class) {
			makeSystemOutputsSLF4JPrintStreamsIfNecessary();
			sendSystemOutAndErrToSLF4JForThisContext(exceptionHandlingStrategyFactory);
			LOG.info("Redirected System.out and System.err to SLF4J");
		}
	}

	private void makeSystemOutputsSLF4JPrintStreamsIfNecessary() {
		if (systemOutputsAreNotSLF4JPrintStreams()) {
			makeSystemOutputsSLF4JPrintStreams();
			LOG.info("Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams");
		}
	}

	private boolean systemOutputsAreNotSLF4JPrintStreams() {
		return !systemOutputsAreSLF4JPrintStreams();
	}

	private boolean systemOutputsAreSLF4JPrintStreams() {
		return System.out.getClass().getName().equals(SLF4JPrintStream.class.getName());
	}

	private void makeSystemOutputsSLF4JPrintStreams() {
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Wirer.class);
		Class<?> wirerClass = ClassLoaderUtils.loadClass(classLoader, Wirer.class);
		ReflectionUtils.invokeStaticMethod("replaceSystemOutputsWithSLF4JPrintStreamsIfNecessary", wirerClass);
	}

	private void sendSystemOutAndErrToSLF4JForThisContext(
			ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			registerNewLoggerAppender(exceptionHandlingStrategyFactory, systemOutput);
		}
	}

	private void registerNewLoggerAppender(
			ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory, SystemOutput systemOutput) {
		PrintStream originalPrintStream = getOriginalPrintStream(systemOutput.get());
		ExceptionHandlingStrategy exceptionHandlingStrategy = 
			exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(systemOutput.getLogLevel(), originalPrintStream);
		LoggerAppenderImpl loggerAppender = 
			new LoggerAppenderImpl(systemOutput.getLogLevel(), exceptionHandlingStrategy, originalPrintStream);
		registerLoggerAppender(systemOutput, loggerAppender);
	}

	private PrintStream getOriginalPrintStream(PrintStream slf4jPrintStream) {
		return (PrintStream) ReflectionUtils.invokeMethod("getOriginalPrintStream", slf4jPrintStream);
	}

	private void registerLoggerAppender(SystemOutput systemOutput, LoggerAppenderImpl loggerAppender) {
		ReflectionUtils.invokeMethod("registerLoggerAppender", systemOutput.get(), Object.class, loggerAppender);
	}

	void sendSystemOutAndErrToOriginals() {
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Wirer.class, System.out.getClass().getClassLoader());
		Class<?> wirerClass = ClassLoaderUtils.loadClass(classLoader, Wirer.class);
		ReflectionUtils.invokeStaticMethod("restoreOriginalSystemOutputsIfNecessary", wirerClass);
		LOG.info("Restored original System.out and System.err");
	}
}
