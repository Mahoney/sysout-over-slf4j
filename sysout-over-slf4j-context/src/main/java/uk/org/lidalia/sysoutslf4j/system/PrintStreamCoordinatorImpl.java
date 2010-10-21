/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.system;

import java.io.PrintStream;

import uk.org.lidalia.sysoutslf4j.common.PrintStreamCoordinator;
import uk.org.lidalia.sysoutslf4j.common.SLF4JPrintStream;
import uk.org.lidalia.sysoutslf4j.common.SystemOutput;

public final class PrintStreamCoordinatorImpl implements PrintStreamCoordinator {
	
	@Override
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

	@Override
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
