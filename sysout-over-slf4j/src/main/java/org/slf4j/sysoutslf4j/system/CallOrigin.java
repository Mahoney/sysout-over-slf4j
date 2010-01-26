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
