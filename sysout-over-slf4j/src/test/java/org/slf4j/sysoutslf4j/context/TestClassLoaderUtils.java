package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.WrappedCheckedException;
import org.slf4j.testutils.ClassCreationUtils;

public class TestClassLoaderUtils extends SysOutOverSLF4JTestCase {
	
	@Test
	public void makeNewClassLoaderForJarHasCorrectJarURL() throws Exception {
		InsecureURLClassLoader classLoader = (InsecureURLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		assertEquals(1, classLoader.getURLs().size());
		URL junitJarUrl = classLoader.getURLs().get(0);
		assertNotNull(junitJarUrl.getContent());
		assertTrue(junitJarUrl.toString().startsWith("jar:file:/"));
		assertTrue(junitJarUrl.toString().endsWith(".jar!/"));
	}
	
	@Test
	public void makeNewClassLoaderForJarHasSystemParent() throws Exception {
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		assertSame(ClassLoader.getSystemClassLoader(), classLoader.getParent());
	}
	
	@Test
	public void makeNewClassLoaderForJarWithClassNotInJar() throws Exception {
		InsecureURLClassLoader classLoader = (InsecureURLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(TestClassLoaderUtils.class);
		URL classFolderUrl = classLoader.getURLs().get(0);
		assertNotNull(classFolderUrl.getContent());
		assertTrue(classFolderUrl + "should start with file:/", classFolderUrl.toString().startsWith("file:/"));
		String classFolderSuffix = "/target/test-classes/";
		assertTrue(classFolderUrl + "should end with " + classFolderSuffix,
				classFolderUrl.toString().endsWith(classFolderSuffix));
	}
	
	@Test
	public void makeNewClassLoaderForJarThrowsIllegalStateExceptionIfMalformedUrlOccurs() throws Throwable {
		final Class<?> randomClass = ClassCreationUtils.makeClass("org.Something");
		final WrappedCheckedException expectedException = shouldThrow(WrappedCheckedException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ClassLoaderUtils.makeNewClassLoaderForJar(randomClass);
				return null;
			}
		});
		assertEquals(MalformedURLException.class, expectedException.getCause().getClass());
		assertEquals("Unable to build jar URL from url [null] from class [class org.Something]", expectedException.getMessage());
	}
	
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
