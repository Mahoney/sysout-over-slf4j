/*
 * Copyright (c) 2009-2012 Robert Elliot
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

package uk.org.lidalia.sysoutslf4j.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static uk.org.lidalia.test.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CallOrigin.class, LoggingSystemRegister.class})
public class CallOriginTests {

	private final LoggingSystemRegister loggingSystemRegister = mock(LoggingSystemRegister.class);
	@Test
	public void getCallOriginThrowsIllegalStateExceptionIfNoPerContextPrintStreamStackEntry() throws Throwable {
		expectGetStackTraceToReturn(
				stackTraceElement("org.a.1"),
				stackTraceElement("org.a.2")
		);

		IllegalStateException exception = shouldThrow(IllegalStateException.class, new Runnable() {
			public void run() {
				CallOrigin.getCallOrigin(loggingSystemRegister);
			}
		});
		assertEquals("Must be called from down stack of uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream", exception.getMessage());
	}

	@Test
	public void getCallOriginReturnsFirstClassName() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement("org.a.ClassName"),
				stackTraceElement("org.b.ClassName")
		);

		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void getCallOriginIsNotStackTraceIfThrowableNotFirstElement() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement("org.a.ClassName"));

		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertFalse(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void getCallOriginIsStackTraceIfThrowableIsInStackBeforePerContextPrintStreamElement() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement("java.lang.Throwable", "printStackTrace"),
				stackTraceElement("org.a.ClassName")
		);

		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertTrue(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void getCallOriginReturnsFirstClassNameOtherThanThrowable() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement("some.other.ClassName"),
				stackTraceElement(Throwable.class, "printStackTrace"),
				stackTraceElement("org.a.ClassName")
		);

		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void getCallOriginReturnsInnerClassesAsTheOuterClass() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement("org.a.ClassName$InnerClass"),
				stackTraceElement("org.b.ClassName")
		);
		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void getCallOriginIsInLoggingSystemIfLoggingSystemRegisterSaysItIs() {
		expectGetStackTraceToReturn(
				stackTraceElement(PerContextPrintStream.class),
				stackTraceElement(Throwable.class, "printStackTrace"),
				stackTraceElement("class.in.logging.system")
		);
		when(loggingSystemRegister.isInLoggingSystem("class.in.logging.system")).thenReturn(true);

		CallOrigin callOrigin = CallOrigin.getCallOrigin(loggingSystemRegister);
		assertTrue(callOrigin.isInLoggingSystem());
	}

	private void expectGetStackTraceToReturn(StackTraceElement... stackTraceElements) {
		Thread mockThread = mock(Thread.class);
		when(mockThread.getStackTrace()).thenReturn(stackTraceElements);
		mockStatic(Thread.class);
		when(Thread.currentThread()).thenReturn(mockThread);
		when(Thread.class.getName()).thenReturn("java.lang.Thread");
	}

	private StackTraceElement stackTraceElement(Class<?> declaringClass, String methodName) {
		return stackTraceElement(declaringClass.getName(), methodName);
	}

	private StackTraceElement stackTraceElement(String declaringClass, String methodName) {
		return new StackTraceElement(declaringClass, methodName, "", 0);
	}

	private StackTraceElement stackTraceElement(Class<?> declaringClass) {
		return stackTraceElement(declaringClass, "");
	}

	private StackTraceElement stackTraceElement(String declaringClass) {
		return stackTraceElement(declaringClass, "");
	}
}
