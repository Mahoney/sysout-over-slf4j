package org.slf4j.sysoutslf4j.common;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.context.LogLevel;

public enum SystemOutput {

	SYSOUT("System.out", LogLevel.INFO) {
		public PrintStream get() {
			return System.out;
		}

		public void set(PrintStream newPrintStream) {
			System.setOut(newPrintStream);
		}
	}, SYSERR("System.err", LogLevel.ERROR) {
		public PrintStream get() {
			return System.err;
		}

		public void set(PrintStream newPrintStream) {
			System.setErr(newPrintStream);
		}
	};

	public abstract PrintStream get();
	public abstract void set(PrintStream newPrintStream);

	private final String friendlyName;
	private final LogLevel logLevel;

	private SystemOutput(String name, LogLevel logLevel) {
		this.friendlyName = name;
		this.logLevel = logLevel;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	@Override
	public String toString() {
		return friendlyName;
	}
}
