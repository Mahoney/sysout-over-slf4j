package org.slf4j.sysoutslf4j.context;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggingSystemRegister {

	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);
	private final Set<String> loggingSystemNameFragments = new CopyOnWriteArraySet<String>();
	
	void registerLoggingSystem(final String packageName) {
		loggingSystemNameFragments.add(packageName);
		LOG.info("Package {} registered; all classes within it or subpackages of it will "
					+ "be allowed to print to System.out and System.err", packageName);
	}

	void unregisterLoggingSystem(final String packageName) {
		if (loggingSystemNameFragments.remove(packageName)) {
			LOG.info("Package {} unregistered; all classes within it or subpackages of it will "
					+ "have System.out and System.err redirected to SLF4J", packageName);
		}
	}

	boolean isInLoggingSystem(final String className) {
		boolean isInLoggingSystem = false;
		for (String packageName : loggingSystemNameFragments) {
			if (className.startsWith(packageName)) {
				isInLoggingSystem = true;
				break;
			}
		}
		return isInLoggingSystem;
	}
	
	LoggingSystemRegister() {
		super();
	}
}
