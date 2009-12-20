package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.InvocationTargetException;

public class ExceptionUtils {

	public static RuntimeException asRuntimeException(Throwable t) {
		if (t == null) {
			throw new IllegalArgumentException("Throwable argument cannot be null");
		} else if (t instanceof Error) {
			throw (Error) t;
		} else if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else if (t instanceof InvocationTargetException) {
			return asRuntimeException(t.getCause());
		} else {
			return new RuntimeException("Wrapping checked exception " + t.toString(), t);
		}
	}
	
	private ExceptionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
