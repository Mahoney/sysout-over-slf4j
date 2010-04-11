package org.slf4j.sysoutslf4j.context;

import static org.easymock.classextension.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;

import java.io.PrintStream;

import org.easymock.classextension.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, SysOutOverSLF4J.class})
@SuppressStaticInitializationFor("org.slf4j.sysoutslf4j.context.SysOutOverSLF4J")
public class TestLoggerAppenderImpl extends SysOutOverSLF4JTestCase {

	private static final String CLASS_IN_LOGGING_SYSTEM = "org.logging.LoggerClass";
	private static final String CLASS_NAME = "org.something.SomeClass";
	
	private LogLevel level = LogLevel.INFO;
	
	private IMocksControl mocksControl = createStrictControl();
	private ExceptionHandlingStrategy exceptionHandlingStrategyMock = mocksControl.createMock(ExceptionHandlingStrategy.class);
	private PrintStream printStreamMock = mocksControl.createMock(PrintStream.class);
	private Logger loggerMock = mocksControl.createMock(Logger.class);
	
	private LoggerAppenderImpl loggerAppenderImplInstance = new LoggerAppenderImpl(level, exceptionHandlingStrategyMock, printStreamMock);
	
	@Before
	public void setUp() {
		mockStatic(LoggerFactory.class);
		expect(LoggerFactory.getLogger(CLASS_NAME)).andStubReturn(loggerMock);
		
		mockStatic(SysOutOverSLF4J.class);
		expect(SysOutOverSLF4J.isInLoggingSystem(CLASS_IN_LOGGING_SYSTEM)).andStubReturn(true);
		expect(SysOutOverSLF4J.isInLoggingSystem(CLASS_NAME)).andStubReturn(false);
	}
	
	@Test
	public void appendNotifiesNotStackTrace() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		replayAll();
		
		loggerAppenderImplInstance.append("irrelevant");
		mocksControl.verify();
		verifyAll();
	}
	
	@Test
	public void appendAndLogPrintsToPrintStreamIfInLoggingSystem() {
		printStreamMock.println("some text");
		replayAll();
		
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_IN_LOGGING_SYSTEM, false);
		verifyAll();
	}
	
	@Test
	public void appendAndLogNonStackTraceLogsAndNotifiesNotStackTrace() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		loggerMock.info("some text");
		replayAll();
		
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_NAME, false);
		verifyAll();
	}
	
	@Test
	public void appendAndLogStackTraceCallsExceptionHandlingStrategy() {
		exceptionHandlingStrategyMock.handleExceptionLine("some text", loggerMock);
		replayAll();
		
		loggerAppenderImplInstance.appendAndLog("some text", CLASS_NAME, true);
		verifyAll();
	}
	
	@Test
	public void appendAndLogFlushesAndResetsBuffer() {
		exceptionHandlingStrategyMock.notifyNotStackTrace();
		expectLastCall().asStub();
		
		loggerMock.info("12");
		loggerMock.info("34");
		replayAll();
		
		loggerAppenderImplInstance.append("1");
		loggerAppenderImplInstance.appendAndLog("2", CLASS_NAME, false);
		loggerAppenderImplInstance.append("3");
		loggerAppenderImplInstance.appendAndLog("4", CLASS_NAME, false);
		verifyAll();
	}
	
	private void replayAll() {
		mocksControl.replay();
		PowerMock.replayAll();
	}
	
	private void verifyAll() {
		mocksControl.verify();
		PowerMock.verifyAll();
	}
}
