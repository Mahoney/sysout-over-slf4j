package org.slf4j.sysoutslf4j.system;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;
import org.slf4j.sysoutslf4j.common.SystemOutput;

public final class SLF4JPrintStreamConfigurator {
	
	public void replaceSystemOutputsWithSLF4JPrintStreams() {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			replaceSystemOutputWithSLF4JPrintStream(systemOutput);
		}
	}

	private static void replaceSystemOutputWithSLF4JPrintStream(final SystemOutput systemOutput) {
		final SLF4JPrintStreamImpl slf4jPrintStream = buildSLF4JPrintStream(systemOutput.get());
		systemOutput.set(slf4jPrintStream);
	}

	private static SLF4JPrintStreamImpl buildSLF4JPrintStream(final PrintStream originalPrintStream) {
		final LoggerAppenderStore loggerAppenderStore = new LoggerAppenderStore();
		final SLF4JPrintStreamDelegate delegate = new SLF4JPrintStreamDelegate(originalPrintStream, loggerAppenderStore);
		return new SLF4JPrintStreamImpl(originalPrintStream, delegate);
	}

	public void restoreOriginalSystemOutputs() {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			restoreSystemOutput(systemOutput);
		}
	}

	private static void restoreSystemOutput(final SystemOutput systemOutput) {
		final SLF4JPrintStream slf4jPrintStream = (SLF4JPrintStream) systemOutput.get();
		systemOutput.set(slf4jPrintStream.getOriginalPrintStream());
	}
}
