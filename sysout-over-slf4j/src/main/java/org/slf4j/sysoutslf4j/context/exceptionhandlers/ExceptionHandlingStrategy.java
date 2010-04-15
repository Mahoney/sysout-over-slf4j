package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import org.slf4j.Logger;

/**
 * This interface defines the methods used by sysout-over-slf4j to convert the results of Throwable.printStacktrace
 * into logging events.<br/>
 * 
 * Since the exception itself cannot be reclaimed, all that is available is the individual lines of the stack trace
 * as they are printed to System.out/err. Something more like normal logging might be achievable by buffering these
 * lines and constructing a new Exception from them.
 * 
 */
public interface ExceptionHandlingStrategy {

	/**
	 * Called for each line of the stack trace as sent to the System.out/err PrintStream
	 *
	 * @param line The stacktrace line
	 * @param log The {@link org.slf4j.Logger} with a name matching the fully qualified name of the
	 * 				class where printStacktrace was called
	 */
	void handleExceptionLine(String line, Logger log);

	/**
	 * Called whenever any other calls are intercepted by sysout-over-slf4j
	 * - may be a useful trigger for flushing a buffer
	 */
	void notifyNotStackTrace();
}
