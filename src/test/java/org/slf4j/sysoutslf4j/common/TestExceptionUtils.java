package org.slf4j.sysoutslf4j.common;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ExceptionUtils.class})
public class TestExceptionUtils extends SysOutOverSLF4JTestCase {

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
		shouldThrow(expectedError, new Callable<Void>() {
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(expectedError);
				return null;
			}
		});
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
	public void asRuntimeExceptionResetsInterruptedStatusAndReturnsPassedInInterruptedExceptionAsCauseOfWrappedCheckedException() {
		InterruptedException expectedException = new InterruptedException();
		RuntimeException actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertTrue(actualException instanceof WrappedCheckedException);
		assertTrue(Thread.interrupted());
		assertSame(expectedException, actualException.getCause());
	}
	
	@Test
	public void asRuntimeExceptionResetsInterruptedStatusAndReturnsPassedInInterruptedIOExceptionAsCauseOfWrappedCheckedException() {
		InterruptedIOException expectedException = new InterruptedIOException();
		RuntimeException actualException = ExceptionUtils.asRuntimeException(expectedException);
		assertTrue(actualException instanceof WrappedCheckedException);
		assertTrue(Thread.interrupted());
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
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(ExceptionUtils.class);
	}
	
	@Test
	public void doUncheckedReturnsResultOfWork() {
		final Object expected = new Object();
		Object result = ExceptionUtils.doUnchecked(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return expected;
			}
		});
		assertSame(expected, result);
	}
	
	@Test
	public void doUncheckedThrowsCheckedExceptionAsRuntimeException() throws Throwable {
		final Exception checkedException = new Exception();
		final WrappedCheckedException uncheckedException = new WrappedCheckedException(checkedException);
		mockStaticPartial(ExceptionUtils.class, "asRuntimeException");
		expect(ExceptionUtils.asRuntimeException(checkedException)).andReturn(uncheckedException);
		replayAll();
		
		shouldThrow(uncheckedException, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ExceptionUtils.doUnchecked(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						throw checkedException;
					}
				});
				return null;
			}
		});
	}
}
