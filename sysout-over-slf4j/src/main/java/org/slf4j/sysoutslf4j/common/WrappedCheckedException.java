package org.slf4j.sysoutslf4j.common;

public class WrappedCheckedException extends RuntimeException {

	private static final long serialVersionUID = 1;

	public WrappedCheckedException(final Throwable cause) {
		super(cause);
	}
}
