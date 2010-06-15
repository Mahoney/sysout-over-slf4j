package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.testutils.SimpleClassloader;

public class TestInsecureURLClassLoader {

	@Test
	public void newInstanceCreatesClassLoaderWithCorrectURLs() throws Exception {
		List<URL> urls = Arrays.asList(new URL[] {new URL("file:/somedir"), new URL("file:/someotherdir")});
		InsecureURLClassLoader classLoader = InsecureURLClassLoader.newInstance(urls, null);
		assertEquals(urls, classLoader.getURLs());
	}
	
	@Test
	public void newInstanceCreatesClassLoaderWithCorrectParent() throws Exception {
		List<URL> urls = Arrays.asList(new URL[] {new URL("file:/somedir"), new URL("file:/someotherdir")});
		ClassLoader parent = new ClassLoader() {};
		InsecureURLClassLoader classLoader = InsecureURLClassLoader.newInstance(urls, parent);
		assertEquals(parent, classLoader.getParent());
	}
	
	@Test
	public void findClass() throws Exception {
		URL jarUrl = ClassLoaderUtils.getJarURL(Test.class);
		List<URL> urls = Arrays.asList(new URL[] { jarUrl });
		ClassLoader classLoader = InsecureURLClassLoader.newInstance(urls, new SystemClassLoaderWrapper());
		Class<?> loadedClass = classLoader.loadClass("org.junit.Test");
		assertSame(classLoader, loadedClass.getClassLoader());
	}
	
		private static class SystemClassLoaderWrapper extends ClassLoader {
		
		@Override
		public Class<?> loadClass(String name, boolean blah) throws ClassNotFoundException {
			if (name.startsWith("org.junit")) {
				throw new ClassNotFoundException();
			}
			return super.loadClass(name, blah);
		}
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("org.junit")) {
				throw new ClassNotFoundException();
			}
			return super.findClass("org.slf4j.sysoutslf4j");
		}
	}
}
