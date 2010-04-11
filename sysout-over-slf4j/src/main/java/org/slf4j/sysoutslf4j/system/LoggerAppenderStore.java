package org.slf4j.sysoutslf4j.system;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.sysoutslf4j.common.LoggerAppender;

class LoggerAppenderStore {
	
	private static final Object NULL_KEY = new Object();

	private final Map<Object, LoggerAppender> loggerAppenderMap =
		new ConcurrentHashMap<Object, LoggerAppender>();
	
	LoggerAppender get() {
		return get(contextClassLoader());
	}

	private LoggerAppender get(final ClassLoader classLoader) {
		Object classLoaderAsKey = getClassLoaderAsKey(classLoader);
		final LoggerAppender possible = loggerAppenderMap.get(classLoaderAsKey);
		
		final LoggerAppender result;
		if (possible == null) {
			if (classLoader == null) {
				result = null;
			} else {
				result = get(classLoader.getParent());
			}
		} else {
			result = possible;
		}
		return result;
	}

	private Object getClassLoaderAsKey(final ClassLoader classLoader) {
		return classLoader == null ? NULL_KEY : classLoader;
	}

	void put(final LoggerAppender loggerAppender) {
		Object classLoaderAsKey = getClassLoaderAsKey(contextClassLoader());
		loggerAppenderMap.put(classLoaderAsKey, loggerAppender);
	}

	private ClassLoader contextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	void remove() {
		loggerAppenderMap.remove(contextClassLoader());
	}
}
