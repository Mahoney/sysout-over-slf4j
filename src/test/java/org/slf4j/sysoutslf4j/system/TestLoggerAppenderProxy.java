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

package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.LoggerAppender;

public class TestLoggerAppenderProxy extends SysOutOverSLF4JTestCase {
	
	@Test
	public void wrapReturnsUnwrappedLoggerAppenderIfInSameClassLoader() {
		LoggerAppender expected = createMock(LoggerAppender.class);
		assertSame(expected, LoggerAppenderProxy.wrap(expected));
	}
	
	@Test
	public void wrapReturnsWrappedLoggerAppenderIfInDifferentClassLoader() throws Exception {
		FakeLoggerAppender loggerAppender = createMock(FakeLoggerAppender.class);
		loggerAppender.append("message");
		loggerAppender.appendAndLog("message2", "classname", true);
		replayAll();
		
		LoggerAppender wrappedLoggerAppender = LoggerAppenderProxy.wrap(loggerAppender);
		wrappedLoggerAppender.append("message");
		wrappedLoggerAppender.appendAndLog("message2", "classname", true);
		verifyAll();
	}
	
	private static interface FakeLoggerAppender {
		void append(String message);
		void appendAndLog(String message, String className, boolean isStackTrace);
	}
	
	@Test
	public void wrapThrowsNestedNoSuchMethodExceptionIfInstantiatedWithWrongType() throws Throwable {
		IllegalArgumentException iae = shouldThrow(IllegalArgumentException.class, new Callable<Void>() {
			public Void call() throws Exception {
				LoggerAppenderProxy.wrap(new Object());
				return null;
			}
		});
		assertSame(NoSuchMethodException.class, iae.getCause().getClass());
		assertEquals("Must only be instantiated with a LoggerAppender instance, got a class java.lang.Object", iae.getMessage());
	}
}
