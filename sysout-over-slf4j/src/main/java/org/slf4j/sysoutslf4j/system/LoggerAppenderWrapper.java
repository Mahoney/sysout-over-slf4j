package org.slf4j.sysoutslf4j.system;

import org.slf4j.sysoutslf4j.common.LoggerAppender;

class LoggerAppenderWrapper {

	static LoggerAppender wrap(Object targetLoggerAppender) {
		if (targetLoggerAppender instanceof LoggerAppender) {
			return (LoggerAppender) targetLoggerAppender;
		} else {
			return new LoggerAppenderProxy(targetLoggerAppender);
		}
	}
	
	private LoggerAppenderWrapper() {
		throw new IllegalArgumentException("Not instantiable");
	}

}
