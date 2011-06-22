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

package uk.org.lidalia.sysoutslf4j.context;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.context.CallOrigin;
import uk.org.lidalia.sysoutslf4j.system.SimplePrintStream;

public class LoggerAppenderImpl implements SimplePrintStream {

	private final LogLevel level;
	private final ExceptionHandlingStrategy exceptionHandlingStrategy;
	private final PrintStream originalPrintStream;
	private final LoggingSystemRegister loggingSystemRegister;
	
	private StringBuilder buffer = new StringBuilder();

	LoggerAppenderImpl(final LogLevel level, final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory,
			final PrintStream originalPrintStream, final LoggingSystemRegister loggingSystemRegister) {
		super();
		this.level = level;
		this.exceptionHandlingStrategy =
				exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(level, originalPrintStream);
		this.originalPrintStream = originalPrintStream;
		this.loggingSystemRegister = loggingSystemRegister;
	}

	public void print(final String message) {
		exceptionHandlingStrategy.notifyNotStackTrace();
		buffer.append(message);
	}

	public void println(final String message) {		
		buffer.append(message);
		final String logStatement = flushBuffer();
		logOrPrint(logStatement);
	}

	private String flushBuffer() {
		final String logStatement = buffer.toString();
		buffer = new StringBuilder();
		return logStatement;
	}

	private void logOrPrint(final String logStatement) {
		final CallOrigin callOrigin = getCallOrigin();
		if (loggingSystemRegister.isInLoggingSystem(callOrigin.getClassName())) {
			originalPrintStream.println(logStatement);
		} else {
			log(logStatement, callOrigin.getClassName(), callOrigin.isPrintingStackTrace());
		}
	}

	private CallOrigin getCallOrigin() {
		final CallOrigin callOrigin = CallOrigin.getCallOrigin("uk.org.lidalia.sysoutslf4j");
		return callOrigin;
	}

	private void log(final String logStatement, final String className, final boolean isStackTrace) {
		final Logger log = LoggerFactory.getLogger(className);
		if (isStackTrace) {
			exceptionHandlingStrategy.handleExceptionLine(logStatement, log);
		} else {
			exceptionHandlingStrategy.notifyNotStackTrace();
			level.log(log, logStatement);
		}
	}

}
