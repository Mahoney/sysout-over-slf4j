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

package org.slf4j.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.powermock.reflect.Whitebox;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class Assert {

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E shouldThrow(Class<E> throwableType, Callable<Void> callable) throws Throwable {
		try {
			callable.call();
			fail("No exception thrown");
			return null;
		} catch (Throwable t) {
			if (instanceOf(t, throwableType)) {
				return (E) t;
			}
			throw t;
		}
	}
	
	public static void shouldThrow(Throwable expected, Callable<?> callable) throws Throwable {
		try {
			callable.call();
			fail("No exception thrown");
		} catch (Throwable actual) {
			if (instanceOf(actual, expected.getClass())) {
				assertSame(expected, actual);
				return;
			}
			throw actual;
		}
	}
	
	public static boolean instanceOf(Object o, Class<?> c) {
		return c.isAssignableFrom(o.getClass());
	}
	
	private Assert() {
		throw new UnsupportedOperationException("Not instantiable");
	}

	public static void assertExpectedLoggingEvent(
			ILoggingEvent loggingEvent, String message, Level level, String className) {
		assertExpectedLoggingEvent(loggingEvent, message, level, null, className, null);
	}
	
	public static void assertExpectedLoggingEvent(
			ILoggingEvent loggingEvent, String message, Level level, Marker marker, String className) {
		assertExpectedLoggingEvent(loggingEvent, message, level, marker, className, null);
	}
	
	public static void assertExpectedLoggingEvent(
			ILoggingEvent loggingEvent, String message, Level level, String className, Throwable throwable) {
		assertExpectedLoggingEvent(loggingEvent, message, level, null, className, throwable);
	}
	
	public static void assertExpectedLoggingEvent(
			ILoggingEvent loggingEvent, String message, Level level, Marker marker, String className, Throwable throwable) {
		assertEquals(message, loggingEvent.getMessage());
		assertEquals(level, loggingEvent.getLevel());
		assertEquals(className, loggingEvent.getLoggerName());
		assertEquals(marker, loggingEvent.getMarker());
		IThrowableProxy throwableProxy = loggingEvent.getThrowableProxy();
		assertThrowableProxyEquals(throwable, throwableProxy);
	}

	private static void assertThrowableProxyEquals(Throwable throwable, IThrowableProxy throwableProxy) {
		if (throwable != null) {
			assertEquals(throwable.getClass().getName(), throwableProxy.getClassName());
			assertEquals(throwable.getMessage(), throwableProxy.getMessage());
			assertThrowableProxyEquals(throwable.getCause(), throwableProxy.getCause());
		} else {
			assertNull(throwableProxy);
		}
	}
	
	public static void assertNotInstantiable(final Class<?> classThatShouldNotBeInstantiable) throws Throwable {
		assertOnlyHasNoArgsConstructor(classThatShouldNotBeInstantiable);
		
		UnsupportedOperationException oue = shouldThrow(UnsupportedOperationException.class, new Callable<Void>() {
			public Void call() throws Exception {
				Whitebox.invokeConstructor(classThatShouldNotBeInstantiable);
				return null;
			}
		});
		assertEquals("Not instantiable", oue.getMessage());
	}

	private static void assertOnlyHasNoArgsConstructor(final Class<?> classThatShouldNotBeInstantiable) {
		assertEquals(Object.class, classThatShouldNotBeInstantiable.getSuperclass());
		assertEquals(1, classThatShouldNotBeInstantiable.getDeclaredConstructors().length);
		final Constructor<?> constructor = classThatShouldNotBeInstantiable.getDeclaredConstructors()[0];
		assertEquals(0, constructor.getParameterTypes().length);
	}
}
