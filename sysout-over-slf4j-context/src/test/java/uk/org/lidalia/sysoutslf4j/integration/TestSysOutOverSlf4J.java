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

package uk.org.lidalia.sysoutslf4j.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.SimpleLayout;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

public class TestSysOutOverSlf4J extends SysOutOverSLF4JTestCase {

    private static final String PACKAGE_NAME = StringUtils.substringBeforeLast(TestSysOutOverSlf4J.class.getName(), ".");
    private static final Marker STACKTRACE_MARKER = MarkerFactory.getMarker("stacktrace");
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private TestLogger log = TestLoggerFactory.getTestLogger(TestSysOutOverSlf4J.class);

    @After
    public void unregisterLoggingSystemPackage() {
        SysOutOverSLF4J.unregisterLoggingSystem(PACKAGE_NAME);
    }

    @Test
    public void systemOutNoLongerGoesToSystemOut() throws Exception {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.println("Hello again");

        assertThat(sysOutMock.toString(), not(containsString("Hello again")));
    }

    private OutputStream setUpMockSystemOutput(SystemOutput systemOutput) {
        OutputStream sysOutMock = new ByteArrayOutputStream();
        systemOutput.set(new PrintStream(sysOutMock));
        return sysOutMock;
    }

    @Test
    public void systemOutLoggedAsInfo() throws Exception {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.println("Hello World");

        assertEquals(asList(info("Hello World")), log.getLoggingEvents());
    }

    @Test
    public void systemErrLoggedAsError() throws Exception {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.err.println("Hello World");

        assertEquals(asList(error("Hello World")), log.getLoggingEvents());
    }

    @Test
    public void juliConsoleAppenderStillLogsToConsole() throws Exception {
        OutputStream newSysErr = setUpMockSystemOutput(SystemOutput.ERR);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        java.util.logging.Logger log = java.util.logging.Logger.getLogger("");
        for (Handler handler : log.getHandlers()) {
            log.removeHandler(handler);
        }
        log.addHandler(new ConsoleHandler());
        log.info("Should reach the old syserr");

        assertThat(newSysErr.toString(), containsString("INFO: Should reach the old syserr"));
    }

    @Test
    public void log4JConsoleAppenderStillLogsToConsole() throws Exception {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        org.apache.log4j.Logger log = configureLog4jLoggerToUseConsoleAppender();
        log.info("Should reach the old sysout");

        assertThat(sysOutMock.toString(), containsString("INFO - Should reach the old sysout"));
    }

    private org.apache.log4j.Logger configureLog4jLoggerToUseConsoleAppender() {
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();
        log.setLevel(org.apache.log4j.Level.INFO);
        log.removeAllAppenders();
        log.addAppender(new org.apache.log4j.ConsoleAppender(new SimpleLayout()));
        return log;
    }

    @Test
    public void printMethodsAreLogged() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.print("Hello World");
        System.out.print(true);
        System.out.print(1);
        System.out.print('c');
        System.out.print('\n');

        assertEquals(asList(info("Hello Worldtrue1c")), log.getLoggingEvents());
    }

    private static final int FOUR = 4;

    @Test
    public void appendMethodsAreLogged() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.append('c');
        System.out.append("Hello");
        System.out.append("Hello", 0, FOUR);
        System.out.println();

        assertEquals(asList(info("cHelloHell")), log.getLoggingEvents());
    }

    @Test
    public void formatMethodsAreLogged() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.format("Hello %1$s", "World");
        System.out.format(Locale.getDefault(), "Disciples: %1$s\r\n", 12);

        assertEquals(asList(info("Hello WorldDisciples: 12")), log.getLoggingEvents());
    }

    @Test
    public void printfMethods() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.printf("Hello %1$s", "World");
        System.out.printf(Locale.getDefault(), "Disciples: %1$s\r\n", 12);

        assertEquals(asList(info("Hello WorldDisciples: 12")), log.getLoggingEvents());
    }

    @Test
    public void printStackTrace() {

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        Exception exception = new Exception();
        exception.printStackTrace();

        assertExpectedStackTraceLoggingEvents(exception, Level.ERROR);
    }

    private void assertExpectedStackTraceLoggingEvents(Exception exception, Level level) {
        assertEquals(new LoggingEvent(level, STACKTRACE_MARKER, exception.toString()), log.getLoggingEvents().get(0));
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            assertEquals(new LoggingEvent(level, STACKTRACE_MARKER, "\tat " + stackTrace[i].toString()), log.getLoggingEvents().get(i + 1));
        }
        assertEquals(exception.getStackTrace().length + 1, log.getLoggingEvents().size());
    }

    @Test
    public void printStackTraceWithSysOut() {

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        Exception exception = new Exception();
        exception.printStackTrace(System.out);

        assertExpectedStackTraceLoggingEvents(exception, Level.INFO);
    }

    @Test
    public void innerClassLoggedAsOuterClass() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        (new Runnable() {
            public void run() {
                System.out.println("From inner class");
            }
        }).run();

        assertEquals(asList(info("From inner class")), log.getLoggingEvents());
    }

    @Test
    public void registeredLoggingSystemCanStillGetToConsole() {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        SysOutOverSLF4J.registerLoggingSystem(PACKAGE_NAME);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.println("Should reach console");

        assertThat(sysOutMock.toString(), containsString("Should reach console" + LINE_SEPARATOR));
    }

    @Test
    public void levelsAreConfigurable() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(Level.DEBUG, Level.WARN);

        System.out.println("Message 1");
        System.err.println("Message 2");

        assertEquals(asList(
                debug("Message 1"),
                warn("Message 2")),
                log.getLoggingEvents());
    }

    @Test
    public void stopSendingSystemOutAndErrToSLF4JSendsOutputToOldSystemOut() {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();

        System.out.println("Hello");

        assertThat(sysOutMock.toString(), containsString("Hello" + LINE_SEPARATOR));
    }

    @Test
    public void stopSendingSystemOutAndErrToSLF4JLeavesSLF4JPrintStreams() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        PrintStream newOutPrintStream = System.out;
        PrintStream newErrPrintStream = System.err;

        SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();

        assertSame(newOutPrintStream, System.out);
        assertSame(newErrPrintStream, System.err);
    }

    @Test
    public void restoreOriginalSystemOutputsRestoresOldPrintStreams() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SysOutOverSLF4J.restoreOriginalSystemOutputs();
        assertSame(SYS_OUT, System.out);
        assertSame(SYS_ERR, System.err);
    }

    @Test
    public void methodsCanBeCalledMultipleTimes() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        SysOutOverSLF4J.restoreOriginalSystemOutputs();
        SysOutOverSLF4J.restoreOriginalSystemOutputs();
        SysOutOverSLF4J.restoreOriginalSystemOutputs();
        assertSame(SYS_OUT, System.out);
        assertSame(SYS_ERR, System.err);
    }

    @Test
    public void nullBehaviour() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        System.out.print((Object) null);
        System.out.println((Object) null);
        System.out.print((String) null);
        System.out.println((String) null);

        assertEquals(asList(info("nullnull"), info("nullnull")), log.getLoggingEvents());
    }

    @Test
    public void isSLF4JPrintStreamReturnsFalseWhenSystemOutIsSLF4JPrintStream() {
        assertFalse(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
    }

    @Test
    public void isSLF4JPrintStreamReturnsTrueWhenSystemOutIsSLF4JPrintStream() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        assertTrue(SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams());
    }

    @Test
    public void bufferDoesNotGrowForever() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        for (int i = 1; i <= 3; i++) {
            System.out.print("message\nprompt>");
        }
        assertEquals(asList(info("message"), info("prompt>message"), info("prompt>message")), log.getLoggingEvents());
    }
}
