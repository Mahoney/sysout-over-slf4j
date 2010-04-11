package org.slf4j.sysoutslf4j.system;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.StringUtils;

class SLF4JPrintStreamDelegate {
	
	private final PrintStream originalPrintStream;
	private final LoggerAppenderStore loggerAppenderStore;

	SLF4JPrintStreamDelegate(final PrintStream originalPrintStream, final LoggerAppenderStore loggerAppenderStore) {
		super();
		this.originalPrintStream = originalPrintStream;
		this.loggerAppenderStore = loggerAppenderStore;
	}

	void registerLoggerAppender(final LoggerAppender loggerAppender) {
		loggerAppenderStore.put(loggerAppender);
	}
	
	void deregisterLoggerAppender() {
		loggerAppenderStore.remove();
	}

	void delegatePrintln(final String message) {
		final LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.println(message);
		} else {
			appendAndLog(message, loggerAppender);
		}
	}

	void delegatePrint(final String message) {
		final LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.print(message);
		} else if (message.endsWith("\n")) {
			final String messageWithoutLineBreak = StringUtils.stripEnd(message, "\r\n");
			appendAndLog(messageWithoutLineBreak, loggerAppender);
		} else {
			loggerAppender.append(message);
		}
	}

	private static void appendAndLog(final String message, final LoggerAppender loggerAppender) {
		final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		final String libraryPackageName = "org.slf4j.sysoutslf4j";
		final CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, libraryPackageName);
		loggerAppender.appendAndLog(message, callOrigin.getClassName(), callOrigin.isPrintingStackTrace());
	}
}
