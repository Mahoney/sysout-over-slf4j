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

import java.util.Arrays;

final class CallOrigin {

	private final boolean printingStackTrace;
	private final String className;

	private CallOrigin(final boolean isStacktrace, final String className) {
		this.printingStackTrace = isStacktrace;
		this.className = className;
	}
	
	boolean isPrintingStackTrace() {
		return printingStackTrace;
	}
	
	String getClassName() {
		return className;
	}

	static CallOrigin getCallOrigin(final StackTraceElement[] stackTraceElements, final String libraryPackageName) {
		for (int i = stackTraceElements.length - 1; i >= 0; i--) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
			String className = stackTraceElement.getClassName();
            if (className.startsWith(libraryPackageName)) {
                return new CallOrigin(false, getCallingClassName(stackTraceElements, i, libraryPackageName));
            } else if (callToPrintStackTraceOnThrowable(className, stackTraceElement.getMethodName())) {
                return new CallOrigin(true, getCallingClassName(stackTraceElements, i, libraryPackageName));
            }
		}
		throw new IllegalStateException(
                "Nothing in the stack originated from inside package name "
                        +libraryPackageName+"; this should be impossible. "+
                        "Stack: "+Arrays.toString(stackTraceElements));
	}

    private static String getCallingClassName(StackTraceElement[] stackTraceElements, int i, String libraryPackageName) {
        int callingStackIndex = i + 1;
        if (stackTraceElements.length > callingStackIndex) {
            StackTraceElement stackTraceElement = stackTraceElements[i + 1];
            return getOuterClassName(stackTraceElement.getClassName());
        } else {
            throw new IllegalStateException(
                    "Nothing in the stack originated from outside package name "
                            +libraryPackageName+"; this should be impossible. "+
                            "Stack: "+Arrays.toString(stackTraceElements));
        }
    }

    private static boolean callToPrintStackTraceOnThrowable(String className, String methodName) {
        return methodName.equals("printStackTrace") &&
                (className.equals(Throwable.class.getName()) || classExtendsThrowable(className));
    }

    private static boolean classExtendsThrowable(String className) {
        try {
            return Throwable.class.isAssignableFrom(Class.forName(className));
        } catch (Exception e) {
            return false;
        }
    }

	private static String getOuterClassName(final String className) {
		final int startOfInnerClassName = className.indexOf('$');
		final String outerClassName;
		if (startOfInnerClassName == -1) {
			outerClassName = className;
		} else {
			outerClassName = className.substring(0, startOfInnerClassName);
		}
		return outerClassName;
	}
}
