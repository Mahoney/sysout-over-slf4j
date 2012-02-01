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

package uk.org.lidalia.sysoutslf4j.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.SimpleLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;
import uk.org.lidalia.testutils.Assert;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.layout.EchoLayout;

public class TestSysOutOverSlf4J extends SysOutOverSLF4JTestCase {

	private static final String PACKAGE_NAME = StringUtils.substringBeforeLast(TestSysOutOverSlf4J.class.getName(), ".");
	private static final Marker STACKTRACE_MARKER = MarkerFactory.getMarker("stacktrace");
	
	private Logger log = (Logger) LoggerFactory.getLogger(TestSysOutOverSlf4J.class);

	@Before
	public void setUp() {
		log.setLevel(Level.TRACE);
	}

	@After
	public void unregisterLoggingSystemPackage() {
		SysOutOverSLF4J.unregisterLoggingSystem(PACKAGE_NAME);
	}

	@Test
	public void systemOutNoLongerGoesToSystemOut() throws Exception {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.out.println("Hello again");
		
		assertEquals("", sysOutMock.toString());
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
		
		assertExpectedLoggingEvent(appender.list.get(0), "Hello World", Level.INFO);
	}
	
	@Test
	public void systemErrLoggedAsError() throws Exception {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.err.println("Hello World");
		
		assertExpectedLoggingEvent(appender.list.get(0), "Hello World", Level.ERROR);
	}
	
	@Test
	public void logBackConsoleAppenderStillLogsToConsole() throws Exception {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		configureLogBackConsoleAppender();
		log.info("Should reach the old sysout");
		
		assertEquals("[INFO] Should reach the old sysout" + CoreConstants.LINE_SEPARATOR, sysOutMock.toString());
	}

	private void configureLogBackConsoleAppender() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger log = lc.getLogger(Logger.ROOT_LOGGER_NAME);
		ConsoleAppender<ILoggingEvent> app = new ConsoleAppender<ILoggingEvent>();
		app.setContext((Context) LoggerFactory.getILoggerFactory());
		app.setLayout(new EchoLayout<ILoggingEvent>());
		app.start();
		log.addAppender(app);
		
		Logger consoleLogger = lc.getLogger(ConsoleTarget.class);
		consoleLogger.setLevel(Level.ALL);
		consoleLogger.addAppender(app);
	}
	
	@Test
	public void juliConsoleAppenderStillLogsToConsole() throws Exception {
		OutputStream newSysErr = setUpMockSystemOutput(SystemOutput.ERR);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		java.util.logging.Logger log = java.util.logging.Logger.getLogger(getClass().getCanonicalName());
		log.info("Should reach the old syserr");
		
		assertTrue(newSysErr.toString().contains("INFO: Should reach the old syserr"));
	}
	
	@Test
	public void log4JConsoleAppenderStillLogsToConsole() throws Exception {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		org.apache.log4j.Logger log = configureLog4jLoggerToUseConsoleAppender();
		log.info("Should reach the old sysout");
		
		assertEquals("INFO - Should reach the old sysout" + CoreConstants.LINE_SEPARATOR, sysOutMock.toString());
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
		
		assertExpectedLoggingEvent(appender.list.get(0), "Hello Worldtrue1c", Level.INFO);
	}
	
	private static final int FOUR = 4;
	
	@Test
	public void appendMethodsAreLogged() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.out.append('c');
		System.out.append("Hello");
		System.out.append("Hello", 0, FOUR);
		System.out.println();
		
		assertExpectedLoggingEvent(appender.list.get(0), "cHelloHell", Level.INFO);
	}
	
	@Test
	public void formatMethodsAreLogged() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.out.format("Hello %1$s", "World");
		System.out.format(Locale.getDefault(), "Disciples: %1$s\r\n", 12);
		
		assertExpectedLoggingEvent(appender.list.get(0), "Hello WorldDisciples: 12", Level.INFO);
	}
	
	@Test
	public void printfMethods() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.out.printf("Hello %1$s", "World");
		System.out.printf(Locale.getDefault(), "Disciples: %1$s\r\n", 12);
		
		assertEquals("Hello WorldDisciples: 12", appender.list.get(0).getMessage());
	}
	
	@Test
	public void printStackTrace() {
		
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		Exception exception = new Exception();
		exception.printStackTrace();
		
		assertExpectedStackTraceLoggingEvents(exception, Level.ERROR);
	}

	private void assertExpectedStackTraceLoggingEvents(Exception exception, Level level) {
		assertExpectedLoggingEvent(appender.list.get(0), exception.toString(), level, STACKTRACE_MARKER);
		StackTraceElement[] stackTrace = exception.getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			assertExpectedLoggingEvent(appender.list.get(i + 1), "\tat " + stackTrace[i].toString(), level, STACKTRACE_MARKER);
		}
		assertEquals(exception.getStackTrace().length + 1, appender.list.size());
	}
	
	@Test
	public void printStackTraceWithSysOut() {
		
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		Exception exception = new Exception();
		exception.printStackTrace(System.out);
		
		assertExpectedStackTraceLoggingEvents(exception, Level.INFO);
	}
	
	private void assertExpectedLoggingEvent(ILoggingEvent loggingEvent, String message, Level level) {
		Assert.assertExpectedLoggingEvent(loggingEvent, message, level, null, getClass().getName());
	}
	
	private void assertExpectedLoggingEvent(ILoggingEvent loggingEvent, String message, Level level, Marker marker) {
		Assert.assertExpectedLoggingEvent(loggingEvent, message, level, marker, getClass().getName());
	}
	
	@Test
	public void innerClassLoggedAsOuterClass() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		(new Runnable() {
			public void run() {
				System.out.println("From inner class");
			}
		}).run();
		
		Assert.assertExpectedLoggingEvent(appender.list.get(0), "From inner class", Level.INFO, null, getClass().getName());
	}

	@Test
	public void registeredLoggingSystemCanStillGetToConsole() {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		SysOutOverSLF4J.registerLoggingSystem(PACKAGE_NAME);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		System.out.println("Should reach console");
		
		assertEquals("Should reach console" + CoreConstants.LINE_SEPARATOR, sysOutMock.toString());
	}
	
	@Test
	public void levelsAreConfigurable() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.DEBUG, LogLevel.WARN);
		
		System.out.println("Message 1");
		System.err.println("Message 2");
		
		assertExpectedLoggingEvent(appender.list.get(0), "Message 1", Level.DEBUG);
		assertExpectedLoggingEvent(appender.list.get(1), "Message 2", Level.WARN);
	}
	
	@Test
	public void stopSendingSystemOutAndErrToSLF4JSendsOutputToOldSystemOut() {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
		
		System.out.println("Hello");
		
		assertEquals("Hello" + CoreConstants.LINE_SEPARATOR, sysOutMock.toString());
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

		assertExpectedLoggingEvent(appender.list.get(0), "nullnull", Level.INFO);
		assertExpectedLoggingEvent(appender.list.get(1), "nullnull", Level.INFO);
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
		assertEquals(3, appender.list.size());
        assertExpectedLoggingEvent(appender.list.get(0), "message", Level.INFO);
        assertExpectedLoggingEvent(appender.list.get(1), "prompt>message", Level.INFO);
        assertExpectedLoggingEvent(appender.list.get(2), "prompt>message", Level.INFO);
	}
}
