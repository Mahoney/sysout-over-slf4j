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

public enum SLF4JSystemOutput {

	OUT(SystemOutput.OUT), ERR(SystemOutput.ERR);
	
	private final SystemOutput systemOutput;
	
	private SLF4JSystemOutput(final SystemOutput systemOutput) {
		this.systemOutput = systemOutput;
	}
	
	public boolean isSLF4JPrintStream() {
		return systemOutput.get() instanceof SLF4JPrintStream;
	}
	
	public void restoreOriginalPrintStream() {
		final Lock writeLock = systemOutput.getLock().writeLock();
		writeLock.lock();
		try {
			if (isSLF4JPrintStream()) {
				systemOutput.set(getSLF4JPrintStream().getOriginalPrintStream());
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
			if (isSLF4JPrintStream()) {
				result = getSLF4JPrintStream().getOriginalPrintStream();
			} else {
				result = systemOutput.get();
			}
			return result;
		} finally {
			readLock.unlock();
		}
	}
	
	private SLF4JPrintStream getSLF4JPrintStream() {
		return (SLF4JPrintStream) systemOutput.get();
	}
	
	public void deregisterLoggerAppender() {
		final Lock readLock = systemOutput.getLock().readLock();
		readLock.lock();
		try {
			if (isSLF4JPrintStream()) {
				getSLF4JPrintStream().deregisterLoggerAppender();
			}
		} finally {
			readLock.unlock();
		}
	}

	public void registerLoggerAppender(final LoggerAppender loggerAppender) {
		final Lock writeLock = systemOutput.getLock().writeLock();
		writeLock.lock();
		try {
			makeSLF4JPrintStream();
			getSLF4JPrintStream().registerLoggerAppender(loggerAppender);
		} finally {
			writeLock.unlock();
		}
	}
	
	private void makeSLF4JPrintStream() {
		if (!isSLF4JPrintStream()) {
			systemOutput.set(buildSLF4JPrintStream());
		}
	}
	
	private SLF4JPrintStream buildSLF4JPrintStream() {
		final LoggerAppenderStore loggerAppenderStore = new LoggerAppenderStore();
		final PrintStream originalPrintStream = systemOutput.get();
		final SLF4JPrintStreamDelegate delegate = new SLF4JPrintStreamDelegate(originalPrintStream, loggerAppenderStore);
		return new SLF4JPrintStream(originalPrintStream, delegate);
	}
}
