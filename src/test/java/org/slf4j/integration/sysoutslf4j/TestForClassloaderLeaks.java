package org.slf4j.integration.sysoutslf4j;

import static org.junit.Assert.assertNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.junit.Test;
import org.slf4j.testutils.LoggingUtils;
import org.slf4j.testutils.SimpleClassloader;

public class TestForClassloaderLeaks extends SysOutOverSlf4jIntegrationTestCase {
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
