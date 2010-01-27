package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.InvocationTargetException;

public final class ExceptionUtils {

	public static RuntimeException asRuntimeException(final Throwable throwable) {
		final RuntimeException result;
		if (throwable == null) {
			throw new IllegalArgumentException("Throwable argument cannot be null");
		} else if (throwable instanceof Error) {
			throw (Error) throwable;
		} else if (throwable instanceof RuntimeException) {
			result = (RuntimeException) throwable;
		} else if (throwable instanceof InvocationTargetException) {
			result = asRuntimeException(throwable.getCause());
		} else {
			result = new WrappedCheckedException(throwable);
		}
		return result;
	}
	
	private ExceptionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
