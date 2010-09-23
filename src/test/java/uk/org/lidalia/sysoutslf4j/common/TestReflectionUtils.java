/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.common;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static uk.org.lidalia.testutils.Assert.assertNotInstantiable;
import static uk.org.lidalia.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.ClassLoaderUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.ExceptionUtils;
import uk.org.lidalia.sysoutslf4j.common.ReflectionUtils;

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
				ReflectionUtils.invokeMethod("methodThatDoesntExist", new String());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, runtimeException.getCause().getClass());
		assertEquals("java.lang.String.methodThatDoesntExist()", runtimeException.getCause().getMessage());
	}
	
	@Test
	public void invokeMethodCoercesExceptionToRuntimeException() throws Throwable {
		
		final CharSequence target = createMock(CharSequence.class);
		RuntimeException expectedException = new RuntimeException();
		expect(target.length()).andThrow(expectedException);		
		replayAll();

		shouldThrow(expectedException, new Callable<Void>() {
			public Void call() throws Exception {
				ReflectionUtils.invokeMethod("length", target);
				return null;
			}
		});
	}
	
	@Test
	public void invokeProtectedMethodOnSuperclass() {
		SubClass subClass = new SubClass();
		assertEquals("invoked", ReflectionUtils.invokeMethod("protectedMethod", subClass));
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
	
	private static class ClassWithProtectedMethod {
		@SuppressWarnings("unused")
		protected String protectedMethod() {
			return "invoked";
		}
	}
	private static class SubClass extends ClassWithProtectedMethod {
		
	}
}
