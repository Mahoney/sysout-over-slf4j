package org.slf4j.sysoutslf4j.common;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.slf4j.testutils.ThrowableEquals.eqExceptionCause;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExceptionUtils.class)
public class TestReflectionUtils extends SysOutOverSLF4JTestCase {
	
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
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(ReflectionUtils.class);
	}
	
	@Test
	public void wrapReturnsUnwrappedSLF4JPrintStreamIfInSameClassLoader() {
		Iterable<?> expected = createMock(Iterable.class);
		assertSame(expected, ReflectionUtils.wrap(expected, Iterable.class));
	}
	
	@Test
	public void wrapReturnsWrappedSLF4JPrintStreamIfInDifferentClassLoader() throws Exception {
		ExampleInterfaceWithSameMethods instanceToProxy = createMock(ExampleInterfaceWithSameMethods.class);
		Object result = new Object();
		expect(instanceToProxy.get("anarg")).andStubReturn(result);
		replayAll();
		
		ExampleInterface wrappedInstance = ReflectionUtils.wrap(instanceToProxy, ExampleInterface.class);
		assertEquals(result, wrappedInstance.get("anarg"));
	}
	
	private static interface ExampleInterface {
		Object get(String argument);
	}
	
	private static interface ExampleInterfaceWithSameMethods {
		Object get(String argument);
	}
	
	@Test
	public void wrapThrowsIllegalArgumentExceptionIfCalledWithObjectThatDoesNotHaveMethodsMatchingInterfaceSignature() throws Throwable {
		final Object target = new Object();
		IllegalArgumentException exception = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ReflectionUtils.wrap(target, ExampleInterface.class);
				return null;
			}
		});
		assertEquals("Target " + target + " does not have methods to match all method signatures on class " + ExampleInterface.class, exception.getMessage());
	}
}
