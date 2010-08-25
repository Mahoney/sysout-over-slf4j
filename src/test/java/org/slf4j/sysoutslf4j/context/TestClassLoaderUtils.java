package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.net.URL;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.WrappedCheckedException;

public class TestClassLoaderUtils extends SysOutOverSLF4JTestCase {
	
	@Test
	@SuppressWarnings("unchecked")
	public void loadClassLoadsClassFromClassloader() throws Exception {
		ClassLoader mockClassLoader = createMock(ClassLoader.class);
		Class expected = Object.class;
		expect(mockClassLoader.loadClass("java.lang.Object")).andReturn(expected);
		replay(mockClassLoader);
		
		assertEquals(expected, ClassLoaderUtils.loadClass(mockClassLoader, Object.class));
	}
	
	@Test
	public void loadClassMakesClassNotFoundExceptionUnchecked() throws Throwable {
		final ClassLoader mockClassLoader = createMock(ClassLoader.class);
		final ClassNotFoundException expected = new ClassNotFoundException();
		expect(mockClassLoader.loadClass("java.lang.Object")).andThrow(expected);
		replay(mockClassLoader);

		WrappedCheckedException exception = shouldThrow(WrappedCheckedException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ClassLoaderUtils.loadClass(mockClassLoader, Object.class);
				return null;
			}
		});
		assertSame(expected, exception.getCause());
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(ClassLoaderUtils.class);
	}
	
	@Test
	public void getJarUrlReturnsCredibleUrl() throws Exception {
		URL jarUrl = ClassLoaderUtils.getJarURL(Logger.class);
		assertTrue(jarUrl.toString().matches("^jar:file:.*slf4j-api.*\\.jar!\\/$"));
	}
	
	@Test
	public void exceptionBuildingJarUrlThrownAsWrappedCheckedException() throws Throwable {
		final RuntimeException exceptionGettingResource = new RuntimeException();
		Thread.currentThread().setContextClassLoader(new ClassLoader() {
			@Override
			public URL getResource(String name) {
				throw exceptionGettingResource;
			}
		});
		
		IllegalStateException exception = shouldThrow(IllegalStateException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ClassLoaderUtils.getJarURL(Object.class);
				return null;
			}
		});
		
		assertSame(exceptionGettingResource, exception.getCause());
		assertEquals("Unable to build jar URL from class [" + Object.class + "]", exception.getMessage());
	}
}
