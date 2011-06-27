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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, CallOrigin.class, LoggingSystemRegister.class})
public class LoggerAppenderTests {

	private static final String CLASS_IN_LOGGING_SYSTEM = "org.logging.LoggerClass";
	private static final String CLASS_NAME = "org.something.SomeClass";

	private LogLevel level = LogLevel.INFO;

	private ExceptionHandlingStrategy exceptionHandlingStrategyMock = mock(ExceptionHandlingStrategy.class);
	private PrintStream origPrintStreamMock = mock(PrintStream.class);
	private Logger loggerMock = mock(Logger.class);
	private LoggingSystemRegister loggingSystemRegisterMock = mock(LoggingSystemRegister.class);
	private LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyMock, origPrintStreamMock, loggingSystemRegisterMock);

	@Before
	public void setUp() {
		mockStatic(LoggerFactory.class);
		when(LoggerFactory.getLogger(anyString())).thenReturn(mock(Logger.class));
		when(LoggerFactory.getLogger(CLASS_NAME)).thenReturn(loggerMock);
		
		when(loggingSystemRegisterMock.isInLoggingSystem(CLASS_NAME)).thenReturn(false);
		when(loggingSystemRegisterMock.isInLoggingSystem(CLASS_IN_LOGGING_SYSTEM)).thenReturn(true);
		
		mockGettingCallOrigin(CLASS_NAME, false);
	}

	@Test
	public void appendNotifiesNotStackTrace() {
		loggerAppenderImplInstance.append("irrelevant");
		verify(exceptionHandlingStrategyMock).notifyNotStackTrace();
	}

	@Test
	public void appendLogsWhenMessageEndsWithUnixLineBreak() {
		loggerAppenderImplInstance.append("the message\n");
		verify(loggerMock).info("the message");
	}

	@Test
	public void delegatePrintCallsLoggerAppenderAppendAndLogWhenMessageEndsWithWindowsLineBreak() {
		loggerAppenderImplInstance.append("the message\r\n");
		verify(loggerMock).info("the message");
	}

	@Test
	public void appendAndLogPrintsToPrintStreamIfInLoggingSystem() {
		mockGettingCallOrigin(CLASS_IN_LOGGING_SYSTEM, false);
		
		loggerAppenderImplInstance.appendAndLog("some text");
		
		verify(origPrintStreamMock).println("some text");
	}

	@Test
	public void appendAndLogNonStackTraceNotifiesNotStackTrace() {
		loggerAppenderImplInstance.appendAndLog("some text");
		verify(exceptionHandlingStrategyMock).notifyNotStackTrace();
	}

	@Test
	public void appendAndLogLogs() {
		loggerAppenderImplInstance.appendAndLog("some text");
		
		verify(loggerMock).info("some text");
		verify(exceptionHandlingStrategyMock, never()).handleExceptionLine(anyString(), any(Logger.class));
	}

	@Test
	public void appendAndLogStackTraceCallsExceptionHandlingStrategy() {
		mockGettingCallOrigin(CLASS_NAME, true);
		
		loggerAppenderImplInstance.appendAndLog("exception line");
		
		verify(exceptionHandlingStrategyMock).handleExceptionLine("exception line", loggerMock);
		verify(exceptionHandlingStrategyMock, never()).notifyNotStackTrace();
		verifyZeroInteractions(loggerMock);
	}

	@Test
	public void appendAndLogFlushesAndResetsBuffer() {
		loggerAppenderImplInstance.append("1");
		loggerAppenderImplInstance.appendAndLog("2");
		
		verify(loggerMock).info("12");
		
		loggerAppenderImplInstance.append("3");
		loggerAppenderImplInstance.appendAndLog("4");
		
		verify(loggerMock).info("34");
	}

	private void mockGettingCallOrigin(String className, boolean printingStackTrace) {
		CallOrigin callOriginMock = mock(CallOrigin.class);
		when(callOriginMock.isPrintingStackTrace()).thenReturn(printingStackTrace);
		when(callOriginMock.getClassName()).thenReturn(className);

		mockStatic(CallOrigin.class);
		when(CallOrigin.getCallOrigin()).thenReturn(callOriginMock);
	}
}
