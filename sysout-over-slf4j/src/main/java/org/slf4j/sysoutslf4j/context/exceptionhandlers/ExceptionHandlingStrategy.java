package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import org.slf4j.Logger;

public interface ExceptionHandlingStrategy {

	void handleExceptionLine(String line, Logger log);

	void notifyNotStackTrace();
}
