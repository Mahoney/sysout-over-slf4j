package org.slf4j.sysoutslf4j.system;

import java.io.PrintStream;

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
			String messageWithoutLineBreak = stripEnd(message, "\r\n");
			appendAndLog(messageWithoutLineBreak, loggerAppender);
		} else {
			loggerAppender.append(message);
		}
	}

	private static void appendAndLog(String message, LoggerAppender loggerAppender) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String libraryPackageName = "org.slf4j.sysoutslf4j";
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, libraryPackageName);
		loggerAppender.appendAndLog(message, callOrigin.getClassName(), callOrigin.isStackTrace());
	}
	
	private static String stripEnd(final String str, final String stripChars) {
		return stripEnd(str, str.length() - 1, stripChars);
	}
	
	private static String stripEnd(final String string, final int index, final String stripChars) {
		if (index == -1) {
			return "";
		}
		final char candidateToBeStripped = string.charAt(index);
		final boolean candidateShouldNotBeStripped = stripChars.indexOf(candidateToBeStripped) == -1;
		if (candidateShouldNotBeStripped) {
			return string.substring(0, index + 1);
		} else {
			return stripEnd(string, index - 1, stripChars);
		}
	}
}
