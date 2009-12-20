package org.slf4j.sysoutslf4j.system;

import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.sysoutslf4j.common.LoggerAppender;

class SLF4JPrintStreamDelegater {
	
	private final PrintStream originalPrintStream;
	private final LoggerAppenderStore loggerAppenderStore;

	SLF4JPrintStreamDelegater(PrintStream originalPrintStream, LoggerAppenderStore loggerAppenderStore) {
		super();
		this.originalPrintStream = originalPrintStream;
		this.loggerAppenderStore = loggerAppenderStore;
	}

	void registerLoggerAppender(Object loggerAppenderObject) {
		loggerAppenderStore.set(loggerAppenderObject);
	}

	void delegatePrintln(String message) {
		LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.println(message);
		} else {
			appendAndLog(message, loggerAppender);
		}
	}

	void delegatePrint(String message) {
		LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.print(message);
		} else if (message.endsWith("\n")) {
			String messageWithoutLineBreak = StringUtils.stripEnd(message, "\r\n");
			appendAndLog(messageWithoutLineBreak, loggerAppender);
		} else {
			loggerAppender.append(message);
		}
	}

	private void appendAndLog(String message, LoggerAppender loggerAppender) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String libraryPackageName = "org.slf4j.sysoutslf4j";
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, libraryPackageName);
		loggerAppender.appendAndLog(message, callOrigin.getClassName(), callOrigin.isStackTrace());
	}
}
