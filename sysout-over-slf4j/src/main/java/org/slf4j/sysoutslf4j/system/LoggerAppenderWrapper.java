package org.slf4j.sysoutslf4j.system;

import org.slf4j.sysoutslf4j.common.LoggerAppender;

final class LoggerAppenderWrapper {

	static LoggerAppender wrap(final Object targetLoggerAppender) {
		LoggerAppender result;
		if (targetLoggerAppender instanceof LoggerAppender) {
			result = (LoggerAppender) targetLoggerAppender;
		} else {
			result = new LoggerAppenderProxy(targetLoggerAppender);
		}
		return result;
	}
	
	private LoggerAppenderWrapper() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
