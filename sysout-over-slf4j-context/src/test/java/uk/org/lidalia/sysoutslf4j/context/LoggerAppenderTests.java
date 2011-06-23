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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.LoggerAppender;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.context.CallOrigin;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, CallOrigin.class})
public class LoggerAppenderTests extends SysOutOverSLF4JTestCase {

	private static final String CLASS_IN_LOGGING_SYSTEM = "org.logging.LoggerClass";
	private static final String CLASS_NAME = "org.something.SomeClass";

	private LogLevel level = LogLevel.INFO;

	private ExceptionHandlingStrategy exceptionHandlingStrategyMock = createMock(ExceptionHandlingStrategy.class);
	private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
	private PrintStream origPrintStreamMock = createMock(PrintStream.class);
	private Logger loggerMock = createMock(Logger.class);
	private LoggingSystemRegister loggingSystemRegisterMock = createMock(LoggingSystemRegister.class);

	@Before
	public void setUp() {
		expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(level, origPrintStreamMock)).andStubReturn(exceptionHandlingStrategyMock);
		
		mockStatic(LoggerFactory.class);
		expect(LoggerFactory.getLogger(CLASS_NAME)).andStubReturn(loggerMock);
		
		loggingSystemRegisterMock.isInLoggingSystem(CLASS_NAME);
		expectLastCall().andStubReturn(false);
		
	}

	@Test
	public void appendNotifiesNotStackTrace() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("irrelevant");
	}

	@Test
	public void appendLogsWhenMessageEndsWithUnixLineBreak() {
		mockGettingCallOrigin(CLASS_NAME, false);
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		expectLastCall().asStub();
		
		loggerMock.info("the message");
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("the message\n");
	}

	@Test
	public void delegatePrintCallsLoggerAppenderAppendAndLogWhenMessageEndsWithWindowsLineBreak() {
		mockGettingCallOrigin(CLASS_NAME, false);
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		expectLastCall().asStub();
		
		loggerMock.info("the message");
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("the message\r\n");
	}

	@Test
	public void appendAndLogPrintsToPrintStreamIfInLoggingSystem() {
		mockGettingCallOrigin(CLASS_IN_LOGGING_SYSTEM, false);
		loggingSystemRegisterMock.isInLoggingSystem(CLASS_IN_LOGGING_SYSTEM);
		expectLastCall().andStubReturn(true);

		origPrintStreamMock.println("some text");
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text");
	}

	@Test
	public void appendAndLogNonStackTraceLogsAndNotifiesNotStackTrace() {
		mockGettingCallOrigin(CLASS_NAME, false);

		exceptionHandlingStrategyMock.notifyNotStackTrace();
		loggerMock.info("some text");
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text");
	}

	@Test
	public void appendAndLogStackTraceCallsExceptionHandlingStrategy() {
		mockGettingCallOrigin(CLASS_NAME, true);
		exceptionHandlingStrategyMock.handleExceptionLine("some text", loggerMock);
		replayAll();
	
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text");
	}

	@Test
	public void appendAndLogFlushesAndResetsBuffer() {
		mockGettingCallOrigin(CLASS_NAME, false);
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		expectLastCall().asStub();
		
		loggerMock.info("12");
		loggerMock.info("34");
		replayAll();
		
		LoggerAppender loggerAppenderImplInstance = new LoggerAppender(level, exceptionHandlingStrategyFactoryMock, origPrintStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("1");
		loggerAppenderImplInstance.appendAndLog("2");
		loggerAppenderImplInstance.append("3");
		loggerAppenderImplInstance.appendAndLog("4");
	}

	private void mockGettingCallOrigin(String className, boolean printingStackTrace) {
		CallOrigin callOriginMock = createMock(CallOrigin.class);
		expect(callOriginMock.isPrintingStackTrace()).andStubReturn(printingStackTrace);
		expect(callOriginMock.getClassName()).andStubReturn(className);

		mockStatic(CallOrigin.class);
		expect(CallOrigin.getCallOrigin(eq("uk.org.lidalia.sysoutslf4j"))).andStubReturn(callOriginMock);
	}
}
