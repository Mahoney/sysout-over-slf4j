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

package uk.org.lidalia.testutils;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static uk.org.lidalia.testutils.Assert.assertNotInstantiable;
import static uk.org.lidalia.testutils.Assert.shouldThrow;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.testutils.ExceptionUtils;
import uk.org.lidalia.testutils.WrappedCheckedException;

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
	public void asRuntimeExceptionThrowsIllegalArgumentExceptionWhenInterruptedExceptionPassedIn() throws Throwable {
		final InterruptedException expectedException = new InterruptedException();
		IllegalArgumentException actualException = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(expectedException);
				return null;
			}
		});
		
		assertTrue(actualException instanceof IllegalArgumentException);
		assertSame(expectedException, actualException.getCause());
		assertEquals("An interrupted exception needs to be handled to end the thread, or the interrupted status needs to be " +
					"restored, or the exception needs to be propagated explicitly - it should not be used as an argument to " +
					"this method", actualException.getMessage());
	}
	
	@Test
	public void asRuntimeExceptionThrowsIllegalArgumentExceptionWhenInterruptedIOExceptionPassedIn() throws Throwable {
		final InterruptedIOException expectedException = new InterruptedIOException();
		final IllegalArgumentException actualException = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ExceptionUtils.asRuntimeException(expectedException);
				return null;
			}
		});
		
		assertTrue(actualException instanceof IllegalArgumentException);
		assertSame(expectedException, actualException.getCause());
		assertEquals("An interrupted exception needs to be handled to end the thread, or the interrupted status needs to be " +
					"restored, or the exception needs to be propagated explicitly - it should not be used as an argument to " +
					"this method", actualException.getMessage());
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
