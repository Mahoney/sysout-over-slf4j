package org.slf4j.sysoutslf4j.common;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClassLoaderUtils.class })
public class TestClassLoaderUtils extends SysOutOverSLF4JTestCase {
	
	@Test
	public void makeNewClassLoaderForJarHasCorrectJarURL() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		URL junitJarUrl = classLoader.getURLs()[0];
		assertNotNull(junitJarUrl.getContent());
		assertTrue(junitJarUrl.toString().startsWith("jar:file:/"));
		assertTrue(junitJarUrl.toString().endsWith(".jar!/"));
	}

	@Test
	public void makeNewClassLoaderForJarHasOnlyOneURL() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		URL[] classLoaderUrls = classLoader.getURLs();
		assertEquals(1, classLoaderUrls.length);
	}
	
	@Test
	public void makeNewClassLoaderForJarHasSystemParent() throws Exception {
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		assertSame(ClassLoader.getSystemClassLoader(), classLoader.getParent());
	}

	@Test
	public void makeNewClassLoaderForJarWithNullParentHasCorrectJarURL() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, null);
		URL junitJarUrl = classLoader.getURLs()[0];
		assertNotNull(junitJarUrl.getContent());
		assertTrue(junitJarUrl.toString().startsWith("jar:file:/"));
		assertTrue(junitJarUrl.toString().endsWith(".jar!/"));
	}

	@Test
	public void makeNewClassLoaderForJarWithNullParentHasOnlyOneURL() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, null);
		URL[] classLoaderUrls = classLoader.getURLs();
		assertEquals(1, classLoaderUrls.length);
	}
	
	@Test
	public void makeNewClassLoaderForJarWithNullParentHasNullParent() throws Exception {
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, null);
		assertNull(classLoader.getParent());
	}
	
	@Test
	public void makeNewClassLoaderForJarHasGivenParent() throws Exception {
		ClassLoader expectedParentClassLoader = new ClassLoader() { };
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, expectedParentClassLoader);
		assertSame(expectedParentClassLoader, classLoader.getParent());
	}
	
	@Test
	public void makeNewClassLoaderForJarWithClassNotInJar() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(TestClassLoaderUtils.class, null);
		URL classFolderUrl = classLoader.getURLs()[0];
		assertNotNull(classFolderUrl.getContent());
		assertTrue(classFolderUrl + "should start with file:/", classFolderUrl.toString().startsWith("file:/"));
		String classFolderSuffix = "/target/test-classes/";
		assertTrue(classFolderUrl + "should end with " + classFolderSuffix,
				classFolderUrl.toString().endsWith(classFolderSuffix));
	}
	
	@Test
	public void makeNewClassLoaderForJarThrowsIllegalStateExceptionIfMalformedUrlOccurs() throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, null);
		
		MalformedURLException expected = new MalformedURLException();
		expectNew(URL.class, classLoader.getURLs()[0].toString()).andThrow(expected);
		replay(URL.class);
		
		try {
			ClassLoaderUtils.makeNewClassLoaderForJar(Test.class, null);
			fail();
		} catch (IllegalStateException ise) {
			assertSame(expected, ise.getCause());
			assertEquals("Should not be possible", ise.getMessage());
		}
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
	public void loadClassMakesClassNotFoundExceptionUnchecked() throws Exception {
		ClassLoader mockClassLoader = createMock(ClassLoader.class);
		ClassNotFoundException expected = new ClassNotFoundException();
		expect(mockClassLoader.loadClass("java.lang.Object")).andThrow(expected);
		replay(mockClassLoader);
		
		try {
			ClassLoaderUtils.loadClass(mockClassLoader, Object.class);
			fail();
		} catch (RuntimeException e) {
			assertSame(expected, e.getCause());
		}
	}
	
	@Test
	public void classLoaderUtilsNotInstantiable() throws Exception {
		try {
			Whitebox.invokeConstructor(ClassLoaderUtils.class);
			fail();
		} catch (UnsupportedOperationException oue) {
			assertEquals("Not instantiable", oue.getMessage());
		}
	}
}
