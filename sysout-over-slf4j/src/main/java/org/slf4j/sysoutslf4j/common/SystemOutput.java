package org.slf4j.sysoutslf4j.common;

import java.io.PrintStream;

public enum SystemOutput {

	OUT("System.out") {
		public PrintStream get() {
			return System.out;
		}

		public void set(final PrintStream newPrintStream) {
			System.setOut(newPrintStream);
		}
	}, ERR("System.err") {
		public PrintStream get() {
			return System.err;
		}

		public void set(final PrintStream newPrintStream) {
			System.setErr(newPrintStream);
		}
	};

	public abstract PrintStream get();
	public abstract void set(PrintStream newPrintStream);

	private final String friendlyName;

	private SystemOutput(final String name) {
		this.friendlyName = name;
	}

	@Override
	public String toString() {
		return friendlyName;
	}
}
