package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class TestExceptionUtils {
	
	@Test
	public void asRuntimeExceptionThrowsIllegalArgumentExceptionWhenNullPassedIn() {
		try {
			ExceptionUtils.asRuntimeException(null);
			fail();
		} catch (IllegalArgumentException iae) {
			assertEquals("Throwable argument cannot be null", iae.getMessage());
		}
	}
	
	@Test
	public void asRuntimeExceptionThrowsPassedInError() {
		Error expectedError = new Error();
		try {
			ExceptionUtils.asRuntimeException(expectedError);
			fail();
		} catch (Error actualError) {
			assertSame(expectedError, actualError);
		}
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInRuntimeException() {
		RuntimeException expectedException = new RuntimeException();
		RuntimeException actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertSame(expectedException, actualException);
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInCheckedExceptionAsCauseOfRuntimeException() {
		Exception expectedException = new Exception();
		Throwable actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertSame(RuntimeException.class, actualException.getClass());
		assertSame(expectedException, actualException.getCause());
	}
	
	@Test
	public void asRuntimeExceptionThrowsPassedInInvocationTargetExceptionsCauseIfError() {
		Error expectedError = new Error();
		InvocationTargetException invocationTargetException = new InvocationTargetException(expectedError);
		try {
			ExceptionUtils.asRuntimeException(invocationTargetException);
			fail();
		} catch (Error actualError) {
			assertSame(expectedError, actualError);
		}
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInInvocationTargetExceptionsCauseIfRuntimeException() {
		RuntimeException expectedException = new RuntimeException();
		InvocationTargetException invocationTargetException = new InvocationTargetException(expectedException);
		RuntimeException actualException = ExceptionUtils.asRuntimeException(invocationTargetException);
		assertSame(expectedException, actualException);
	}
	
	@Test
	public void asRuntimeExceptionReturnsPassedInInvocationTargetExceptionsCauseAsCauseOfRuntimeExceptionIfCheckedException() {
		Exception expectedException = new Exception();
		InvocationTargetException invocationTargetException = new InvocationTargetException(expectedException);
		RuntimeException actualException = ExceptionUtils.asRuntimeException(invocationTargetException);
		assertSame(RuntimeException.class, actualException.getClass());
		assertSame(expectedException, actualException.getCause());
	}
	
	@Test
	public void exceptionUtilsNotInstantiable() throws Exception {
		try {
			Whitebox.invokeConstructor(ExceptionUtils.class);
			fail();
		} catch (UnsupportedOperationException uoe) {
			assertEquals("Not instantiable", uoe.getMessage());
		}
	}
}
