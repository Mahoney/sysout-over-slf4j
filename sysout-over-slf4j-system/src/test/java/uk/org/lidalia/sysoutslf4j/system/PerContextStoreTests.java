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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.system.PerContextStore;

public class PerContextStoreTests extends SysOutOverSLF4JTestCase {
	
	private final PerContextStore<String> storeUnderTest = new PerContextStore<String>();
	private final ClassLoader[] classLoaders = { new ClassLoader() { }, new ClassLoader() { } };
	private final String[] objectsToStore = { "1", "2" };
	
	@Test
	public void perContextStoreStoresValueRelativeToContextClassLoader() {
		storeValuesAgainstDifferentContextClassLoaders();
		assertCorrectValueReturnedForEachClassLoader();
	}

	private void storeValuesAgainstDifferentContextClassLoaders() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			storeUnderTest.put(objectsToStore[i]);
		}
	}
	
	private void assertCorrectValueReturnedForEachClassLoader() {
		for (int i = 0; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			assertSame(objectsToStore[i], storeUnderTest.get());
		}
	}
	
	@Test
	public void perContextStoreWorksIfContextClassLoaderIsNull() {
		currentThread().setContextClassLoader(null);
		storeUnderTest.put("value");
		assertNull(currentThread().getContextClassLoader());
		assertEquals("value", storeUnderTest.get());
	}
	
	@Test
	public void perContextStoreReturnsValueStoredAgainstParentOfContextClassLoader() {
		ClassLoader parent = new ClassLoader() { };
		String value = "aValue";
		currentThread().setContextClassLoader(parent);
		storeUnderTest.put(value);
		
		ClassLoader child = new ClassLoader(parent) { };
		currentThread().setContextClassLoader(child);
		
		assertSame(value, storeUnderTest.get());
	}
	
	@Test
	public void perContextStoreReturnsNullIfNoValueStoredAndNoDefaultSet() {
		assertNull(storeUnderTest.get());
	}

	@Test
	public void perContextStoreReturnsDefaultIfNoClassLoaderStored() {
		PerContextStore<String> storeUnderTest = new PerContextStore<String>("default");
		assertEquals("default", storeUnderTest.get());
	}

	@Test
	public void removeRemovesValueForCurrentContextClassLoader() {
		storeValuesAgainstDifferentContextClassLoaders();
		currentThread().setContextClassLoader(classLoaders[0]);
		storeUnderTest.remove();
		assertNull(storeUnderTest.get());
		for (int i = 1; i < classLoaders.length; i++) {
			currentThread().setContextClassLoader(classLoaders[i]);
			assertSame(objectsToStore[i], storeUnderTest.get());
		}
	}
	
	@Test
	public void getDefaultValueReturnsDefaultValue() {
		PerContextStore<String> storeUnderTest = new PerContextStore<String>("default");
		assertEquals("default", storeUnderTest.getDefaultValue());
	}
}
