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

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;

import java.io.PrintStream;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.LoggerAppenderImpl;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class})
public class TestLoggerAppenderImpl extends SysOutOverSLF4JTestCase {

	private static final String CLASS_IN_LOGGING_SYSTEM = "org.logging.LoggerClass";
	private static final String CLASS_NAME = "org.something.SomeClass";
	
	private LogLevel level = LogLevel.INFO;
	
	private IMocksControl mocksControl = createStrictControl();
	private ExceptionHandlingStrategy exceptionHandlingStrategyMock = mocksControl.createMock(ExceptionHandlingStrategy.class);
	private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock = mocksControl.createMock(ExceptionHandlingStrategyFactory.class);
	private PrintStream printStreamMock = mocksControl.createMock(PrintStream.class);
	private Logger loggerMock = mocksControl.createMock(Logger.class);
	private LoggingSystemRegister loggingSystemRegisterMock = mocksControl.createMock(LoggingSystemRegister.class);

	@Before
	public void setUp() {
		expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(level, printStreamMock)).andStubReturn(exceptionHandlingStrategyMock);
		
		mockStatic(LoggerFactory.class);
		expect(LoggerFactory.getLogger(CLASS_NAME)).andStubReturn(loggerMock);
		
		loggingSystemRegisterMock.isInLoggingSystem(CLASS_NAME);
		expectLastCall().andStubReturn(false);
	}

	@Test
	public void appendNotifiesNotStackTrace() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		replayAll();
		
		LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyFactoryMock, printStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("irrelevant");
	}

	@Test
	public void appendAndLogPrintsToPrintStreamIfInLoggingSystem() {
		loggingSystemRegisterMock.isInLoggingSystem(CLASS_IN_LOGGING_SYSTEM);
		expectLastCall().andStubReturn(true);

		printStreamMock.println("some text");
		replayAll();
		
		LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyFactoryMock, printStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_IN_LOGGING_SYSTEM, false);
	}

	@Test
	public void appendAndLogNonStackTraceLogsAndNotifiesNotStackTrace() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		loggerMock.info("some text");
		replayAll();
		
		LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyFactoryMock, printStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_NAME, false);
	}

	@Test
	public void appendAndLogStackTraceCallsExceptionHandlingStrategy() {
		exceptionHandlingStrategyMock.handleExceptionLine("some text", loggerMock);
		replayAll();
	
		LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyFactoryMock, printStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_NAME, true);
	}

	@Test
	public void appendAndLogFlushesAndResetsBuffer() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		expectLastCall().asStub();
		
		loggerMock.info("12");
		loggerMock.info("34");
		replayAll();
		
		LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyFactoryMock, printStreamMock, loggingSystemRegisterMock);
		loggerAppenderImplInstance.append("1");
		loggerAppenderImplInstance.appendAndLog("2", CLASS_NAME, false);
		loggerAppenderImplInstance.append("3");
		loggerAppenderImplInstance.appendAndLog("4", CLASS_NAME, false);
	}
	
	private void replayAll() {
		mocksControl.replay();
		PowerMock.replayAll();
	}
	
	@After
	public void verifyAll() {
		mocksControl.verify();
	}
}
