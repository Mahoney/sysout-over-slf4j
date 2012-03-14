/* 
 * Copyright (c) 2009-2012 Robert Elliot
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum SystemOutput {

	OUT("System.out") {
		public PrintStream get() {
			final Lock readLock = getLock().readLock();
			readLock.lock();
			try {
				return System.out;
			} finally {
				readLock.unlock();	
			}
		}

		public void set(final PrintStream newPrintStream) {
			final Lock writeLock = getLock().writeLock();
			writeLock.lock();
			try {
				System.setOut(newPrintStream);
			} finally {
				writeLock.unlock();	
			}
		}
	}, ERR("System.err") {
		public PrintStream get() {
			final Lock readLock = getLock().readLock();
			readLock.lock();
			try {
				return System.err;
			} finally {
				readLock.unlock();	
			}
		}

		public void set(final PrintStream newPrintStream) {
			final Lock writeLock = getLock().writeLock();
			writeLock.lock();
			try {
				System.setErr(newPrintStream);
			} finally {
				writeLock.unlock();	
			}
		}
	};

	public abstract PrintStream get();
	public abstract void set(PrintStream newPrintStream);

	private final String name;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private SystemOutput(final String name) {
		this.name = name;
	}
	
	public ReadWriteLock getLock() {
		return lock;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public static SystemOutput findByName(String name) {
		for (SystemOutput systemOutput : SystemOutput.values()) {
			if (systemOutput.name.equalsIgnoreCase(name)) {
				return systemOutput;
			}
		}
		throw new IllegalArgumentException("No system output [" + name + "]; valid values are " + names());
	}

    private static String names() {
        StringBuilder builder = new StringBuilder("[");
        SystemOutput[] values = values();
        for (int i = 0; i < values.length; i++) {
            builder.append(values[i].getName());
            if (i < values.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
