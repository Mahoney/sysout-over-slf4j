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
import java.util.concurrent.locks.Lock;

public enum PerContextSystemOutput {

	OUT(SystemOutput.OUT), ERR(SystemOutput.ERR);
	
	private final SystemOutput systemOutput;
	
	private PerContextSystemOutput(final SystemOutput systemOutput) {
		this.systemOutput = systemOutput;
	}
	
	public boolean isPerContextPrintStream() {
		return systemOutput.get() instanceof PerContextPrintStream;
	}
	
	public void restoreOriginalPrintStream() {
		final Lock writeLock = systemOutput.getLock().writeLock();
		writeLock.lock();
		try {
			if (isPerContextPrintStream()) {
				systemOutput.set(getPerContextPrintStream().getOriginalPrintStream());
			}
		} finally {
			writeLock.unlock();
		}
	}
	
	public PrintStream getOriginalPrintStream() {
		final PrintStream result;
		final Lock readLock = systemOutput.getLock().readLock();
		readLock.lock();
		try {
			if (isPerContextPrintStream()) {
				result = getPerContextPrintStream().getOriginalPrintStream();
			} else {
				result = systemOutput.get();
			}
			return result;
		} finally {
			readLock.unlock();
		}
	}
	
	private PerContextPrintStream getPerContextPrintStream() {
		return (PerContextPrintStream) systemOutput.get();
	}
	
	public void deregisterSimplePrintStream() {
		final Lock readLock = systemOutput.getLock().readLock();
		readLock.lock();
		try {
			if (isPerContextPrintStream()) {
				getPerContextPrintStream().deregisterSimplePrintStream();
			}
		} finally {
			readLock.unlock();
		}
	}

	public void registerSimplePrintStream(final SimplePrintStream simplePrintStream) {
		final Lock writeLock = systemOutput.getLock().writeLock();
		writeLock.lock();
		try {
			makePerContextPrintStream();
			getPerContextPrintStream().registerSimplePrintStream(simplePrintStream);
		} finally {
			writeLock.unlock();
		}
	}
	
	private void makePerContextPrintStream() {
		if (!isPerContextPrintStream()) {
			systemOutput.set(buildPerContextPrintStream());
		}
	}
	
	private PerContextPrintStream buildPerContextPrintStream() {
		final SimplePrintStreamStore simplePrintStreamStore = new SimplePrintStreamStore();
		final PrintStream originalPrintStream = systemOutput.get();
		final PerContextPrintStreamDelegate delegate = new PerContextPrintStreamDelegate(originalPrintStream, simplePrintStreamStore);
		return new PerContextPrintStream(originalPrintStream, delegate);
	}
}
