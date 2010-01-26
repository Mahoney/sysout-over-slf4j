package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestCallOrigin {

	@Test
	public void testGetCallOriginThrowsNullPointerIfCalledWithNoStackTrace() {
		try {
			CallOrigin.getCallOrigin(null, "com");
			fail();
		} catch (NullPointerException npe) {
			// expected
		}
	}
	
	@Test
	public void testGetCallOriginThrowsNullPointerIfCalledWithNoLibraryPackageName() {
		try {
			StackTraceElement[] stackTraceElements = { buildStackTraceElement("org.a.ClassName") };
			CallOrigin.getCallOrigin(stackTraceElements, null);
			fail();
		} catch (NullPointerException npe) {
			// expected
		}
	}
	
	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfCalledWithEmptyStackTrace() {
		try {
			StackTraceElement[] stackTraceElements = { };
			CallOrigin.getCallOrigin(stackTraceElements, "");
			fail();
		} catch (IllegalStateException ise) {
			// expected
		}
	}
	
	@Test
	public void testGetCallOriginThrowsIllegalStateExceptionIfAllStackTraceElementsAreInTheLibrary() {
		try {
			StackTraceElement[] stackTraceElements = {
					buildStackTraceElement("org.a.1"),
					buildStackTraceElement("org.a.2")
			};
			CallOrigin.getCallOrigin(stackTraceElements, "org.a");
			fail();
		} catch (IllegalStateException ise) {
			// expected
		}
	}
	
	@Test
	public void testGetCallOriginReturnsFirstClassName() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("org.a.ClassName"),
				buildStackTraceElement("org.b.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}
	
	@Test
	public void testGetCallOriginIsNotStackTraceIfThrowableNotFirstElement() {
		StackTraceElement[] stackTraceElements = { buildStackTraceElement("org.a.ClassName") };
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertFalse(callOrigin.isPrintingStackTrace());
	}
	
	@Test
	public void testGetCallOriginIsStackTraceIfThrowableIsFirstElement() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Throwable"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertTrue(callOrigin.isPrintingStackTrace());
	}
	
	@Test
	public void testGetCallOriginReturnsFirstClassNameOtherThanThrowable() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Throwable"),
				buildStackTraceElement("org.a.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}
	
	@Test
	public void testGetCallOriginReturnsFirstClassNameOtherThanThread() {
		StackTraceElement[] stackTraceElements = {
				buildStackTraceElement("java.lang.Thread"),
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
				buildStackTraceElement("java.lang.Throwable"),
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
				buildStackTraceElement("java.lang.Throwable"),
				buildStackTraceElement("org.a.ClassName"),
				buildStackTraceElement("org.b.ClassName")
		};
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}
	
	@Test
	public void testGetCallOriginReturnsInnerClassesAsTheOuterClass() {
		StackTraceElement[] stackTraceElements = { buildStackTraceElement("org.a.ClassName$InnerClass") };
		CallOrigin callOrigin = CallOrigin.getCallOrigin(stackTraceElements, "com");
		assertEquals("org.a.ClassName", callOrigin.getClassName());
	}

	private StackTraceElement buildStackTraceElement(String declaringClass) {
		return new StackTraceElement(declaringClass, "", "", 0);
	}
}
