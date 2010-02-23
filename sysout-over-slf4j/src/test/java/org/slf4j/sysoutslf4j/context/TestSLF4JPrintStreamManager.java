package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.ClassLoaderUtils;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SystemOutput;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class, ClassLoaderUtils.class, SLF4JPrintStreamManager.class})
public class TestSLF4JPrintStreamManager extends SysOutOverSLF4JTestCase {

    private SLF4JPrintStreamManager slf4JPrintStreamManagerInstance;
    private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock;

    @Before
    public void setUp() {
        exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
        slf4JPrintStreamManagerInstance = new SLF4JPrintStreamManager();
    }

    @Test
    public void sendSystemOutAndErrToSLF4JMakesSystemOutputsSLF4JPrintStreamsWhenTheyAreNotAlready() throws Exception {

        expectConfiguratorClassToBeLoaded();
        
        mockStatic(ReflectionUtils.class);
        expect(ReflectionUtils.invokeStaticMethod(
        		"replaceSystemOutputsWithSLF4JPrintStreams", SLF4JPrintStreamConfigurator.class)).andReturn(null);
        
        for (SystemOutput systemOutput : SystemOutput.values()) {
	        PrintStream originalPrintStreamMock = createMock(PrintStream.class);
	        expect(ReflectionUtils.invokeMethod(
	        		"getOriginalPrintStream", systemOutput.get())).andReturn(originalPrintStreamMock);
	        ExceptionHandlingStrategy exceptionHandlingStrategyMock = createMock(ExceptionHandlingStrategy.class);
	        expect(exceptionHandlingStrategyFactoryMock.makeExceptionHandlingStrategy(
	        		systemOutput.getLogLevel(), originalPrintStreamMock)).andReturn(exceptionHandlingStrategyMock);
	        LoggerAppenderImpl loggerAppenderMock = createMock(LoggerAppenderImpl.class);
	        expectNew(LoggerAppenderImpl.class, systemOutput.getLogLevel(), exceptionHandlingStrategyMock, originalPrintStreamMock).andReturn(loggerAppenderMock);
	        expect(ReflectionUtils.invokeMethod("registerLoggerAppender", systemOutput.get(), Object.class, loggerAppenderMock)).andReturn(null);
        }
        
        replayAll();

        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactoryMock);
        verifyAll();
    }

	private void expectConfiguratorClassToBeLoaded() {
		mockStatic(ClassLoaderUtils.class);
        ClassLoader classLoaderMock = createMock(ClassLoader.class);
        expect(ClassLoaderUtils.makeNewClassLoaderForJar(SLF4JPrintStreamConfigurator.class)).andReturn(classLoaderMock);
        ClassLoaderUtils.loadClass(classLoaderMock, SLF4JPrintStreamConfigurator.class);
        expectLastCall().andReturn(SLF4JPrintStreamConfigurator.class);
	}
}
