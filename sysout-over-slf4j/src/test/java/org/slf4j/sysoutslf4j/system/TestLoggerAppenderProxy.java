package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.LoggerAppender;

public class TestLoggerAppenderProxy extends SysOutOverSLF4JTestCase {
	
	@Test
	public void wrapReturnsUnwrappedLoggerAppenderIfInSameClassLoader() {
		LoggerAppender expected = createMock(LoggerAppender.class);
		assertSame(expected, LoggerAppenderProxy.wrap(expected));
	}
	
	@Test
	public void wrapReturnsWrappedLoggerAppenderIfInDifferentClassLoader() throws Exception {
		FakeLoggerAppender loggerAppender = createMock(FakeLoggerAppender.class);
		loggerAppender.append("message");
		loggerAppender.appendAndLog("message2", "classname", true);
		replayAll();
		
		LoggerAppender wrappedLoggerAppender = LoggerAppenderProxy.wrap(loggerAppender);
		wrappedLoggerAppender.append("message");
		wrappedLoggerAppender.appendAndLog("message2", "classname", true);
		verifyAll();
	}
	
	private static interface FakeLoggerAppender {
		void append(String message);
		void appendAndLog(String message, String className, boolean isStackTrace);
	}
	
	@Test
	public void wrapThrowsNestedNoSuchMethodExceptionIfInstantiatedWithWrongType() throws Throwable {
		IllegalArgumentException iae = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			public Void call() throws Exception {
				LoggerAppenderProxy.wrap(new Object());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, iae.getCause().getClass());
		assertEquals("Must only be instantiated with a LoggerAppender instance, got a class java.lang.Object", iae.getMessage());
	}
}
