package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import org.slf4j.Logger;

/**
 * This interface defines the methods used by sysout-over-slf4j to convert the results of Throwable.printStacktrace
 * into logging events.<br/>
 * 
 * Since the exception itself cannot be reclaimed, all that is available is the 
 * 
 */
public interface ExceptionHandlingStrategy {

	void handleExceptionLine(String line, Logger log);

	void notifyNotStackTrace();
}
