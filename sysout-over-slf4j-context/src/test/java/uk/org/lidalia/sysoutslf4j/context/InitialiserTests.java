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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.read.ListAppender;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jutils.Level;
import uk.org.lidalia.testutils.ClassCreationUtils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggingSystemRegister.class })
public class InitialiserTests {

    private Initialiser initialiser;
    private LoggingSystemRegister loggingSystemRegister;
    private Logger loggerImplementation;

    public InitialiserTests() {
        loggingSystemRegister = mock(LoggingSystemRegister.class);
        initialiser = new Initialiser(loggingSystemRegister);
    }

    @Test
    public void initialiseWithX4JuliRegistersLoggingPackageAutomatically() throws Exception {
        Logger x4juliLogger = makeMockLogger("org.x4juli.X4JuliLogger");

        givenLoggerIsA(x4juliLogger);
        whenInitialiseIsCalled();
        thenX4juliIsRegistered();
    }

    @Test
    public void initialiseWithGrleaSimpleLoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger grleaSimpleLogger = makeMockLogger("org.grlea.log.adapters.slf4j.Slf4jAdapter");

        givenLoggerIsA(grleaSimpleLogger);
        whenInitialiseIsCalled();
        thenGrleaSimpleLoggerIsRegistered();
    }

    @Test
    public void initialiseWithSlf4jSimpleLoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger slf4jSimpleLogger = makeMockLogger("org.slf4j.impl.SimpleLogger");

        givenLoggerIsA(slf4jSimpleLogger);
        whenInitialiseIsCalled();
        thenSlf4jImplementationIsRegistered();
    }

    @Test
    public void initialiseWithLogbackLoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger logbackLogger = makeMockLogger("ch.qos.logback.classic.Logger");

        givenLoggerIsA(logbackLogger);
        whenInitialiseIsCalled();
        verify(loggingSystemRegister).registerLoggingSystem("ch.qos.logback.");
    }

    @Test
    public void initialiseWithNLog4JLoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger nlog4jLogger = makeMockLogger("org.apache.log4j.NLogger");

        givenLoggerIsA(nlog4jLogger);
        whenInitialiseIsCalled();
        verify(loggingSystemRegister).registerLoggingSystem("org.apache.log4j.");
    }

    @Test
    public void initialiseWithLog4JLoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger log4jLogger = makeMockLogger("org.slf4j.impl.Log4jLoggerAdapter");

        givenLoggerIsA(log4jLogger);
        whenInitialiseIsCalled();
        thenSlf4jImplementationIsRegistered();
    }

    @Test
    public void initialiseWithJDK14LoggerRegistersLoggingPackageAutomatically() throws Exception {
        Logger jdk14Logger = makeMockLogger("org.slf4j.impl.JDK14LoggerAdapter");

        givenLoggerIsA(jdk14Logger);
        whenInitialiseIsCalled();
        thenSlf4jImplementationIsRegistered();
    }

    private Logger makeMockLogger(String loggerClassName) throws Exception {
        Class<Logger> mockLoggerClass = makeMockLoggerClass(loggerClassName);
        return mock(mockLoggerClass);
    }

    @SuppressWarnings("unchecked")
    private Class<Logger> makeMockLoggerClass(String loggerClassName) throws Exception {
        return (Class<Logger>) ClassCreationUtils.makeClass(loggerClassName, Logger.class);
    }

    private void givenLoggerIsA(Logger logger) throws Exception {
        loggerImplementation = logger;
    }

    private void whenInitialiseIsCalled() {
        initialiser.initialise(loggerImplementation);
    }

    private void thenX4juliIsRegistered() {
        verify(loggingSystemRegister).registerLoggingSystem("org.x4juli.");
    }

    private void thenGrleaSimpleLoggerIsRegistered() {
        verify(loggingSystemRegister).registerLoggingSystem("org.grlea.log.");
    }

    private void thenSlf4jImplementationIsRegistered() {
        verify(loggingSystemRegister).registerLoggingSystem("org.slf4j.impl.");
    }

    @Test
    public void initialiseWithUnknownLoggingSystemLogsWarnMessage() throws Exception {
        Logger unknownLogger = mock(Logger.class);
        givenLoggerIsA(unknownLogger);
        whenInitialiseIsCalled();
        thenAWarnMessageToSayRegistrationMayBeNecessaryShouldBeLogged(unknownLogger.getClass());
    }

    private void thenAWarnMessageToSayRegistrationMayBeNecessaryShouldBeLogged(Class<? extends Logger> unknownLoggerClass) {
        assertEquals(asList(
                warn("Your logging framework {} is not known - if it needs access to the console you " +
                "will need to register it by calling SysOutOverSLF4J.registerLoggingSystem", unknownLoggerClass)),
                TestLoggerFactory.getTestLogger(Initialiser.class).getLoggingEvents());
    }
}