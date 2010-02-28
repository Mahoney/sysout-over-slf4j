package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
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
import org.powermock.reflect.Whitebox;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.ClassLoaderUtils;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class, ClassLoaderUtils.class, SLF4JPrintStreamManager.class, SLF4JSystemOutput.class})
public class TestSLF4JPrintStreamManager extends SysOutOverSLF4JTestCase {

    private SLF4JPrintStreamManager slf4JPrintStreamManagerInstance;
    private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock;
    private SLF4JSystemOutput slf4jSystemOutMock;
    private SLF4JSystemOutput slf4jSystemErrMock;
    
    private Logger log = (Logger) LoggerFactory.getLogger(SysOutOverSLF4J.class);

    @Before
    public void setUp() {
        exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
        slf4JPrintStreamManagerInstance = new SLF4JPrintStreamManager();
        mockStatic(ReflectionUtils.class);
        mockStatic(ClassLoaderUtils.class);
        log.setLevel(Level.TRACE);
        slf4jSystemOutMock = createMock(SLF4JSystemOutput.class);
        slf4jSystemErrMock = createMock(SLF4JSystemOutput.class);
        Whitebox.setInternalState(SLF4JSystemOutput.class, "OUT", slf4jSystemOutMock);
        Whitebox.setInternalState(SLF4JSystemOutput.class, "ERR", slf4jSystemErrMock);
    }

    @Test
    public void sendSystemOutAndErrToSLF4JMakesSystemOutputsSLF4JPrintStreamsWhenTheyAreNotAlready() throws Exception {

        expectSystemOutputsToBeReplacedWithSLF4JPrintStreams();
        expectLoggerAppendersToBeRegistered();
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactoryMock);
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams", Level.INFO, null, SysOutOverSLF4J.class.getName());
        assertExpectedLoggingEvent(appender.list.get(1), "Redirected System.out and System.err to SLF4J for this context", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    @Test
    public void sendSystemOutAndErrToSLF4JDoesNotMakeSystemOutputsSLF4JPrintStreamsWhenTheyAreAlready() throws Exception {
    	SLF4JPrintStreamConfigurator.replaceSystemOutputsWithSLF4JPrintStreams();
        expectLoggerAppendersToBeRegistered();
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactoryMock);
        verifyAll();
        
        assertExpectedLoggingEvent(appender.list.get(0), "System.out and System.err are already SLF4JPrintStreams", Level.DEBUG, null, SysOutOverSLF4J.class.getName());
        assertExpectedLoggingEvent(appender.list.get(1), "Redirected System.out and System.err to SLF4J for this context", Level.INFO, null, SysOutOverSLF4J.class.getName());
    }
    
    private void expectLoggerAppendersToBeRegistered() throws Exception {
    	expectLoggerAppenderToBeRegistered(SLF4JSystemOutput.OUT, LogLevel.INFO);
    	expectLoggerAppenderToBeRegistered(SLF4JSystemOutput.ERR, LogLevel.ERROR);
    }

	private void expectLoggerAppenderToBeRegistered(SLF4JSystemOutput systemOutput, LogLevel logLevel) throws Exception {
		SLF4JPrintStream slf4jPrintStreamMock = createMock(SLF4JPrintStream.class);
		expect(systemOutput.get()).andReturn(slf4jPrintStreamMock);
        PrintStream originalPrintStreamMock = createMock(PrintStream.class);
        expect(slf4jPrintStreamMock.getOriginalPrintStream()).andReturn(originalPrintStreamMock);
        ExceptionHandlingStrategy exceptionHandlingStrategyMock = createMock(ExceptionHandlingStrategy.class);
        expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(logLevel, originalPrintStreamMock)).andReturn(exceptionHandlingStrategyMock);
        LoggerAppenderImpl loggerAppenderMock = createMock(LoggerAppenderImpl.class);
        expectNew(LoggerAppenderImpl.class, logLevel, exceptionHandlingStrategyMock, originalPrintStreamMock).andReturn(loggerAppenderMock);
        slf4jPrintStreamMock.registerLoggerAppender(loggerAppenderMock);
	}

	private void expectSystemOutputsToBeReplacedWithSLF4JPrintStreams() {
		expectConfiguratorClassToBeLoaded();
        expect(ReflectionUtils.invokeStaticMethod(
        		"replaceSystemOutputsWithSLF4JPrintStreams", SLF4JPrintStreamConfigurator.class)).andReturn(null);
	}

	private void expectConfiguratorClassToBeLoaded() {
        ClassLoader classLoaderMock = createMock(ClassLoader.class);
        expect(ClassLoaderUtils.makeNewClassLoaderForJar(SLF4JPrintStreamConfigurator.class)).andReturn(classLoaderMock);
        ClassLoaderUtils.loadClass(classLoaderMock, SLF4JPrintStreamConfigurator.class);
        expectLastCall().andReturn(SLF4JPrintStreamConfigurator.class);
	}
}
