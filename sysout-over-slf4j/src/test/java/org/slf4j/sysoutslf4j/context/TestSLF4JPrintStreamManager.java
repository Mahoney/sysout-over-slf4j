package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class, LoggerFactory.class})
public class TestSLF4JPrintStreamManager {

    private Logger loggerMock;
    private SLF4JPrintStreamManager slf4JPrintStreamManagerInstance;
    private ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactoryMock;

    @Before
    public void setUp() {
        loggerMock = createMock(Logger.class);
        exceptionHandlingStrategyFactoryMock = createMock(ExceptionHandlingStrategyFactory.class);
        mockStatic(LoggerFactory.class);
		expect(LoggerFactory.getLogger(SysOutOverSLF4J.class)).andStubReturn(loggerMock);

        slf4JPrintStreamManagerInstance = new SLF4JPrintStreamManager();
    }
    @Test
    public void sendSystemOutAndErrToSLF4JMakesSystemOutputsSLF4JPrintStreamsIfTheyAreNotAlready() {

        mockStatic(ReflectionUtils.class);
        expect(ReflectionUtils.invokeStaticMethod("replaceSystemOutputsWithSLF4JPrintStreamsIfNecessary", SLF4JPrintStreamConfigurator.class)).andReturn(null);
        replayAll();

        SLF4JPrintStreamConfigurator.replaceSystemOutputsWithSLF4JPrintStreams();
        slf4JPrintStreamManagerInstance.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactoryMock);
        verifyAll();
    }
}
