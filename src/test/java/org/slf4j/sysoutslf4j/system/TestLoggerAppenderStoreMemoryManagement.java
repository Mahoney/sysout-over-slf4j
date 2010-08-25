package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.PrintStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.context.LogLevel;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import org.slf4j.testutils.SimpleClassloader;

public class TestLoggerAppenderStoreMemoryManagement extends SysOutOverSLF4JTestCase {

	private final LoggerAppenderStore storeUnderTest = new LoggerAppenderStore();

	private SimpleClassloader classLoader = SimpleClassloader.make();
	private final WeakReference<ClassLoader> refToClassLoader =
		new WeakReference<ClassLoader>(classLoader, new ReferenceQueue<Object>());

	private Object loggerAppenderObject = null;
	private WeakReference<Object> refToLoggerAppenderObject = null;

	@Before
	public void buildLoggerAppenderFromClassLoader() throws Exception {
		Class<?> exceptionHandlerClass = classLoader.loadClass(ExceptionHandlingStrategy.class.getName());
		Class<?> loggerAppenderClass = classLoader.loadClass("org.slf4j.sysoutslf4j.context.LoggerAppenderImpl");
		Class<?> logLevelClass = classLoader.loadClass(LogLevel.class.getName());
		Constructor<?> loggerAppenderConstructor = Whitebox.getConstructor(
				loggerAppenderClass, logLevelClass, exceptionHandlerClass, PrintStream.class);
		loggerAppenderObject = loggerAppenderConstructor.newInstance(null, null, null);
		refToLoggerAppenderObject = new WeakReference<Object>(loggerAppenderObject, new ReferenceQueue<Object>());
	}

	@Test @Ignore
	public void loggerAppenderStoreMaintainsReferenceToLoggerAppenderWhileReferenceToClassLoaderExists() throws Exception {
		storeLoggerAppenderAgainstClassLoader();
		removeLocalReferenceToLoggerAppenderAndGarbageCollect();
		assertLoggerAppenderHasNotBeenGarbageCollected();
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

	private void assertLoggerAppenderHasNotBeenGarbageCollected() {
		assertNotNull(refToLoggerAppenderObject.get());
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