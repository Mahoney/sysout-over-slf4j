package org.slf4j.sysoutslf4j.common;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

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
			if (throwable instanceof InterruptedException || throwable instanceof InterruptedIOException) {
				Thread.currentThread().interrupt();
			}
			result = new WrappedCheckedException(throwable);
		}
		return result;
	}

	public static <E> E doUnchecked(Callable<E> work) {
		try {
			return work.call();
		} catch (Throwable t) {
			throw asRuntimeException(t);
		}
	}

	private ExceptionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
