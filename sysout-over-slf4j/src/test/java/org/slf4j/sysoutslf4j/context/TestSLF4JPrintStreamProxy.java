package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

public class TestSLF4JPrintStreamProxy {
	
	@Test
	public void wrapReturnsUnwrappedSLF4JPrintStreamIfInSameClassLoader() {
		SLF4JPrintStream expected = createMock(SLF4JPrintStream.class);
		assertSame(expected, SLF4JPrintStreamProxy.wrap(expected));
	}
	
	@Test
	public void wrapReturnsWrappedSLF4JPrintStreamIfInDifferentClassLoader() throws Exception {
		Object loggerAppender = new Object();
		FakeSLF4JPrintStream slf4jPrintStream = createMock(FakeSLF4JPrintStream.class);
		PrintStream originalPrintStream = createMock(PrintStream.class);
		expect(slf4jPrintStream.getOriginalPrintStream()).andStubReturn(originalPrintStream);
		slf4jPrintStream.registerLoggerAppender(loggerAppender);
		replayAll();
		
		SLF4JPrintStream wrappedSlf4jPrintStream = SLF4JPrintStreamProxy.wrap(slf4jPrintStream);
		assertEquals(originalPrintStream, wrappedSlf4jPrintStream.getOriginalPrintStream());
		wrappedSlf4jPrintStream.registerLoggerAppender(loggerAppender);
		verifyAll();
	}
	
	private static interface FakeSLF4JPrintStream {
		PrintStream getOriginalPrintStream();
		void registerLoggerAppender(Object loggerAppender);
	}
	
	@Test
	public void wrapThrowsNestedNoSuchMethodExceptionIfInstantiatedWithWrongType() throws Throwable {
		IllegalArgumentException iae = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			public Void call() throws Exception {
				SLF4JPrintStreamProxy.wrap(new Object());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, iae.getCause().getClass());
		assertEquals("Must only be instantiated with an SLF4JPrintStream instance, got a class java.lang.Object", iae.getMessage());
	}
}
