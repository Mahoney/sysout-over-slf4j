/*
 * Copyright (c) 2009-2012 Robert Elliot
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

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static uk.org.lidalia.test.Assert.assertNotInstantiable;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J")
@PrepareForTest({ LogPerLineExceptionHandlingStrategyFactory.class, LoggingSystemRegister.class, SysOutOverSLF4J.class, PerContextSystemOutput.class })
public class SysOutOverSLF4JTests extends SysOutOverSLF4JTestCase {

    private final LoggingSystemRegister loggingSystemRegisterMock = mock(LoggingSystemRegister.class);
    private final Logger loggerMock = mock(Logger.class);
    private final PerContextSystemOutput outMock = mock(PerContextSystemOutput.class);
    private final PerContextSystemOutput errMock = mock(PerContextSystemOutput.class);
    private final ExceptionHandlingStrategyFactory customExceptionHandlingStrategyFactoryMock = mock(ExceptionHandlingStrategyFactory.class);
    private final ExceptionHandlingStrategyFactory defaultExceptionHandlingStrategyFactoryMock = mock(ExceptionHandlingStrategyFactory.class);

    private PrintStream outContextPrintStream;
    private PrintStream errContextPrintStream;

    @Before
    public void mockLoggingSystemRegister() {
        Whitebox.setInternalState(SysOutOverSLF4J.class, loggerMock);
        Whitebox.setInternalState(SysOutOverSLF4J.class, loggingSystemRegisterMock);
    }

    @Before
    public void mockLogPerLineExceptionHandlingStrategyFactory() {
        mockStatic(LogPerLineExceptionHandlingStrategyFactory.class);
        when(LogPerLineExceptionHandlingStrategyFactory.getInstance()).thenReturn(defaultExceptionHandlingStrategyFactoryMock);
    }

    @Before
    public void mockPerContextSystemOutputEnum() {
        Whitebox.setInternalState(PerContextSystemOutput.class, "OUT", outMock);
        Whitebox.setInternalState(PerContextSystemOutput.class, "ERR", errMock);
        mockStatic(PerContextSystemOutput.class);
        when(PerContextSystemOutput.values()).thenReturn(new PerContextSystemOutput[]{outMock, errMock});
    }

    @Test
    public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndLogPerLineExceptionHandlingStrategy() throws Exception {
        expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, LogPerLineExceptionHandlingStrategyFactory.getInstance());

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        verify(outMock).registerPrintStreamForThisContext(outContextPrintStream);
        verify(errMock).registerPrintStreamForThisContext(errContextPrintStream);
    }

    @Test
    public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithDefaultLevelsAndGivenExceptionHandlingStrategy() throws Exception {
        expectLoggerAppendersToBeRegistered(LogLevel.INFO, LogLevel.ERROR, customExceptionHandlingStrategyFactoryMock);

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(customExceptionHandlingStrategyFactoryMock);

        verify(outMock).registerPrintStreamForThisContext(outContextPrintStream);
        verify(errMock).registerPrintStreamForThisContext(errContextPrintStream);
    }

    @Test
    public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndLogPerLineExceptionHandlingStrategy() throws Exception {
        expectLoggerAppendersToBeRegistered(LogLevel.DEBUG, LogLevel.WARN, LogPerLineExceptionHandlingStrategyFactory.getInstance());

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.DEBUG, LogLevel.WARN);

        verify(outMock).registerPrintStreamForThisContext(outContextPrintStream);
        verify(errMock).registerPrintStreamForThisContext(errContextPrintStream);
    }

    @Test
    public void sendSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManagerWithCustomLevelsAndGivenExceptionHandlingStrategy() throws Exception {
        expectLoggerAppendersToBeRegistered(LogLevel.DEBUG, LogLevel.WARN, customExceptionHandlingStrategyFactoryMock);

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.DEBUG, LogLevel.WARN, customExceptionHandlingStrategyFactoryMock);

        verify(outMock).registerPrintStreamForThisContext(outContextPrintStream);
        verify(errMock).registerPrintStreamForThisContext(errContextPrintStream);
    }

    private void expectLoggerAppendersToBeRegistered(LogLevel outLevel, LogLevel errLevel, ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws Exception {
        outContextPrintStream = expectLoggerAppenderToBeRegistered(outMock, outLevel, exceptionHandlingStrategyFactory);
        errContextPrintStream = expectLoggerAppenderToBeRegistered(errMock, errLevel, exceptionHandlingStrategyFactory);
    }

    private PrintStream expectLoggerAppenderToBeRegistered(PerContextSystemOutput systemOutputMock, LogLevel logLevel, ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws Exception {
        PrintStream originalPrintStreamMock = mock(PrintStream.class);
        when(systemOutputMock.getOriginalPrintStream()).thenReturn(originalPrintStreamMock);

        ExceptionHandlingStrategy exceptionHandlingStrategy = mock(ExceptionHandlingStrategy.class);
        when(exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(logLevel, originalPrintStreamMock)).thenReturn(exceptionHandlingStrategy);

        LoggingOutputStream slf4jOutputStreamMock = mock(LoggingOutputStream.class);
        whenNew(LoggingOutputStream.class).withArguments(logLevel, exceptionHandlingStrategy, originalPrintStreamMock, loggingSystemRegisterMock).thenReturn(slf4jOutputStreamMock);

        PrintStream newPrintStream = mock(PrintStream.class);
        whenNew(PrintStream.class).withArguments(slf4jOutputStreamMock, true).thenReturn(newPrintStream);

        return newPrintStream;
    }

    @Test
    public void stopSendingSystemOutAndErrToSLF4JDelegatesToSLF4JPrintStreamManager() {
        SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();

        verify(outMock).deregisterPrintStreamForThisContext();
        verify(errMock).deregisterPrintStreamForThisContext();
    }

    @Test
    public void restoreOriginalSystemOutputsDelegatesToSLF4JPrintStreamManager() {
        SysOutOverSLF4J.restoreOriginalSystemOutputs();

        verify(outMock).restoreOriginalPrintStream();
        verify(errMock).restoreOriginalPrintStream();
    }

    @Test
    public void registerLoggingSystemDelegatesToLoggingSystemRegister() {
        SysOutOverSLF4J.registerLoggingSystem("somePackageName");
        verify(loggingSystemRegisterMock).registerLoggingSystem("somePackageName");
    }

    @Test
    public void unregisterLoggingSystemDelegatesToLoggingSystemRegister() {
        SysOutOverSLF4J.unregisterLoggingSystem("somePackageName");
        verify(loggingSystemRegisterMock).unregisterLoggingSystem("somePackageName");
    }

    @Test
    public void isInLoggingSystemDelegatesToLoggingSystemRegister() {
        when(loggingSystemRegisterMock.isInLoggingSystem("somePackageName")).thenReturn(true);
        assertTrue(SysOutOverSLF4J.isInLoggingSystem("somePackageName"));

        when(loggingSystemRegisterMock.isInLoggingSystem("somePackageName")).thenReturn(false);
        assertFalse(SysOutOverSLF4J.isInLoggingSystem("somePackageName"));
    }

    @Test
    public void isSLF4JPrintStreamReturnsFalseWhenSystemOutIsSLF4JPrintStream() {
        when(outMock.isPerContextPrintStream()).thenReturn(false);
        assertFalse(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
    }

    @Test
    public void isSLF4JPrintStreamReturnsTrueWhenSystemOutIsSLF4JPrintStream() {
        when(outMock.isPerContextPrintStream()).thenReturn(true);
        assertTrue(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
    }

    @Test
    public void notInstantiable() throws Throwable {
        assertNotInstantiable(SysOutOverSLF4J.class);
    }
}
