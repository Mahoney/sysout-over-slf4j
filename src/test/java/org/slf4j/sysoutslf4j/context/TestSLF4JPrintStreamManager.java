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
@PrepareForTest({ ReflectionUtils.class, SLF4JPrintStreamManager.class, SLF4JPrintStreamProxy.class, SLF4JPrintStreamConfiguratorClass.class })
public class TestSLF4JPrintStreamManager extends SysOutOverSLF4JTestCase {

    private SLF4JPrintStreamManager slf4JPrintStreamManagerInstance;
    private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock;
    
    private Logger log = (Logger) LoggerFactory.getLogger(SysOutOverSLF4J.class);

    @Before
    public void setUp() {
        exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
        slf4JPrintStreamManagerInstance = new SLF4JPrintStreamManager();
        mockStatic(ReflectionUtils.class);
        mockStatic(SLF4JPrintStreamProxy.class);
        mockStatic(SLF4JPrintStreamConfiguratorClass.class);
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
    	(new PrintStreamCoordinatorImpl()).replaceSystemOutputsWithSLF4JPrintStreams();
        expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR);
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR, exceptionHandlingStrategyFactoryMock);
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "System.out and System.err are already SLF4JPrintStreams", Level.DEBUG, null, SysOutOverSLF4J.class.getName());
        assertExpectedLoggingEvent(appender.list.get(1), "Redirected System.out and System.err to SLF4J for this context", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void stopSendingSystemOutAndErrToSLF4JDeregistersLoggerAppenders() {
    	
    	expectLoggerAppenderToBeDeregistered(SystemOutput.OUT);
    	expectLoggerAppenderToBeDeregistered(SystemOutput.ERR);
    	
    	replayAll();
    	
    	slf4JPrintStreamManagerInstance.stopSendingSystemOutAndErrToSLF4J();
    	
    	verifyAll();
    }
    
    private void expectLoggerAppenderToBeDeregistered(SystemOutput systemOutput) {
    	SLF4JPrintStream slf4jPrintStreamMock = createMock(SLF4JPrintStream.class);
		expect(SLF4JPrintStreamProxy.wrap(systemOutput.get())).andReturn(slf4jPrintStreamMock);
		slf4jPrintStreamMock.deregisterLoggerAppender();
    }
    
    @Test
    public void stopSendingSystemOutAndErrToSLF4JLogsWarningIfSystemOutputsAreNotSLF4JPrintStreams() {
    	expect(SLF4JPrintStreamProxy.wrap(System.out)).andStubThrow(new IllegalArgumentException());
    	expect(SLF4JPrintStreamProxy.wrap(System.err)).andStubThrow(new IllegalArgumentException());
    	
    	replayAll();
    	
    	slf4JPrintStreamManagerInstance.stopSendingSystemOutAndErrToSLF4J();
    	
    	assertExpectedLoggingEvent(appender.list.get(0),
    			"Cannot stop sending System.out and System.err to SLF4J - they are not being sent there at the moment",
    			Level.WARN, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void restoreOriginalSystemOutputsIfNecessaryRestoresOriginalPrintStreams() {
    	new PrintStreamCoordinatorImpl().replaceSystemOutputsWithSLF4JPrintStreams();
    	
    	PrintStreamCoordinator configurator = new PrintStreamCoordinatorImpl();
		expect(SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass()).andReturn(configurator);
        expect(ReflectionUtils.invokeMethod(
        		"restoreOriginalSystemOutputs", configurator)).andReturn(null);
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
		expect(SLF4JPrintStreamProxy.wrap(systemOutput.get())).andReturn(slf4jPrintStreamMock);
        PrintStream originalPrintStreamMock = createMock(PrintStream.class);
        expect(slf4jPrintStreamMock.getOriginalPrintStream()).andReturn(originalPrintStreamMock);
        ExceptionHandlingStrategy exceptionHandlingStrategyMock = createMock(ExceptionHandlingStrategy.class);
        expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(logLevel, originalPrintStreamMock)).andReturn(exceptionHandlingStrategyMock);
        LoggerAppenderImpl loggerAppenderMock = createMock(LoggerAppenderImpl.class);
        expectNew(LoggerAppenderImpl.class, logLevel, exceptionHandlingStrategyMock, originalPrintStreamMock).andReturn(loggerAppenderMock);
        slf4jPrintStreamMock.registerLoggerAppender(loggerAppenderMock);
	}

	private void expectSystemOutputsToBeReplacedWithSLF4JPrintStreams() throws Exception {
		PrintStreamCoordinator configurator = new PrintStreamCoordinatorImpl();
		expect(SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass()).andReturn(configurator);
        expect(ReflectionUtils.invokeMethod(
        		"replaceSystemOutputsWithSLF4JPrintStreams", configurator)).andReturn(null);
	}

//	private void expectConfiguratorClassToBeLoadedFromNewClassLoader() {
//        ClassLoader classLoaderMock = createMock(ClassLoader.class);
//        expect(ClassLoaderUtils.makeNewClassLoaderForJar(SLF4JPrintStreamConfigurator.class)).andReturn(classLoaderMock);
//        ClassLoaderUtils.loadClass(classLoaderMock, SLF4JPrintStreamConfigurator.class);
//        expectLastCall().andReturn(SLF4JPrintStreamConfigurator.class);
//	}
}
