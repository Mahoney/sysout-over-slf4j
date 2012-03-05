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

package uk.org.lidalia.sysoutslf4j.system;

import static org.junit.Assert.assertNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.system.PerContextStore;

public class PerContextStoreMemoryManagementTests extends SysOutOverSLF4JTestCase {

	private final PerContextStore<String> storeUnderTest = new PerContextStore<String>();

	private ClassLoader classLoader = new ClassLoader(){};
	private final WeakReference<ClassLoader> refToClassLoader =
		new WeakReference<ClassLoader>(classLoader, new ReferenceQueue<Object>());
	private String valueToStore = "some value";

	@Test
	public void loggerAppenderStoreDoesNotCauseAClassLoaderLeak() throws Exception {
		storeLoggerAppenderAgainstClassLoader();
		removeLocalReferenceToClassLoader();
		removeLocalReferenceToLoggerAppenderAndGarbageCollect();
		assertClassLoaderHasBeenGarbageCollected();
	}

	private void storeLoggerAppenderAgainstClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoader);
		storeUnderTest.put(valueToStore);
	}

	private void removeLocalReferenceToClassLoader() {
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
		classLoader = null;
	}
	
	private void removeLocalReferenceToLoggerAppenderAndGarbageCollect() {
		valueToStore = null;
		System.gc();
	}


	private void assertClassLoaderHasBeenGarbageCollected() {
		assertNull("classloader has not been garbage collected", refToClassLoader.get());
	}
}
