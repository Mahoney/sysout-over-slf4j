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
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.assertExpectedLoggingEvent;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.PrintStreamCoordinator;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.common.SystemOutput;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.PrintStreamCoordinatorImpl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReflectionUtils.class, SLF4JPrintStreamManager.class, PrintStreamCoordinatorFactory.class })
public class TestSLF4JPrintStreamManager extends SysOutOverSLF4JTestCase {

    private SLF4JPrintStreamManager slf4JPrintStreamManagerInstance;
    private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock;
    
    private Logger log = (Logger) LoggerFactory.getLogger(SysOutOverSLF4J.class);

    @Before
    public void setUp() {
        exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
        slf4JPrintStreamManagerInstance = new SLF4JPrintStreamManager();
        mockStatic(ReflectionUtils.class);
        mockStatic(PrintStreamCoordinatorFactory.class);
        log.setLevel(Level.TRACE);
    }

    @Test
    public void sendSystemOutAndErrToSLF4JMakesSystemOutputsSLF4JPrintStreamsWhenTheyAreNotAlready() throws Exception {

        expectSystemOutputsToBeReplacedWithSLF4JPrintStreams();
        expectLoggerAppendersToBeRegistered(LogLevel.DEBUG, LogLevel.WARN);
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(LogLevel.DEBUG, LogLevel.WARN, exceptionHandlingStrategyFactoryMock);
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams", Level.INFO, null, SysOutOverSLF4J.class.getName());
        assertExpectedLoggingEvent(appender.list.get(1), "Redirected System.out and System.err to SLF4J for this context", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void sendSystemOutAndErrToSLF4JDoesNotMakeSystemOutputsSLF4JPrintStreamsWhenTheyAreAlready() throws Exception {
    	new PrintStreamCoordinatorImpl().replaceSystemOutputsWithSLF4JPrintStreams();
        expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR);
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactoryMock);
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "System.out and System.err are already SLF4JPrintStreams", Level.DEBUG, null, SysOutOverSLF4J.class.getName());
        assertExpectedLoggingEvent(appender.list.get(1), "Redirected System.out and System.err to SLF4J for this context", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void stopSendingSystemOutAndErrToSLF4JDeregistersLoggerAppenders() {
    	new PrintStreamCoordinatorImpl().replaceSystemOutputsWithSLF4JPrintStreams();
    	
    	expectLoggerAppenderToBeDeregistered(SystemOutput.OUT);
    	expectLoggerAppenderToBeDeregistered(SystemOutput.ERR);
    	replayAll();
    	
    	slf4JPrintStreamManagerInstance.stopSendingSystemOutAndErrToSLF4J();
    	
    	verifyAll();
    }
    
    private void expectLoggerAppenderToBeDeregistered(SystemOutput systemOutput) {
    	SLF4JPrintStream slf4jPrintStreamMock = createMock(SLF4JPrintStream.class);
		expect(ReflectionUtils.wrap(systemOutput.get(), SLF4JPrintStream.class)).andReturn(slf4jPrintStreamMock);
		slf4jPrintStreamMock.deregisterLoggerAppender();
    }
    
    @Test
    public void stopSendingSystemOutAndErrToSLF4JLogsWarningIfSystemOutputsAreNotSLF4JPrintStreams() {
    	
    	slf4JPrintStreamManagerInstance.stopSendingSystemOutAndErrToSLF4J();
    	
    	assertExpectedLoggingEvent(appender.list.get(0),
    			"Cannot stop sending System.out and System.err to SLF4J - they are not being sent there at the moment",
    			Level.WARN, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void restoreOriginalSystemOutputsIfNecessaryRestoresOriginalPrintStreams() {
    	new PrintStreamCoordinatorImpl().replaceSystemOutputsWithSLF4JPrintStreams();
    	
    	PrintStreamCoordinator configurator = createMock(PrintStreamCoordinator.class);
		expect(PrintStreamCoordinatorFactory.createPrintStreamCoordinator()).andReturn(configurator);
        configurator.restoreOriginalSystemOutputs();
        replayAll();

        slf4JPrintStreamManagerInstance.restoreOriginalSystemOutputsIfNecessary();
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "Restored original System.out and System.err", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void restoreOriginalSystemOutputsIfNecessaryDoesNotRestoreOriginalPrintStreamsIfNotSLF4JPrintStreams() {
        replayAll();

        slf4JPrintStreamManagerInstance.restoreOriginalSystemOutputsIfNecessary();
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "System.out and System.err are not SLF4JPrintStreams - cannot restore", Level.WARN, null, SysOutOverSLF4J.class.getName());
    }
    
    private void expectLoggerAppendersToBeRegistered(LogLevel outLevel, LogLevel errLevel) throws Exception {
    	expectLoggerAppenderToBeRegistered(SystemOutput.OUT, outLevel);
    	expectLoggerAppenderToBeRegistered(SystemOutput.ERR, errLevel);
    }

	private void expectLoggerAppenderToBeRegistered(SystemOutput systemOutput, LogLevel logLevel) throws Exception {
		SLF4JPrintStream slf4jPrintStreamMock = createMock(SLF4JPrintStream.class);
		expect(ReflectionUtils.wrap(systemOutput.get(), SLF4JPrintStream.class)).andReturn(slf4jPrintStreamMock);
        PrintStream originalPrintStreamMock = createMock(PrintStream.class);
        expect(slf4jPrintStreamMock.getOriginalPrintStream()).andReturn(originalPrintStreamMock);
        ExceptionHandlingStrategy exceptionHandlingStrategyMock = createMock(ExceptionHandlingStrategy.class);
        expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(logLevel, originalPrintStreamMock)).andReturn(exceptionHandlingStrategyMock);
        LoggerAppenderImpl loggerAppenderMock = createMock(LoggerAppenderImpl.class);
        expectNew(LoggerAppenderImpl.class, logLevel, exceptionHandlingStrategyMock, originalPrintStreamMock).andReturn(loggerAppenderMock);
        slf4jPrintStreamMock.registerLoggerAppender(loggerAppenderMock);
	}

	private void expectSystemOutputsToBeReplacedWithSLF4JPrintStreams() throws Exception {
		PrintStreamCoordinator configurator = createMock(PrintStreamCoordinator.class);
		expect(PrintStreamCoordinatorFactory.createPrintStreamCoordinator()).andReturn(configurator);
        configurator.replaceSystemOutputsWithSLF4JPrintStreams();
	}
}
