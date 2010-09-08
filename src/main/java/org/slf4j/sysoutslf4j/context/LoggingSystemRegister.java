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
