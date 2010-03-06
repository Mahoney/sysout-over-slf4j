package org.slf4j.sysoutslf4j.system;

import static java.lang.Thread.currentThread;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.LoggerAppender;

public class TestLoggerAppenderStore extends SysOutOverSLF4JTestCase {
	
	private final LoggerAppenderStore storeUnderTest = new LoggerAppenderStore();
	private final ClassLoader[] classLoaders = { new ClassLoader() { }, new ClassLoader() { } };
	private final LoggerAppender[] loggerAppenders = { createMock(LoggerAppender.class), createMock(LoggerAppender.class) };
	
	@Test
	public void loggerAppenderStoresRelativeToContextClassLoader() {
		storeAppendersAgainstDifferentContextClassLoaders();
		assertCorrectAppenderReturnedForEachClassLoader();
	}

	private void storeAppendersAgainstDifferentContextClassLoaders() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			storeUnderTest.set(loggerAppenders[i]);
		}
	}
	
	private void assertCorrectAppenderReturnedForEachClassLoader() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			assertSame(loggerAppenders[i], storeUnderTest.get());
		}
	}
	
	@Test
	public void loggerAppenderStoreWorksIfContextClassLoaderIsNull() {
		currentThread().setContextClassLoader(null);
		storeUnderTest.set(loggerAppenders[0]);
		assertNull(currentThread().getContextClassLoader());
		assertSame(loggerAppenders[0], storeUnderTest.get());
	}
	
	@Test
	public void loggerAppenderStoreReturnsLoggerAppenderStoredAgainstParentOfContextClassLoader() {
		ClassLoader parent = new ClassLoader() { };
		LoggerAppender loggerAppender = createMock(LoggerAppender.class);
		currentThread().setContextClassLoader(parent);
		storeUnderTest.set(loggerAppender);
		
		ClassLoader child = new ClassLoader(parent) { };
		currentThread().setContextClassLoader(child);
		
		assertSame(loggerAppender, storeUnderTest.get());
	}
	
	@Test
	public void loggerAppenderStoreReturnsNullIfNoClassLoaderStored() {
		assertNull(storeUnderTest.get());
	}
}
