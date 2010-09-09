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

import java.lang.reflect.Method;

import uk.org.lidalia.sysoutslf4j.common.LoggerAppender;
import uk.org.lidalia.sysoutslf4j.common.ReflectionUtils;

final class LoggerAppenderProxy implements LoggerAppender {
	
	private final Object targetLoggerAppender;
	private final Method appendMethod;
	private final Method appendAndLogMethod;

	private LoggerAppenderProxy(final Object targetLoggerAppender) {
		super();
		try {
			final Class<?> loggerAppenderClass = targetLoggerAppender.getClass();
			this.targetLoggerAppender = targetLoggerAppender;
			this.appendMethod = loggerAppenderClass.getDeclaredMethod("append", String.class);
			this.appendAndLogMethod =
				loggerAppenderClass.getDeclaredMethod("appendAndLog", String.class, String.class, boolean.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Must only be instantiated with a LoggerAppender instance, got a " + targetLoggerAppender.getClass(), e);
		}
	}

	public void append(final String message) {
		ReflectionUtils.invokeMethod(appendMethod, targetLoggerAppender, message);
	}

	public void appendAndLog(final String message, final String className, final boolean isStackTrace) {
		ReflectionUtils.invokeMethod(appendAndLogMethod, targetLoggerAppender, message, className, isStackTrace);
	}
	
	static LoggerAppender wrap(final Object targetLoggerAppender) {
		final LoggerAppender result;
		if (targetLoggerAppender instanceof LoggerAppender) {
			result = (LoggerAppender) targetLoggerAppender;
		} else {
			result = new LoggerAppenderProxy(targetLoggerAppender);
		}
		return result;
	}
}
