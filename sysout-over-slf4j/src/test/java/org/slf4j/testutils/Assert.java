package org.slf4j.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class Assert {

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E shouldThrow(Class<E> throwableType, Callable<?> callable) throws Throwable {
		try {
			callable.call();
		} catch (Throwable t) {
			if (instanceOf(t, throwableType)) {
				return (E) t;
			}
			throw t;
		}
		fail("No exception thrown");
		return null;
	}
	
	public static void shouldThrow(Throwable expected, Callable<?> callable) throws Throwable {
		try {
			callable.call();
		} catch (Throwable actual) {
			if (instanceOf(actual, expected.getClass())) {
				assertSame(expected, actual);
				return;
			}
			throw actual;
		}
		fail("No exception thrown");
	}
	
	public static boolean instanceOf(Object o, Class<?> c) {
		return c.isAssignableFrom(o.getClass());
	}
	
	private Assert() {
		throw new UnsupportedOperationException("Not instantiable");
	}

	public static void assertExpectedLoggingEvent(
			ILoggingEvent loggingEvent, String message, Level level, Marker marker, String className) {
		assertEquals(message, loggingEvent.getMessage());
		assertEquals(level, loggingEvent.getLevel());
		assertEquals(className, loggingEvent.getLoggerName());
		assertEquals(marker, loggingEvent.getMarker());
	}
}
