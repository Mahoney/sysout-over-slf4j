package org.slf4j.sysoutslf4j.common;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.ThrowableEquals.eqExceptionCause;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExceptionUtils.class)
public class TestReflectionUtils {
	
	@Test
	public void invokeMethodCallsMethod() {
		assertEquals(2, ReflectionUtils.invokeMethod("length", "aa"));
	}

	@Test
	public void invokeMethodThrowsNoSuchMethodExceptionNestedInIllegalStateExceptionWhenNoSuchMethod() throws Throwable {
		final RuntimeException runtimeException = shouldThrow(RuntimeException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ReflectionUtils.invokeMethod("methodThatDoesntExist", new Object());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, runtimeException.getCause().getClass());
	}
	
	@Test
	public void invokeMethodCoercesExceptionToRuntimeException() throws Throwable {
		
		final CharSequence target = createMock(CharSequence.class);
		RuntimeException expectedException = new RuntimeException();
		expect(target.length()).andThrow(expectedException);
		
		mockStatic(ExceptionUtils.class);
		expect(ExceptionUtils.asRuntimeException(
				and(isA(InvocationTargetException.class), eqExceptionCause(expectedException)))).andReturn(expectedException);
		replayAll();
		
		shouldThrow(expectedException, new Callable<Void>() {
			public Void call() throws Exception {
				ReflectionUtils.invokeMethod("length", target);
				return null;
			}
		});
		
		verifyAll();
	}
	
	@Test
	public void invokeMethodWithArgCallsMethod() {
		assertEquals("world", ReflectionUtils.invokeMethod("substring", "helloworld", int.class, 5));
	}
	
	@Test
	public void invokeStaticMethodCallsMethod() {
		assertEquals(System.getenv(), ReflectionUtils.invokeStaticMethod("getenv", System.class));
	}
	
	@Test
	public void invokeStaticMethodWithArgCallsMethod() {
		assertEquals("5", ReflectionUtils.invokeStaticMethod("valueOf", String.class, int.class, 5));
	}
	
	@Test
	public void notInstantiable() throws Exception {
		assertNotInstantiable(ReflectionUtils.class);
	}
}
