package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SysOutOverSLF4J.class })
@SuppressStaticInitializationFor("org.slf4j.sysoutslf4j.context.SysOutOverSLF4J")
public class TestSysOutOverSLF4J extends SysOutOverSLF4JTestCase {

	private LoggingSystemRegister loggingSystemRegisterMock = createMock(LoggingSystemRegister.class);
	private SLF4JPrintStreamManager slf4jPrintStreamManager = createMock(SLF4JPrintStreamManager.class);
	
	@Before
	public void setStaticMocks() {
		Whitebox.setInternalState(SysOutOverSLF4J.class, loggingSystemRegisterMock);
		Whitebox.setInternalState(SysOutOverSLF4J.class, slf4jPrintStreamManager);
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithLogPerLineExceptionHandlingStrategy() {
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(LogPerLineExceptionHandlingStrategyFactory.getInstance());
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithGivenExceptionHandlingStrategy() {
		ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory = createMock(ExceptionHandlingStrategyFactory.class);
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToOriginalsDelegatesToSLF4JPrintStreamManager() {
		slf4jPrintStreamManager.sendSystemOutAndErrToOriginals();
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToOriginals();
		verifyAll();
	}
	
	@Test
	public void registerLoggingSystemDelegatesToLoggingSystemRegister() {
		loggingSystemRegisterMock.registerLoggingSystem("somePackageName");
		replayAll();
		SysOutOverSLF4J.registerLoggingSystem("somePackageName");
		verifyAll();
	}
	
	@Test
	public void unregisterLoggingSystemDelegatesToLoggingSystemRegister() {
		loggingSystemRegisterMock.unregisterLoggingSystem("somePackageName");
		replayAll();
		SysOutOverSLF4J.unregisterLoggingSystem("somePackageName");
		verifyAll();
	}
	
	@Test
	public void isInLoggingSystemDelegatesToLoggingSystemRegister() {
		expect(loggingSystemRegisterMock.isInLoggingSystem("somePackageName")).andReturn(true);
		replayAll();
		assertTrue(SysOutOverSLF4J.isInLoggingSystem("somePackageName"));
		verifyAll();
	}
	
//	@After
//	public void tearDown() {
//		SysOutOverSLF4J.unregisterLoggingSystem("some.package");
//	}

	
//	private SLF4JPrintStream expectedOutPrintStreamMock;
//	private SLF4JPrintStream expectedErrPrintStreamMock;
	
//	private void setUpNewSLF4JPrintStreamExpectations() throws Exception {
//		LoggerAppenderImpl outLoggerAppenderMock = createMock(LoggerAppenderImpl.class);
//		LoggerAppenderStore outLoggerAppenderStore = createMock(LoggerAppenderStore.class);
//		SLF4JPrintStreamDelegater outSlf4jPrintStreamDelegater = createMock(SLF4JPrintStreamDelegater.class);
//		expectedOutPrintStreamMock = createMock(SLF4JPrintStream.class);
//		
//		LoggerAppenderImpl errLoggerAppenderMock = createMock(LoggerAppenderImpl.class);
//		LoggerAppenderStore errLoggerAppenderStore = createMock(LoggerAppenderStore.class);
//		SLF4JPrintStreamDelegater errSlf4jPrintStreamDelegater = createMock(SLF4JPrintStreamDelegater.class);
//		expectedErrPrintStreamMock = createMock(SLF4JPrintStream.class);
//		
//		expectNew(LoggerAppenderImpl.class, LogLevel.INFO, LogPerLineExceptionHandlingStrategy.getInstance())
//				.andReturn(outLoggerAppenderMock);
//		expectNew(LoggerAppenderStore.class).andReturn(outLoggerAppenderStore);
//		expectNew(SLF4JPrintStreamDelegater.class, System.out, outLoggerAppenderStore).andReturn(outSlf4jPrintStreamDelegater);
//		expectNew(SLF4JPrintStream.class, System.out, outSlf4jPrintStreamDelegater).andReturn(expectedOutPrintStreamMock);
//		
//		expectNew(LoggerAppenderImpl.class, LogLevel.ERROR, LogPerLineExceptionHandlingStrategy.getInstance())
//				.andReturn(errLoggerAppenderMock);
//		expectNew(LoggerAppenderStore.class).andReturn(errLoggerAppenderStore);
//		expectNew(SLF4JPrintStreamDelegater.class, System.err, errLoggerAppenderStore).andReturn(errSlf4jPrintStreamDelegater);
//		expectNew(SLF4JPrintStream.class, System.err, outSlf4jPrintStreamDelegater).andReturn(expectedErrPrintStreamMock);
//		
//		replay(LoggerAppenderImpl.class);
//		replay(LoggerAppenderStore.class);
//		replay(SLF4JPrintStreamDelegater.class);
//		replay(SLF4JPrintStream.class);
//	}
	
//	private void verifyNewSLF4JPrintStreamExpectations() {
//		verify(LoggerAppenderImpl.class);
//		verify(SLF4JPrintStream.class);
//		
//		assertSame(expectedOutPrintStreamMock, System.out);
//		assertSame(expectedErrPrintStreamMock, System.err);
//	}
	
//	@Test
//	public void testSendSystemOutAndErrToSLF4JReplacesOutPrintStreamWithSLF4JPrintStream() throws Exception {
//		setUpNewSLF4JPrintStreamExpectations();
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		verifyNewSLF4JPrintStreamExpectations();
//	}
//	
//	@Test
//	public void testSendSystemOutAndErrToSLF4JCanBeCalledMultipleTimes() throws Exception {
//		setUpNewSLF4JPrintStreamExpectations();
//		
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		
//		verifyNewSLF4JPrintStreamExpectations();
//	}
	
//	@Test
//	public void testSendSystemOutAndErrToOriginalsRestoresOriginalPrintStreams() {
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		SysOutOverSLF4J.sendSystemOutAndErrToOriginals();
//		assertSame(SYS_OUT, System.out);
//		assertSame(SYS_ERR, System.err);
//	}
//	
//	@Test
//	public void testSendSystemOutAndErrToOriginalsCanBeCalledMultipleTimes() {
//		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
//		SysOutOverSLF4J.sendSystemOutAndErrToOriginals();
//		SysOutOverSLF4J.sendSystemOutAndErrToOriginals();
//		SysOutOverSLF4J.sendSystemOutAndErrToOriginals();
//		assertSame(SYS_OUT, System.out);
//		assertSame(SYS_ERR, System.err);
//	}
	
	@Test
	public void testConstructorThrowsUnsupportedOperationException() throws Exception {
		try {
			Whitebox.invokeConstructor(SysOutOverSLF4J.class);
			fail();
		} catch (UnsupportedOperationException uoe) {
			assertEquals("Not instantiable", uoe.getMessage());
		}
	}
}
