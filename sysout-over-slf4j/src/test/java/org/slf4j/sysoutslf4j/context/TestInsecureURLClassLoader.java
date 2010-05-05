package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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
		ClassLoader classLoader = ClassLoaderUtils.makeNewClassLoaderForJar(Test.class);
		System.out.println(classLoader.loadClass("org.junit.Test"));
	}
}
