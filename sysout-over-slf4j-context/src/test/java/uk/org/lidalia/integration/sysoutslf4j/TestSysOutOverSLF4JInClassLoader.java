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

package uk.org.lidalia.integration.sysoutslf4j;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.SystemOutput;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.testutils.Assert;
import uk.org.lidalia.testutils.LoggingUtils;
import uk.org.lidalia.testutils.SimpleClassloader;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class TestSysOutOverSLF4JInClassLoader extends SysOutOverSLF4JTestCase {

	private final ClassLoader app1ClassLoader = SimpleClassloader.make();
	
	@Before
	public void prepareLogging() throws Exception {
		LoggingUtils.turnOffRootLogging(app1ClassLoader);
		resetSysOutUserAppender(app1ClassLoader);
	}
	
	@Test
	public void sysOutOverSLF4JWorksInsideAnotherClassLoader() throws Exception {
		callSendSystemOutAndErrToSLF4JInClassLoader(app1ClassLoader);
		
		ISysOutUser sysOutUser1 = newInstanceInClassLoader(ISysOutUser.class, app1ClassLoader, SysOutUser.class, new Class[]{});
		
		Thread.currentThread().setContextClassLoader(app1ClassLoader);
		sysOutUser1.useSysOut();
		
		List<?> list1 = getRootAppender(app1ClassLoader);
		assertEquals(1, list1.size());
		ILoggingEvent loggingEvent = CrossClassLoaderTestUtils.moveToCurrentClassLoader(ILoggingEvent.class, list1.get(0));
		Assert.assertExpectedLoggingEvent(loggingEvent, "Logged", Level.INFO, null, SysOutUser.class.getName());
	}

	private <E> E newInstanceInClassLoader(
			Class<E> classToReturn, ClassLoader classLoader, Class<? extends E> classToGetInstanceOf,
			Class<?>[] constructorArgTypes, Object... constructorArgs) throws Exception {
		Class<?> class1 = classLoader.loadClass(classToGetInstanceOf.getName());
		Object newInstance = Whitebox.invokeConstructor(class1, constructorArgTypes, constructorArgs);
		return CrossClassLoaderTestUtils.moveToCurrentClassLoader(classToReturn, newInstance);
	}
	
	static void resetSysOutUserAppender(ClassLoader classLoader) throws Exception {
		Class<?> clazz = classLoader.loadClass(TestSysOutOverSLF4JInClassLoader.class.getName());
		clazz.getDeclaredMethod("resetSysOutUserAppender").invoke(clazz);
	}
	
	public static void resetSysOutUserAppender() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger sysOutUserLogger = loggerContext.getLogger(SysOutUser.class.getName());
		sysOutUserLogger.detachAndStopAllAppenders();
		ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
		appender.setName("list");
		appender.setContext(loggerContext);
		appender.start();
		sysOutUserLogger.addAppender(appender);
		sysOutUserLogger.setLevel(Level.INFO);
	}
	
	static List<?> getRootAppender(ClassLoader classLoader) throws Exception {
		Class<?> clazz = classLoader.loadClass(TestSysOutOverSLF4JInClassLoader.class.getName());
		Object listAppender = clazz.getDeclaredMethod("getRootAppender").invoke(clazz);
		Class<?> listAppenderClass = classLoader.loadClass(ListAppender.class.getName());
		Field listField = listAppenderClass.getField("list");
		Object list = listField.get(listAppender);
		return CrossClassLoaderTestUtils.moveToCurrentClassLoader(List.class, list);
	}
	
	public static ListAppender<ILoggingEvent> getRootAppender() {
		LoggerContext LC = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger sysOutLogger = LC.getLogger(SysOutUser.class.getName());
		return (ListAppender<ILoggingEvent>) sysOutLogger.getAppender("list");
	}
	
	@Test
	public void systemOutStillGoesToSystemOutInClassLoaderThatHasNotSentSysOutToLSF4J() throws Exception {
		OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
		callSendSystemOutAndErrToSLF4JInClassLoader(app1ClassLoader);
		
		System.out.println("Hello again");
		
		assertEquals("Hello again" + System.getProperty("line.separator"), sysOutMock.toString());
	}

	private OutputStream setUpMockSystemOutput(SystemOutput systemOutput) {
		OutputStream sysOutMock = new ByteArrayOutputStream();
		systemOutput.set(new PrintStream(sysOutMock));
		return sysOutMock;
	}
	
	protected void callSendSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
		Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
		Thread.currentThread().setContextClassLoader(classLoader);
		sysOutOverSLF4JClass.getMethod("sendSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	}

	protected void callStopSendingSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
		Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
		Thread.currentThread().setContextClassLoader(classLoader);
		sysOutOverSLF4JClass.getMethod("stopSendingSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	}
}
