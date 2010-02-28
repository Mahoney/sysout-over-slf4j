package org.slf4j.sysoutslf4j.context;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

public enum SLF4JSystemOutput {

	OUT {
		public SLF4JPrintStream get() {
			return wrap(System.out);
		}
	}, ERR {
		public SLF4JPrintStream get() {
			return wrap(System.err);
		}
	};

	public abstract SLF4JPrintStream get();

	private static SLF4JPrintStream wrap(final PrintStream targetPrintStream) {
		final SLF4JPrintStream result;
		if (targetPrintStream instanceof SLF4JPrintStream) {
			result = (SLF4JPrintStream) targetPrintStream;
		} else {
			result = new SLF4JPrintStreamProxy(targetPrintStream);
		}
		return result;
	}

}
