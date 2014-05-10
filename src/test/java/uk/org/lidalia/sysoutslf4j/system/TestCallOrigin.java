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

package uk.org.lidalia.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.org.lidalia.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.system.CallOrigin;

public class TestCallOrigin {

	@Test
	public void testGetCallOriginThrowsNullPointerIfCalledWithNoStackTrace() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				CallOrigin.getCallOrigin(null, "com");
				return null;
			}
		});
	}

	@Test
	public void testGetCallOriginThrowsNullPointerIfCalledWithNoLibraryPackageName() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StackTraceElement[] stackTraceElements = { buildStackTraceElement("org.a.ClassName") };
				CallOrigin.getCallOrigin(stackTraceElements, null);
				return null;
			}
		});
	}

	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfCalledWithEmptyStackTrace() throws Throwable {
		shouldThrow(IllegalStateException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StackTraceElement[] stackTraceElements = { };
				CallOrigin.getCallOrigin(stackTraceElements, "");
				return null;
			}
		});
	}

	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfAllStackTraceElementsAreInTheLibrary() throws Throwable {
		shouldThrow(IllegalStateException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StackTraceElement[] stackTraceElements = {
						buildStackTraceElement("org.a.1"),
						buildStackTraceElement("org.a.2")
				};
				CallOrigin.getCallOrigin(stackTraceElements, "org.a");
				return null;
			}
		});
	}

	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfNoStackTraceElementsAreInTheLibrary() throws Throwable {
        shouldThrow(IllegalStateException.class, new Callable<Void>() {
            public Void call() throws Exception {
                StackTraceElement[] stackTraceElements = {
                        buildStackTraceElement("org.a.ClassName"),
                        buildStackTraceElement("org.b.ClassName")
                };
                CallOrigin.getCallOrigin(stackTraceElements, "com");
                return null;
            }
        });
	}

	@Test
	public void testGetCallOriginIsNotStackTraceIfThrowableNotFirstElement() {
		StackTraceElement[] stackTraceElements = {
                buildStackTraceElement("com.inlibrary"),
                buildStackTraceElement("org.a.ClassName")
        };
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertFalse(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginIsStackTraceIfThrowableIsFirstElement() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertTrue(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOtherThanThrowable() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOutsideTheLibrary() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("com.something"),
				buildStackTraceElement("com.somethingelse"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	@Test
	public void testGetCallOriginIsStackTraceIfThrowableIsFirstElementOutsideTheLibrary() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("com.something"),
				buildStackTraceElement("com.somethingelse"),
				buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertTrue(callOrigin.isPrintingStackTrace());
	}

	@Test
	public void testGetCallOriginReturnsFirstClassNameOutsideTheLibraryOtherThanThreadOrThrowable() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Thread"),
				buildStackTraceElement("com.something"),
				buildStackTraceElement("com.somethingelse"),
				buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
				buildStackTraceElement("org.a.ClassName"),
				buildStackTraceElement("org.b.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

    @Test
    public void testGetCallOriginRecognisesPrintStackTraceRegardlessOfInTheMiddleCalls() {
        StackTraceElement[] stackTraceElements = {
                buildStackTraceElement("java.lang.Thread"),
                buildStackTraceElement("com.something"),
                buildStackTraceElement("com.something.else"),
                buildStackTraceElement("com.sun.java.lang.Throwable", "printStackTrace"),
                buildStackTraceElement("java.lang.Throwable$WrappedPrintStream", "println"),
                buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
                buildStackTraceElement("org.a.ClassName"),
                buildStackTraceElement("org.b.ClassName")
        };
        CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com.something");
        assertEquals("org.a.ClassName", callOrigin.getClassName());
        assertTrue(callOrigin.isPrintingStackTrace());
    }

    @Test
    public void testGetCallOriginRecognisesPrintStackTraceOnSubtypeOfThrowable() {
        StackTraceElement[] stackTraceElements = {
                buildStackTraceElement("java.lang.Thread"),
                buildStackTraceElement("com.something"),
                buildStackTraceElement("com.something.else"),
                buildStackTraceElement("com.sun.java.lang.Throwable", "printStackTrace"),
                buildStackTraceElement("java.lang.Throwable$WrappedPrintStream", "println"),
                buildStackTraceElement("java.lang.Throwable", "printStackTrace"),
                buildStackTraceElement("org.apache.commons.lang.exception.NestableError", "printStackTrace"),
                buildStackTraceElement("org.a.ClassName"),
                buildStackTraceElement("org.b.ClassName")
        };
        CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com.something");
        assertEquals("org.a.ClassName", callOrigin.getClassName());
        assertTrue(callOrigin.isPrintingStackTrace());
    }

	@Test
	public void testGetCallOriginReturnsInnerClassesAsTheOuterClass() {
		StackTraceElement[] stackTraceElements = {
                buildStackTraceElement("com.inlibrary"),
                buildStackTraceElement("org.a.ClassName$InnerClass")
        };
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	private StackTraceElement buildStackTraceElement(String declaringClass) {
		return buildStackTraceElement(declaringClass, "");
	}

    private StackTraceElement buildStackTraceElement(String declaringClass, String methodName) {
        return new StackTraceElement(declaringClass, methodName, "", 0);
    }
}
