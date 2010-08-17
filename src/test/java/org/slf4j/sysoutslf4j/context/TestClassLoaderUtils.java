package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;
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
}
