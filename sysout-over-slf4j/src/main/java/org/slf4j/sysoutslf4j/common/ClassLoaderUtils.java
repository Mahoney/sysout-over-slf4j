package org.slf4j.sysoutslf4j.common;

import static org.apache.commons.lang.StringUtils.substringBefore;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ClassLoaderUtils {
	
	public static ClassLoader makeNewClassLoaderForJar(Class<?> classInJar) {
		return makeNewClassLoaderForJar(classInJar, ClassLoader.getSystemClassLoader());
	}
	
	public static ClassLoader makeNewClassLoaderForJar(Class<?> classInJar, final ClassLoader parent) {
		final URL jarURL = getJarURL(classInJar);
		URLClassLoader result = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
			public URLClassLoader run() {
				return new URLClassLoader(new URL[]{jarURL}, parent);
			}
		});
		return result;
	}

	private static URL getJarURL(Class<?> classInJar) {
		try {
			String relativeClassFilePath = getRelativeFilePathOfClass(classInJar);
			URL classURL = getResource(relativeClassFilePath);
			String jarURLString = substringBefore(classURL.toString(), relativeClassFilePath);
			return new URL(jarURLString);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Should not be possible", e);
		}
	}
	
	private static URL getResource(String relativeFilePath) {
		return ClassLoaderUtils.class.getClassLoader().getResource(relativeFilePath);
	}

	private static String getRelativeFilePathOfClass(Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	public static Class<?> loadClass(ClassLoader classLoader, Class<?> classToLoad) {
		try {
			return classLoader.loadClass(classToLoad.getName());
		} catch (ClassNotFoundException cne) {
			throw new RuntimeException(cne);
		}
	}

	private ClassLoaderUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
