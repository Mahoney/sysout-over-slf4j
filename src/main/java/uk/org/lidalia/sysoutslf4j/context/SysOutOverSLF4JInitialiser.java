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

package uk.org.lidalia.sysoutslf4j.context;

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
