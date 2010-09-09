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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import uk.org.lidalia.sysoutslf4j.common.LoggerAppender;

class LoggerAppenderStore {

	private final Map<ClassLoader, WeakReference<LoggerAppender>> loggerAppenderMap =
		new WeakHashMap<ClassLoader, WeakReference<LoggerAppender>>();
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	LoggerAppender get() {
		readLock.lock();
		try {
			return get(contextClassLoader());
		} finally {
			readLock.unlock();
		}
	}

	private LoggerAppender get(final ClassLoader classLoader) {
		final WeakReference<LoggerAppender> loggerAppenderReference = loggerAppenderMap.get(classLoader);
		final LoggerAppender result;
		if (loggerAppenderReference == null) {
			if (classLoader == null) {
				result = null;
			} else {
				result = get(classLoader.getParent());
			}
		} else {
			result = loggerAppenderReference.get();
		}
		return result;
	}

	void put(final LoggerAppender loggerAppender) {
		writeLock.lock();
		try {
			loggerAppenderMap.put(contextClassLoader(), new WeakReference<LoggerAppender>(loggerAppender));
		} finally {
			writeLock.unlock();
		}
	}

	void remove() {
		writeLock.lock();
		try {
			loggerAppenderMap.remove(contextClassLoader());
		} finally {
			writeLock.unlock();
		}
	}
	
	private ClassLoader contextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
