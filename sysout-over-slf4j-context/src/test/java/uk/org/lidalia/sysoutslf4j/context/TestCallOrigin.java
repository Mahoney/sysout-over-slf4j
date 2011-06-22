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

package uk.org.lidalia.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static uk.org.lidalia.test.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.context.CallOrigin;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CallOrigin.class, Thread.class })
public class TestCallOrigin {

	@Test
	public void testGetCallOriginThrowsNullPointerIfCalledWithNoLibraryPackageName() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				CallOrigin.getCallOrigin(null);
				return null;
			}
		});
	}

	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfAllStackTraceElementsAreInTheLibrary() throws Throwable {
		expectGetStackTraceToReturn(
				stackTraceElement("org.a.1"),
				stackTraceElement("org.a.2")
		);
		replayAll();

		IllegalStateException exception = shouldThrow(IllegalStateException.class, new Callable<Void>() {
			public Void call() throws Exception {
				CallOrigin.getCallOrigin("org.a");
				return null;
			}
		});
		assertEquals("Nothing in the stack originated from outside package name org.a", exception.getMessage());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassName() {
		expectGetStackTraceToReturn(
				stackTraceElement("org.a.ClassName"),
				stackTraceElement("org.b.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginIsNotStackTraceIfThrowableNotFirstElement() {
		expectGetStackTraceToReturn(stackTraceElement("org.a.ClassName"));
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertFalse(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginIsStackTraceIfThrowableIsFirstElement() {
		expectGetStackTraceToReturn(
				stackTraceElement("java.lang.Throwable"),
				stackTraceElement("org.a.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertTrue(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOtherThanThrowable() {
		expectGetStackTraceToReturn(
				stackTraceElement("java.lang.Throwable"),
				stackTraceElement("org.a.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOtherThanThread() {
		expectGetStackTraceToReturn(
				stackTraceElement("java.lang.Thread"),
				stackTraceElement("org.a.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOutsideTheLibrary() {
		expectGetStackTraceToReturn(
				stackTraceElement("com.something"),
				stackTraceElement("com.somethingelse"),
				stackTraceElement("org.a.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginIsStackTraceIfThrowableIsFirstElementOutsideTheLibrary() {
		expectGetStackTraceToReturn(
				stackTraceElement("com.something"),
				stackTraceElement("com.somethingelse"),
				stackTraceElement("java.lang.Throwable"),
				stackTraceElement("org.a.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertTrue(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOutsideTheLibraryOtherThanThreadOrThrowable() {
		expectGetStackTraceToReturn(
				stackTraceElement("java.lang.Thread"),
				stackTraceElement("com.something"),
				stackTraceElement("com.somethingelse"),
				stackTraceElement("java.lang.Throwable"),
				stackTraceElement("org.a.ClassName"),
				stackTraceElement("org.b.ClassName")
		);
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginReturnsInnerClassesAsTheOuterClass() {
		expectGetStackTraceToReturn(new StackTraceElement[] { stackTraceElement("org.a.ClassName$InnerClass") });
		replayAll();
		
		CallOrigin callOrigin = CallOrigin.getCallOrigin("com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	private void expectGetStackTraceToReturn(StackTraceElement... stackTraceElements) {
		Thread mockThread = createNiceMock(Thread.class);
		expect(mockThread.getStackTrace()).andStubReturn(stackTraceElements);
		mockStatic(Thread.class);
		expect(Thread.currentThread()).andStubReturn(mockThread);
		expect(Thread.class.getName()).andStubReturn("java.lang.Thread");
	}

	private StackTraceElement stackTraceElement(String declaringClass) {
		return new StackTraceElement(declaringClass, "", "", 0);
	}
}
