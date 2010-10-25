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

package uk.org.lidalia.sysoutslf4j.system;

import java.io.PrintStream;


class SLF4JPrintStreamDelegate {
	
	private final PrintStream originalPrintStream;
	private final LoggerAppenderStore loggerAppenderStore;

	SLF4JPrintStreamDelegate(final PrintStream originalPrintStream, final LoggerAppenderStore loggerAppenderStore) {
		super();
		this.originalPrintStream = originalPrintStream;
		this.loggerAppenderStore = loggerAppenderStore;
	}

	void registerLoggerAppender(final LoggerAppender loggerAppender) {
		loggerAppenderStore.put(loggerAppender);
	}
	
	void deregisterLoggerAppender() {
		loggerAppenderStore.remove();
	}

	void delegatePrintln(final String message) {
		final LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.println(message);
		} else {
			appendAndLog(message, loggerAppender);
		}
	}

	void delegatePrint(final String message) {
		final LoggerAppender loggerAppender = loggerAppenderStore.get();
		if (loggerAppender == null) {
			originalPrintStream.print(message);
		} else if (message.endsWith("\n")) {
			final String messageWithoutLineBreak = StringUtils.stripEnd(message, "\r\n");
			appendAndLog(messageWithoutLineBreak, loggerAppender);
		} else {
			loggerAppender.append(message);
		}
	}

	private static void appendAndLog(final String message, final LoggerAppender loggerAppender) {
		final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		final String libraryPackageName = "uk.org.lidalia.sysoutslf4j";
		final CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, libraryPackageName);
		loggerAppender.appendAndLog(message, callOrigin.getClassName(), callOrigin.isPrintingStackTrace());
	}
}
