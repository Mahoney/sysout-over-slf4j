package org.slf4j.sysoutslf4j.system;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.common.SystemOutput;

public final class Wirer {
	
	public static void replaceSystemOutputsWithSLF4JPrintStreamsIfNecessary() {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			replaceSystemOutputWithSLF4JPrintStreamIfNecessary(systemOutput);
		}
	}

	private static void replaceSystemOutputWithSLF4JPrintStreamIfNecessary(final SystemOutput systemOutput) {
		final SLF4JPrintStream slf4jPrintStream = buildSLF4JPrintStream(systemOutput.get());
		systemOutput.set(slf4jPrintStream);
	}

	private static SLF4JPrintStream buildSLF4JPrintStream(final PrintStream originalPrintStream) {
		final LoggerAppenderStore loggerAppenderStore = new LoggerAppenderStore();
		final SLF4JPrintStreamDelegater delegater = new SLF4JPrintStreamDelegater(originalPrintStream, loggerAppenderStore);
		return new SLF4JPrintStream(originalPrintStream, delegater);
	}

	public static void restoreOriginalSystemOutputsIfNecessary() {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			restoreSystemOutput(systemOutput);
		}
	}

	private static void restoreSystemOutput(final SystemOutput systemOutput) {
		if (systemOutput.get() instanceof SLF4JPrintStream) {
			final SLF4JPrintStream slf4jPrintStream = (SLF4JPrintStream) systemOutput.get();
			systemOutput.set(slf4jPrintStream.getOriginalPrintStream());
		}
	}

	private Wirer() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
