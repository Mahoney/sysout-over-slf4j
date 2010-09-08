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

package org.slf4j.sysoutslf4j.system;

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
		boolean isStackTrace = false;
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String className = stackTraceElement.getClassName();
			if (className.equals(Throwable.class.getName())) {
				isStackTrace = true;
			} else if (outsideThisLibrary(className, libraryPackageName)) {
				className = getOuterClassName(className);
				return new CallOrigin(isStackTrace, className);
			}
		}
		throw new IllegalStateException("Nothing in the stack originated from outside package name " + libraryPackageName);
	}

	private static boolean outsideThisLibrary(final String className, final String libraryPackageName) {
		return !className.equals(Thread.class.getName()) && !className.startsWith(libraryPackageName); //NOPMD
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
