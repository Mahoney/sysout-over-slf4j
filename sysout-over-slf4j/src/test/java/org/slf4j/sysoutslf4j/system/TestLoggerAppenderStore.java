package org.slf4j.sysoutslf4j.system;

import static java.lang.Thread.currentThread;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

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
			assertEquals(loggerAppenders[i], storeUnderTest.get());
		}
	}
	
	@Test
	public void loggerAppenderWorksIfContextClassLoaderIsNull() {
		currentThread().setContextClassLoader(null);
		LoggerAppender loggerAppender = createMock(LoggerAppender.class);
		storeUnderTest.set(loggerAppender);
		assertEquals(loggerAppender, storeUnderTest.get());
	}
}
