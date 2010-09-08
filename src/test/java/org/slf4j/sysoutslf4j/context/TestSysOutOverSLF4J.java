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

package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.assertNotInstantiable;

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
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndLogPerLineExceptionHandlingStrategy() {
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, LogPerLineExceptionHandlingStrategyFactory.getInstance());
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndGivenExceptionHandlingStrategy() {
		ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory = createMock(ExceptionHandlingStrategyFactory.class);
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactory);
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndLogPerLineExceptionHandlingStrategy() {
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, LogPerLineExceptionHandlingStrategyFactory.getInstance());
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndGivenExceptionHandlingStrategy() {
		ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory = createMock(ExceptionHandlingStrategyFactory.class);
		slf4jPrintStreamManager.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactory);
		replayAll();
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
		verifyAll();
	}
	
	@Test
	public void stopSendingSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManager() {
		slf4jPrintStreamManager.stopSendingSystemOutAndErrToSLF4J();
		replayAll();
		SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
		verifyAll();
	}
	
	@Test
	public void restoreOriginalSystemOutputsDelegatesToSLF4JPrintStreamManager() {
		slf4jPrintStreamManager.restoreOriginalSystemOutputsIfNecessary();
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
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(SysOutOverSLF4J.class);
	}
}
