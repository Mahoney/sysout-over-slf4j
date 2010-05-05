package org.slf4j.integration.sysoutslf4j;

import static org.junit.Assert.assertTrue;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.slf4j.testutils.LoggingUtils;
import org.slf4j.testutils.SimpleClassloader;

public class TestForClassloaderLeaks extends SysOutOverSlf4jIntegrationTestCase {
	private static final int NUMBER_OF_CLASSLOADERS = 3;
	
	ClassLoader fakeSystemClassLoader = new SystemClassLoaderWrapper();

	@Test
	public void classLoaderCanBeGarbageCollectedAfterCallingSendSystemOutAndErrToSLF4J() throws Exception {
		ClassLoaderHolder classLoaderHolder = new ClassLoaderHolder(0);
		setSystemClassLoaderUnableToLoadSysoutOverSLF4J(classLoaderHolder);
		
		givenThatSystemOutAndErrHaveBeenSentToSLF4JAndThenStoppedBeingSentToSLF4JInAClassLoader(classLoaderHolder);
		whenNoReferencesToThatClassLoaderExist(classLoaderHolder);
		thenThatClassLoaderShouldBeGarbageCollected(classLoaderHolder);
	}

	private void setSystemClassLoaderUnableToLoadSysoutOverSLF4J(
			ClassLoaderHolder classLoaderHolder) throws ClassNotFoundException {
		Class<?> classLoaderUtilsClass = classLoaderHolder.classLoader.loadClass("org.slf4j.sysoutslf4j.context.ClassLoaderUtils");
		Whitebox.setInternalState(classLoaderUtilsClass, fakeSystemClassLoader);
	}

	private void givenThatSystemOutAndErrHaveBeenSentToSLF4JAndThenStoppedBeingSentToSLF4JInAClassLoader(ClassLoaderHolder classLoaderHolder) throws Exception {
		callSendSystemOutAndErrToSLF4JInClassLoader(classLoaderHolder.classLoader);
		callStopSendingSystemOutAndErrToSLF4JInClassLoader(classLoaderHolder.classLoader);
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
			setSystemClassLoaderUnableToLoadSysoutOverSLF4J(classLoaderHolders[i]);
		}
		givenThatSystemOutAndErrHaveBeenSentToSLF4JAndThenStoppedBeingSentToSLF4JInSeveralClassLoaders(classLoaderHolders);
		whenNoReferencesToThoseClassLoadersExist(classLoaderHolders);
		thenThoseClassLoadersShouldBeGarbageCollected(classLoaderHolders);
	}
	
	private void givenThatSystemOutAndErrHaveBeenSentToSLF4JAndThenStoppedBeingSentToSLF4JInSeveralClassLoaders(
			ClassLoaderHolder[] classLoaderHolders) throws Exception {
		for (ClassLoaderHolder classLoaderHolder : classLoaderHolders) {
			callSendSystemOutAndErrToSLF4JInClassLoader(classLoaderHolder.classLoader);
			callStopSendingSystemOutAndErrToSLF4JInClassLoader(classLoaderHolder.classLoader);
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
		assertTrue("classLoader " + classLoaderHolder.number + " has not been garbage collected",
				classLoaderHolder.referenceToClassLoader.isEnqueued());
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
	
	private static class SystemClassLoaderWrapper extends ClassLoader {
		
		@Override
		public Class<?> loadClass(String name, boolean blah) throws ClassNotFoundException {
			if (name.startsWith("org.slf4j.sysoutslf4j")) {
				throw new ClassNotFoundException();
			}
			return super.loadClass(name, blah);
		}
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("org.slf4j.sysoutoverslf4j")) {
				throw new ClassNotFoundException();
			}
			return super.findClass("org.slf4j.sysoutslf4j");
		}
	}
}
