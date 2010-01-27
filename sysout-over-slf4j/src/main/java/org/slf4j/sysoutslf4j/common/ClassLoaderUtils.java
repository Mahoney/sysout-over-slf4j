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
		final URL jarURL = getJarURL(classInJar);
		return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
			public URLClassLoader run() {
				return new URLClassLoader(new URL[]{jarURL}, parent);
			}
		});
	}

	// TODO give a bit more context in the thrown exception...
	private static URL getJarURL(final Class<?> classInJar) {
		final String relativeClassFilePath = getRelativeFilePathOfClass(classInJar); // NOPMD
		final URL classURL = getResource(relativeClassFilePath);
		final String classUrlString = classURL == null ? "" : classURL.toString();
		final String jarURLString = StringUtils.substringBefore(classUrlString, relativeClassFilePath);
		try {
			return new URL(jarURLString);
		} catch (MalformedURLException malformedURLException) {
			throw new WrappedCheckedException(malformedURLException);
		}
	}

	private static String getRelativeFilePathOfClass(final Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	private static URL getResource(final String relativeFilePath) {
		return Thread.currentThread().getContextClassLoader().getResource(relativeFilePath);
	}

	public static Class<?> loadClass(final ClassLoader classLoader, final Class<?> classToLoad) {
		try {
			return classLoader.loadClass(classToLoad.getName());
		} catch (ClassNotFoundException cne) {
			throw new WrappedCheckedException(cne);
		}
	}

	private ClassLoaderUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
