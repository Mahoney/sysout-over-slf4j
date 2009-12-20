package org.slf4j.sysoutslf4j.system;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReflectionUtils.class, Class.class, TestLoggerAppenderProxy.class })
public class TestLoggerAppenderProxy {
	
	@Test
	public void loggerAppenderProxyThrowsNestedNoSuchMethodExceptionIfInstantiatedWithWrongType() {
		try {
			new LoggerAppenderProxy(new Object());
			fail();
		} catch (IllegalArgumentException iae) {
			assertSame(NoSuchMethodException.class, iae.getCause().getClass());
			assertEquals("Must only be instantiated with a LoggerAppenderImpl instance, got a class java.lang.Object",
					iae.getMessage());
		}
	}

	private final  LoggerAppender targetLoggerAppender = createNiceMock(LoggerAppender.class);
	private final  LoggerAppenderProxy proxy = new LoggerAppenderProxy(targetLoggerAppender);
	private final Class<?> targetLoggerAppenderClass = targetLoggerAppender.getClass();
	
	@Test
	public void appendCallsAppendOnTargetViaReflection() throws Exception {
		mockStatic(ReflectionUtils.class);
		Method expectedMethod = targetLoggerAppenderClass.getMethod("append", String.class);
		expect(ReflectionUtils.invokeMethod(expectedMethod, targetLoggerAppender, "expected")).andReturn(Void.TYPE);
		replayAll();
		
		proxy.append("expected");
		verifyAll();
	}
	
	@Test
	public void testAppendAndLogCallsAppendAndLogOnTargetViaReflection() throws Exception {
		mockStatic(ReflectionUtils.class);
		Method expectedMethod = targetLoggerAppenderClass.getMethod("appendAndLog", String.class, String.class, boolean.class);
		expect(ReflectionUtils.invokeMethod(
				expectedMethod, targetLoggerAppender, "expected", "java.lang.String", true)).andReturn(Void.TYPE);
		replayAll();
		
		proxy.appendAndLog("expected", "java.lang.String", true);
		verifyAll();
	}
}
