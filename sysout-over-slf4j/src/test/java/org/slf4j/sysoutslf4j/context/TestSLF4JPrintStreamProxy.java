package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReflectionUtils.class, Class.class, TestSLF4JPrintStreamProxy.class })
public class TestSLF4JPrintStreamProxy {
	
	@Test
	public void loggerAppenderProxyThrowsNestedNoSuchMethodExceptionIfInstantiatedWithWrongType() throws Throwable {
		IllegalArgumentException iae = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			public Void call() throws Exception {
				new SLF4JPrintStreamProxy(new Object());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, iae.getCause().getClass());
		assertEquals("Must only be instantiated with an SLF4JPrintStream instance, got a class java.lang.Object", iae.getMessage());
	}

	private final SLF4JPrintStream targetSLF4JPrintStream = createNiceMock(SLF4JPrintStream.class);
	private final SLF4JPrintStreamProxy proxy = new SLF4JPrintStreamProxy(targetSLF4JPrintStream);
	private final Class<?> targetSLF4JPrintStreamClass = targetSLF4JPrintStream.getClass();
	
	@Test
	public void appendCallsAppendOnTargetViaReflection() throws Exception {
		mockStatic(ReflectionUtils.class);
		Method expectedMethod = targetSLF4JPrintStreamClass.getMethod("getOriginalPrintStream");
		PrintStream expectedResult = createNiceMock(PrintStream.class);
		expect(ReflectionUtils.invokeMethod(expectedMethod, targetSLF4JPrintStream)).andReturn(expectedResult);
		replayAll();
		
		assertSame(expectedResult, proxy.getOriginalPrintStream());
		verifyAll();
	}
	
	@Test
	public void testAppendAndLogCallsAppendAndLogOnTargetViaReflection() throws Exception {
		mockStatic(ReflectionUtils.class);
		Method expectedMethod = targetSLF4JPrintStreamClass.getMethod("registerLoggerAppender", Object.class);
		Object loggerAppender = new Object();
		expect(ReflectionUtils.invokeMethod(
				expectedMethod, targetSLF4JPrintStream, loggerAppender)).andReturn(Void.TYPE);
		replayAll();
		
		proxy.registerLoggerAppender(loggerAppender);
		verifyAll();
	}
}
