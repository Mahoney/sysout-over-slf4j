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

package uk.org.lidalia.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Initialiser {
	
	private static final Logger LOG = LoggerFactory.getLogger(Initialiser.class);
	
	private static final String UNKNOWN_LOGGING_SYSTEM_MESSAGE =
		"Your logging framework {} is not known - if it needs access to the console you will need to register it " +
		"by calling SysOutOverSLF4J.registerLoggingSystem";

	private static final String[] LOGGING_SYSTEMS_THAT_MIGHT_ACCESS_CONSOLE =
		{ "org.x4juli.", "org.grlea.log.", "org.slf4j.impl.", "ch.qos.logback.", "org.apache.log4j." };
	
	private final LoggingSystemRegister loggingSystemRegister;
	
	Initialiser(final LoggingSystemRegister loggingSystemRegister) {
		this.loggingSystemRegister = loggingSystemRegister;
	}

	void initialise(final Logger currentLoggerImplementation) {
		if (loggingSystemKnownAndMightAccessConsoleViaPrintln(currentLoggerImplementation)) {
			registerCurrentLoggingSystemPackage(currentLoggerImplementation);
		} else {
			LOG.warn(UNKNOWN_LOGGING_SYSTEM_MESSAGE, currentLoggerImplementation.getClass());
		}
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
