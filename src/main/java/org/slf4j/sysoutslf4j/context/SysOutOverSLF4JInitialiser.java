package org.slf4j.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SysOutOverSLF4JInitialiser {
	
	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4JInitialiser.class);
	
	private static final String UNKNOWN_LOGGING_SYSTEM_MESSAGE =
		"Your logging framework {} is not known - if it needs access to the standard println "
		+ "methods on the console you will need to register it by calling registerLoggingSystemPackage";
	private static final String LOGGING_SYSTEM_DOES_NOT_NEED_PRINTLN_MESSAGE =
		"Your logging framework {} should not need access to the standard println methods on "
		+ "the console, so you should not need to register a logging system package.";

	private static final String[] LOGGING_SYSTEMS_THAT_DO_NOT_ACCESS_CONSOLE =
		{ "ch.qos.logback.", "org.slf4j.impl.Log4jLoggerAdapter", "org.slf4j.impl.JDK14LoggerAdapter", "org.apache.log4j." };
	private static final String[] LOGGING_SYSTEMS_THAT_MIGHT_ACCESS_CONSOLE =
		{ "org.x4juli.", "org.grlea.log.", "org.slf4j.impl.SimpleLogger" };
	
	private final LoggingSystemRegister loggingSystemRegister;
	
	SysOutOverSLF4JInitialiser(final LoggingSystemRegister loggingSystemRegister) {
		this.loggingSystemRegister = loggingSystemRegister;
	}

	void initialise(final Logger currentLoggerImplementation) {
		if (loggingSystemKnownAndMightAccessConsoleViaPrintln(currentLoggerImplementation)) {
			registerCurrentLoggingSystemPackage(currentLoggerImplementation);
		} else if (loggingSystemDoesNotAccessConsoleViaPrintln(currentLoggerImplementation)) {
			LOG.debug(LOGGING_SYSTEM_DOES_NOT_NEED_PRINTLN_MESSAGE, currentLoggerImplementation.getClass());
		} else {
			LOG.warn(UNKNOWN_LOGGING_SYSTEM_MESSAGE, currentLoggerImplementation.getClass());
		}
	}

	private boolean loggingSystemDoesNotAccessConsoleViaPrintln(final Logger currentLoggerImplementation) {
		boolean loggingSystemDoesNotAccessConsoleViaPrintln = false;
		for (String loggingPackage : LOGGING_SYSTEMS_THAT_DO_NOT_ACCESS_CONSOLE) {
			if (usingLogFramework(currentLoggerImplementation, loggingPackage)) {
				loggingSystemDoesNotAccessConsoleViaPrintln = true;
				break;
			}
		}
		return loggingSystemDoesNotAccessConsoleViaPrintln;
	}

	private boolean loggingSystemKnownAndMightAccessConsoleViaPrintln(final Logger currentLoggerImplementation) {
		boolean loggingSystemKnownAndMightAccessConsoleViaPrintln = false;
		for (String loggingPackage : LOGGING_SYSTEMS_THAT_MIGHT_ACCESS_CONSOLE) {
			if (usingLogFramework(currentLoggerImplementation, loggingPackage)) {
				loggingSystemKnownAndMightAccessConsoleViaPrintln = true;
				break;
			}
		}
		return loggingSystemKnownAndMightAccessConsoleViaPrintln;
	}

	private void registerCurrentLoggingSystemPackage(final Logger currentLoggerImplementation) {
		for (String loggingPackage : LOGGING_SYSTEMS_THAT_MIGHT_ACCESS_CONSOLE) {
			if (usingLogFramework(currentLoggerImplementation, loggingPackage)) {
				loggingSystemRegister.registerLoggingSystem(loggingPackage);
			}
		}
	}

	private boolean usingLogFramework(final Logger currentLoggerImplementation, final String packageName) {
		return currentLoggerImplementation.getClass().getName().startsWith(packageName);
	}

}
