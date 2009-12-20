package org.slf4j.sysoutslf4j.system;

final class CallOrigin {

	private final boolean isStackTrace;
	private final String className;

	private CallOrigin(boolean isStacktrace, String className) {
		this.isStackTrace = isStacktrace;
		this.className = className;
	}
	
	boolean isStackTrace() {
		return isStackTrace;
	}
	
	String getClassName() {
		return className;
	}

	static CallOrigin getCallOrigin(StackTraceElement[] stackTraceElements, String libraryPackageName) {
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

	private static boolean outsideThisLibrary(String className, String libraryPackageName) {
		return !className.equals(Thread.class.getName()) && !className.startsWith(libraryPackageName);
	}

	private static String getOuterClassName(String className) {
		int startOfInnerClassName = className.indexOf('$');
		if (startOfInnerClassName != -1) {
			return className.substring(0, startOfInnerClassName);
		}
		return className;
	}
}
