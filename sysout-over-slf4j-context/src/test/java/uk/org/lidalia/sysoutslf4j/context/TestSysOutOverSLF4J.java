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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static uk.org.lidalia.test.Assert.assertNotInstantiable;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.LoggingSystemRegister;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SysOutOverSLF4J.class, PerContextSystemOutput.class, PerContextPrintStream.class })
public class TestSysOutOverSLF4J extends SysOutOverSLF4JTestCase {

	private LoggingSystemRegister loggingSystemRegisterMock = createMock(LoggingSystemRegister.class);
	private PerContextSystemOutput outMock;
    private PerContextSystemOutput errMock;

	@Before
	public void setStaticMocks() {
		Whitebox.setInternalState(SysOutOverSLF4J.class, loggingSystemRegisterMock);
		
		outMock = createMock(PerContextSystemOutput.class);
        errMock = createMock(PerContextSystemOutput.class);
    	Whitebox.setInternalState(PerContextSystemOutput.class, "OUT", outMock);
    	Whitebox.setInternalState(PerContextSystemOutput.class, "ERR", errMock);
    	mockStatic(PerContextSystemOutput.class);
    	expect(PerContextSystemOutput.values()).andStubReturn(new PerContextSystemOutput[]{outMock, errMock});

	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndLogPerLineExceptionHandlingStrategy() throws Exception {
		expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, LogPerLineExceptionHandlingStrategyFactory.getInstance());
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndGivenExceptionHandlingStrategy() throws Exception {
		ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory = createMock(ExceptionHandlingStrategyFactory.class);
		expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactory);
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndLogPerLineExceptionHandlingStrategy() throws Exception {
		expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, LogPerLineExceptionHandlingStrategyFactory.getInstance());
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndGivenExceptionHandlingStrategy() throws Exception {
		ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
		expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactoryMock);
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactoryMock);
		verifyAll();
	}

	private void expectLoggerAppendersToBeRegistered(LogLevel outLevel, LogLevel errLevel, ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws Exception {
    	expectLoggerAppenderToBeRegistered(outMock, outLevel, exceptionHandlingStrategyFactory);
    	expectLoggerAppenderToBeRegistered(errMock, errLevel, exceptionHandlingStrategyFactory);
    }

	private void expectLoggerAppenderToBeRegistered(PerContextSystemOutput systemOutputMock, LogLevel logLevel, ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws Exception {
		PrintStream originalPrintStreamMock = createMock(PrintStream.class);
        expect(systemOutputMock.getOriginalPrintStream()).andStubReturn(originalPrintStreamMock);
        LoggerAppender loggerAppenderMock = createMock(LoggerAppender.class);
        expectNew(LoggerAppender.class, logLevel, exceptionHandlingStrategyFactory, originalPrintStreamMock, loggingSystemRegisterMock).andReturn(loggerAppenderMock);
        PerContextPrintStream perContextPrintStream = createMock(PerContextPrintStream.class);
        expectNew(PerContextPrintStream.class, originalPrintStreamMock, loggerAppenderMock).andReturn(perContextPrintStream);
        systemOutputMock.registerPrintStreamForThisContext(perContextPrintStream);
	}

	@Test
	public void stopSendingSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManager() {
		outMock.deregisterPrintStreamForThisContext();
    	errMock.deregisterPrintStreamForThisContext();
    	replayAll();
		replayAll();
		SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void restoreOriginalSystemOutputsDelegatesToSLF4JPrintStreamManager() {
		outMock.restoreOriginalPrintStream();
    	errMock.restoreOriginalPrintStream();
		replayAll();
		SysOutOverSLF4J.restoreOriginalSystemOutputs();
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
	
	@Test
	public void isSLF4JPrintStreamReturnsFalseWhenSystemOutIsSLF4JPrintStream() {
		expect(outMock.isPerContextPrintStream()).andReturn(false);
		replayAll();
		assertFalse(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
	}

	@Test
	public void isSLF4JPrintStreamReturnsTrueWhenSystemOutIsSLF4JPrintStream() {
		expect(outMock.isPerContextPrintStream()).andReturn(true);
		replayAll();
		assertTrue(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
	}

	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(SysOutOverSLF4J.class);
	}
}
