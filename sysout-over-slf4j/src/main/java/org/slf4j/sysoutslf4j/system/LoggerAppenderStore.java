package org.slf4j.sysoutslf4j.system;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.sysoutslf4j.common.LoggerAppender;

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
