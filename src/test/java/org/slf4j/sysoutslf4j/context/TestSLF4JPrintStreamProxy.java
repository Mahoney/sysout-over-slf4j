package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

public class TestSLF4JPrintStreamProxy {
	
	@Test
	public void wrapReturnsUnwrappedSLF4JPrintStreamIfInSameClassLoader() {
		SLF4JPrintStream expected = createMock(SLF4JPrintStream.class);
		assertSame(expected, ReflectionUtils.wrap(expected, SLF4JPrintStream.class));
	}
	
	@Test
	public void wrapReturnsWrappedSLF4JPrintStreamIfInDifferentClassLoader() throws Exception {
		Object loggerAppender = new Object();
		FakeSLF4JPrintStream slf4jPrintStream = createMock(FakeSLF4JPrintStream.class);
		PrintStream originalPrintStream = createMock(PrintStream.class);
		expect(slf4jPrintStream.getOriginalPrintStream()).andStubReturn(originalPrintStream);
		slf4jPrintStream.registerLoggerAppender(loggerAppender);
		slf4jPrintStream.deregisterLoggerAppender();
		replayAll();
		
		SLF4JPrintStream wrappedSlf4jPrintStream = ReflectionUtils.wrap(slf4jPrintStream, SLF4JPrintStream.class);
		assertEquals(originalPrintStream, wrappedSlf4jPrintStream.getOriginalPrintStream());
		wrappedSlf4jPrintStream.registerLoggerAppender(loggerAppender);
		wrappedSlf4jPrintStream.deregisterLoggerAppender();
		verifyAll();
	}
	
	private static interface FakeSLF4JPrintStream {
		PrintStream getOriginalPrintStream();
		void registerLoggerAppender(Object loggerAppender);
		void deregisterLoggerAppender();
	}
}
