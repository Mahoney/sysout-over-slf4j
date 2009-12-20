package org.slf4j.sysoutslf4j.common;

public interface LoggerAppender {

	void append(String message);

	void appendAndLog(String message, String className, boolean isStackTrace);

}
