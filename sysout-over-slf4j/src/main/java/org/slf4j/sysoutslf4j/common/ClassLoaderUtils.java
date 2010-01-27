package org.slf4j.sysoutslf4j.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ClassLoaderUtils {
	
	public static ClassLoader makeNewClassLoaderForJar(final Class<?> classInJar) {
		return makeNewClassLoaderForJar(classInJar, ClassLoader.getSystemClassLoader());
	}
	
	public static ClassLoader makeNewClassLoaderForJar(final Class<?> classInJar, final ClassLoader parent) {
		final URL jarURL = getJarURL(classInJar); // NOPMD
		return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
			public URLClassLoader run() {
				return new URLClassLoader(new URL[]{jarURL}, parent);
			}
		});
	}

	// TODO give a bit more context in the thrown exception...
	private static URL getJarURL(final Class<?> classInJar) {
		try {
			final String relativeClassFilePath = getRelativeFilePathOfClass(classInJar); // NOPMD
			final URL classURL = getResource(relativeClassFilePath);
			final String jarURLString = StringUtils.substringBefore(classURL.toString(), relativeClassFilePath);
			return new URL(jarURLString);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Should not be possible", e);
		}
	}
	
	private static URL getResource(final String relativeFilePath) {
		return Thread.currentThread().getContextClassLoader().getResource(relativeFilePath);
	}

	private static String getRelativeFilePathOfClass(final Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	public static Class<?> loadClass(final ClassLoader classLoader, final Class<?> classToLoad) {
		try {
			return classLoader.loadClass(classToLoad.getName());
		} catch (ClassNotFoundException cne) {
			throw new RuntimeException(cne); // NOPMD
		}
	}

	private ClassLoaderUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
