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

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.LoggerAppender;
import uk.org.lidalia.sysoutslf4j.system.LoggerAppenderStore;

public class TestLoggerAppenderStore extends SysOutOverSLF4JTestCase {
	
	private final LoggerAppenderStore storeUnderTest = new LoggerAppenderStore();
	private final ClassLoader[] classLoaders = { new ClassLoader() { }, new ClassLoader() { } };
	private final LoggerAppender[] loggerAppenders = { createMock(LoggerAppender.class), createMock(LoggerAppender.class) };
	
	@Test
	public void loggerAppenderStoresRelativeToContextClassLoader() {
		storeAppendersAgainstDifferentContextClassLoaders();
		assertCorrectAppenderReturnedForEachClassLoader();
	}

	private void storeAppendersAgainstDifferentContextClassLoaders() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			storeUnderTest.put(loggerAppenders[i]);
		}
	}
	
	private void assertCorrectAppenderReturnedForEachClassLoader() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			assertSame(loggerAppenders[i], storeUnderTest.get());
		}
	}
	
	@Test
	public void loggerAppenderStoreWorksIfContextClassLoaderIsNull() {
		currentThread().setContextClassLoader(null);
		storeUnderTest.put(loggerAppenders[0]);
		assertNull(currentThread().getContextClassLoader());
		assertSame(loggerAppenders[0], storeUnderTest.get());
	}
	
	@Test
	public void loggerAppenderStoreReturnsLoggerAppenderStoredAgainstParentOfContextClassLoader() {
		ClassLoader parent = new ClassLoader() { };
		LoggerAppender loggerAppender = createMock(LoggerAppender.class);
		currentThread().setContextClassLoader(parent);
		storeUnderTest.put(loggerAppender);
		
		ClassLoader child = new ClassLoader(parent) { };
		currentThread().setContextClassLoader(child);
		
		assertSame(loggerAppender, storeUnderTest.get());
	}
	
	@Test
	public void loggerAppenderStoreReturnsNullIfNoClassLoaderStored() {
		assertNull(storeUnderTest.get());
	}

	@Test
	public void removeRemovesLoggerAppenderForCurrentContextClassLoader() {
		storeAppendersAgainstDifferentContextClassLoaders();
		currentThread().setContextClassLoader(classLoaders[0]);
		storeUnderTest.remove();
		assertNull(storeUnderTest.get());
		for (int i = 1; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			assertSame(loggerAppenders[i], storeUnderTest.get());
		}
	}
}
