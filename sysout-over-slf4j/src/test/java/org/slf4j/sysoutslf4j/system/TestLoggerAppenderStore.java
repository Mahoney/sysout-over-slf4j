package org.slf4j.sysoutslf4j.system;

import static java.lang.Thread.currentThread;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
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
	
	@Test
	public void getLeavesLockUnlockedIfExceptionOccurs() throws Throwable {
		// Force getting an object from the logger appender map to throw an exception
		Map<?, ?> loggerAppenderMapMock = createMock(Map.class);
		Map<?, ?> loggerAppenderMap = Whitebox.getInternalState(storeUnderTest, Map.class);
		Whitebox.setInternalState(storeUnderTest, loggerAppenderMapMock);
		expect(loggerAppenderMapMock.get(anyObject())).andThrow(new RuntimeException());
		replay(loggerAppenderMapMock);
		
		// Make the exception get thrown
		shouldThrow(RuntimeException.class, new Callable<Void>() {
			public Void call() throws Exception {
				storeUnderTest.get();
				return null;
			}
		});
		
		// Check that normal operation still works
		Whitebox.setInternalState(storeUnderTest, loggerAppenderMap);
		LoggerAppender loggerAppender = createMock(LoggerAppender.class);
		storeUnderTest.set(loggerAppender);
		assertSame(loggerAppender, storeUnderTest.get());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setLeavesLockUnlockedIfExceptionOccurs() throws Throwable {
		// Force putting an object into the logger appender map to throw an exception
		Map<Object, Object> loggerAppenderMapMock = createMock(Map.class);
		Map<?, ?> loggerAppenderMap = Whitebox.getInternalState(storeUnderTest, Map.class);
		Whitebox.setInternalState(storeUnderTest, loggerAppenderMapMock);
		final LoggerAppender loggerAppender = createMock(LoggerAppender.class);
		expect(loggerAppenderMapMock.put(anyObject(), anyObject())).andThrow(new RuntimeException());
		replay(loggerAppenderMapMock);
		
		// Make the exception get thrown
		shouldThrow(RuntimeException.class, new Callable<Void>() {
			public Void call() throws Exception {
				storeUnderTest.set(loggerAppender);
				return null;
			}
		});
		
		// Check that normal operation still works
		Whitebox.setInternalState(storeUnderTest, loggerAppenderMap);
		storeUnderTest.set(loggerAppender);
		assertSame(loggerAppender, storeUnderTest.get());
	}
}
