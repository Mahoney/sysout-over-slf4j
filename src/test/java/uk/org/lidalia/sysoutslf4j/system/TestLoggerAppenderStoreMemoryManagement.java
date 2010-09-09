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

package uk.org.lidalia.sysoutslf4j.system;

import static org.junit.Assert.assertNull;

import java.io.PrintStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.LoggerAppender;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.system.LoggerAppenderProxy;
import uk.org.lidalia.sysoutslf4j.system.LoggerAppenderStore;
import uk.org.lidalia.testutils.SimpleClassloader;

public class TestLoggerAppenderStoreMemoryManagement extends SysOutOverSLF4JTestCase {

	private final LoggerAppenderStore storeUnderTest = new LoggerAppenderStore();

	private SimpleClassloader classLoader = SimpleClassloader.make();
	private final WeakReference<ClassLoader> refToClassLoader =
		new WeakReference<ClassLoader>(classLoader, new ReferenceQueue<Object>());

	private Object loggerAppenderObject = null;

	@Before
	public void buildLoggerAppenderFromClassLoader() throws Exception {
		Class<?> exceptionHandlerClass = classLoader.loadClass(ExceptionHandlingStrategy.class.getName());
		Class<?> loggerAppenderClass = classLoader.loadClass("uk.org.lidalia.sysoutslf4j.context.LoggerAppenderImpl");
		Class<?> logLevelClass = classLoader.loadClass(LogLevel.class.getName());
		Constructor<?> loggerAppenderConstructor = Whitebox.getConstructor(
				loggerAppenderClass, logLevelClass, exceptionHandlerClass, PrintStream.class);
		loggerAppenderObject = loggerAppenderConstructor.newInstance(null, null, null);
	}

	private void storeLoggerAppenderAgainstClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoader);
		LoggerAppender loggerAppender = LoggerAppenderProxy.wrap(loggerAppenderObject);
		storeUnderTest.put(loggerAppender);
	}

	private void removeLocalReferenceToLoggerAppenderAndGarbageCollect() {
		loggerAppenderObject = null;
		System.gc();
	}

	@Test
	public void loggerAppenderStoreDoesNotCauseAClassLoaderLeak() throws Exception {
		storeLoggerAppenderAgainstClassLoader();
		removeAllKnownReferencesToClassLoader();
		removeLocalReferenceToLoggerAppenderAndGarbageCollect();
		assertClassLoaderHasBeenGarbageCollected();
	}

	private void removeAllKnownReferencesToClassLoader() {
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
		classLoader = null;
	}

	private void assertClassLoaderHasBeenGarbageCollected() {
		assertNull(refToClassLoader.get());
	}
}