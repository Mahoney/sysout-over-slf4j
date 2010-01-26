package org.slf4j.sysoutslf4j.system;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.sysoutslf4j.common.ClassLoaderUtils;
import org.slf4j.sysoutslf4j.common.LoggerAppender;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.context.ReferenceHolder;

class LoggerAppenderStore {

	private final Map<ClassLoader, WeakReference<LoggerAppender>> loggerAppenderMap =
		new WeakHashMap<ClassLoader, WeakReference<LoggerAppender>>();
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	LoggerAppender get() {
		LoggerAppender loggerAppender = null;
		readLock.lock();
		try {
			loggerAppender = get(contextClassLoader());
		} finally {
			readLock.unlock();
		}
		return loggerAppender;
	}

	private LoggerAppender get(final ClassLoader classLoader) {
		final WeakReference<LoggerAppender> loggerAppenderReference = loggerAppenderMap.get(classLoader);
		final LoggerAppender result;
		if (loggerAppenderReference != null) { //NOPMD
			result = loggerAppenderReference.get();
		} else if (classLoader == null) {
			result = null;
		} else {
			result = get(classLoader.getParent());
		}
		return result;
	}

	void set(final Object loggerAppenderObject) {
		final LoggerAppender loggerAppender = LoggerAppenderWrapper.wrap(loggerAppenderObject);
		preventLoggerAppenderFromBeingGarbageCollected(loggerAppenderObject.getClass().getClassLoader(), loggerAppender); //NOPMD

		writeLock.lock();
		try {
			loggerAppenderMap.put(contextClassLoader(), new WeakReference<LoggerAppender>(loggerAppender));
		} finally {
			writeLock.unlock();
		}
	}

	private void preventLoggerAppenderFromBeingGarbageCollected(
			final ClassLoader originatingClassLoader, final LoggerAppender loggerAppender) {
		final Class<?> referenceHolderClass = ClassLoaderUtils.loadClass(originatingClassLoader, ReferenceHolder.class);
		ReflectionUtils.invokeStaticMethod(
				"preventGarbageCollectionForLifeOfClassLoader", referenceHolderClass, Object.class, loggerAppender);
	}

	private ClassLoader contextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
