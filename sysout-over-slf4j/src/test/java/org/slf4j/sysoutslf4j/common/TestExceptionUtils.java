package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.junit.Test;

public class TestExceptionUtils {

	@Test
	public void asRuntimeExceptionThrowsIllegalArgumentExceptionWhenNullPassedIn() throws Throwable {
		IllegalArgumentException iae = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(null);
				return null;
			}
		});
		assertEquals("Throwable argument cannot be null", iae.getMessage());
	}

	@Test
	public void asRuntimeExceptionThrowsPassedInError() throws Throwable {
		final Error expectedError = new Error();
		Error actualError = shouldThrow(Error.class, new Callable<Void>() {
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(expectedError);
				return null;
			}
		});
		assertSame(expectedError, actualError);
	}

	@Test
	public void asRuntimeExceptionReturnsPassedInRuntimeException() {
		RuntimeException expectedException = new RuntimeException();
		RuntimeException actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertSame(expectedException, actualException);
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInCheckedExceptionAsCauseOfWrappedCheckedException() {
		Exception expectedException = new Exception();
		RuntimeException actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertTrue(actualException instanceof WrappedCheckedException);
		assertSame(expectedException, actualException.getCause());
	}
	
	@Test
	public void asRuntimeExceptionThrowsPassedInInvocationTargetExceptionsCauseIfError() throws Throwable {
		final Error expectedError = new Error();
		final InvocationTargetException invocationTargetException = new InvocationTargetException(expectedError);

		Error actualError = shouldThrow(Error.class, new Callable<Void>() {
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(invocationTargetException);
				return null;
			}
		});
		assertSame(expectedError, actualError);
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInInvocationTargetExceptionsCauseIfRuntimeException() {
		RuntimeException expectedException = new RuntimeException();
		InvocationTargetException invocationTargetException = new InvocationTargetException(expectedException);
		RuntimeException actualException = ExceptionUtils.asRuntimeException(invocationTargetException);
		assertSame(expectedException, actualException);
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInInvocationTargetExceptionsCauseAsCauseOfWrappedCheckedExceptionIfCheckedException() {
		Exception expectedException = new Exception();
		InvocationTargetException invocationTargetException = new InvocationTargetException(expectedException);
		RuntimeException actualException = ExceptionUtils.asRuntimeException(invocationTargetException);
		assertTrue(actualException instanceof WrappedCheckedException);
		assertSame(expectedException, actualException.getCause());
	}
	
	@Test
	public void notInstantiable() throws Exception {
		assertNotInstantiable(ExceptionUtils.class);
	}
}
