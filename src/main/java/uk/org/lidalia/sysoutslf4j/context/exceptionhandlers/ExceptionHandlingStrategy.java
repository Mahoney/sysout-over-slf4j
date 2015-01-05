/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.context.exceptionhandlers;

import org.slf4j.Logger;

/**
 * This interface defines the methods used by sysout-over-slf4j to convert the results of Throwable.printStacktrace
 * into logging events.<br>
 * 
 * Since the exception itself cannot be reclaimed, all that is available is the individual lines of the stack trace
 * as they are printed to System.out/err. Something more like normal logging might be achievable by buffering these
 * lines and constructing a new Exception from them.
 * 
 */
public interface ExceptionHandlingStrategy {

	/**
	 * Called for each line of the stack trace as sent to the System.out/err PrintStream.
	 *
	 * @param line The stacktrace line
	 * @param log The {@link org.slf4j.Logger} with a name matching the fully qualified name of the
	 * 				class where printStacktrace was called
	 */
	void handleExceptionLine(String line, Logger log);

	/**
	 * Called whenever any other calls are intercepted by sysout-over-slf4j
	 * - may be a useful trigger for flushing a buffer.
	 */
	void notifyNotStackTrace();
}
