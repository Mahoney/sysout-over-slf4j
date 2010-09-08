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

package org.slf4j.integration.sysoutslf4j;

import static org.junit.Assert.assertNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.testutils.LoggingUtils;
import org.slf4j.testutils.SimpleClassloader;

public class TestForClassloaderLeaks extends SysOutOverSLF4JTestCase {
	private static final int NUMBER_OF_CLASSLOADERS = 3;
	
	@Test
	public void classLoaderCanBeGarbageCollectedAfterCallingSendSystemOutAndErrToSLF4J() throws Exception {
		ClassLoaderHolder classLoaderHolder = new ClassLoaderHolder(0);
		
		givenThatSystemOutAndErrHaveBeenSentToSLF4JInAClassLoader(classLoaderHolder);
		whenNoReferencesToThatClassLoaderExist(classLoaderHolder);
		thenThatClassLoaderShouldBeGarbageCollected(classLoaderHolder);
	}

	private void givenThatSystemOutAndErrHaveBeenSentToSLF4JInAClassLoader(ClassLoaderHolder classLoaderHolder) throws Exception {
		callSendSystemOutAndErrToSLF4JInClassLoader(classLoaderHolder.classLoader);
	}
	
	private void whenNoReferencesToThatClassLoaderExist(ClassLoaderHolder classLoaderHolder) {
		destroyLocalReferenceToClassLoader(classLoaderHolder);
	}
	
	private void thenThatClassLoaderShouldBeGarbageCollected(ClassLoaderHolder classLoaderHolder) {
		assertThatClassLoaderHasBeenGarbageCollected(classLoaderHolder);
	}

	@Test
	public void multipleClassLoadersCanBeGarbageCollectedAfterCallingSendSystemOutAndErrToSLF4JWhenAllCreatedAndThenAllDestroyed()
				throws Exception {
		ClassLoaderHolder[] classLoaderHolders = new ClassLoaderHolder[NUMBER_OF_CLASSLOADERS];
		for (int i = 0; i < NUMBER_OF_CLASSLOADERS; i++) {
			classLoaderHolders[i] = new ClassLoaderHolder(i);
		}
		givenThatSystemOutAndErrHaveBeenSentToSLF4JInSeveralClassLoaders(classLoaderHolders);
		whenNoReferencesToThoseClassLoadersExist(classLoaderHolders);
		thenThoseClassLoadersShouldBeGarbageCollected(classLoaderHolders);
	}
	
	private void givenThatSystemOutAndErrHaveBeenSentToSLF4JInSeveralClassLoaders(
			ClassLoaderHolder[] classLoaderHolders) throws Exception {
		for (ClassLoaderHolder classLoaderHolder : classLoaderHolders) {
			givenThatSystemOutAndErrHaveBeenSentToSLF4JInAClassLoader(classLoaderHolder);
		}
	}

	private void whenNoReferencesToThoseClassLoadersExist(ClassLoaderHolder[] classLoaderHolders) {
		for (ClassLoaderHolder classLoaderHolder : classLoaderHolders) {
			destroyLocalReferenceToClassLoader(classLoaderHolder);
		}
	}
	
	private void thenThoseClassLoadersShouldBeGarbageCollected(ClassLoaderHolder[] classLoaderHolders) {
		for (ClassLoaderHolder classLoaderHolder : classLoaderHolders) {
			assertThatClassLoaderHasBeenGarbageCollected(classLoaderHolder);
		}
	}

	private void destroyLocalReferenceToClassLoader(ClassLoaderHolder classLoaderHolder) {
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
		classLoaderHolder.classLoader = null;
	}

	private void assertThatClassLoaderHasBeenGarbageCollected(ClassLoaderHolder classLoaderHolder) {
		System.gc();
		
		assertNull("classLoader " + classLoaderHolder.number + " has not been garbage collected",
				classLoaderHolder.referenceToClassLoader.get());
	}

	private static class ClassLoaderHolder {
		private ClassLoader classLoader = SimpleClassloader.make();
		private WeakReference<ClassLoader> referenceToClassLoader =
			new WeakReference<ClassLoader>(classLoader, new ReferenceQueue<ClassLoader>());
		private int number;

		private ClassLoaderHolder(int number) {
			this.number = number;
			LoggingUtils.turnOffRootLogging(classLoader);
		}
	}
	
}
