package org.slf4j.sysoutslf4j.context;

import java.net.URL;

import org.slf4j.sysoutslf4j.common.StringUtils;
import org.slf4j.sysoutslf4j.common.WrappedCheckedException;

final class ClassLoaderUtils {
	
	static URL getJarURL(final Class<?> classInJar) {
		final String relativeClassFilePath = getRelativeFilePathOfClass(classInJar); // NOPMD
		
		try {
			final URL classURL = getResource(relativeClassFilePath);
			final String classUrlString = classURL.toString();
			final String jarURLString = StringUtils.substringBefore(classUrlString, relativeClassFilePath);
			return new URL(jarURLString);
		} catch (Exception exception) {
			throw new IllegalStateException(
					"Unable to build jar URL from class [" + classInJar + "]", exception);
		}
	}

	private static String getRelativeFilePathOfClass(final Class<?> clazz) {
		return clazz.getName().replace('.', '/') + ".class";
	}

	private static URL getResource(final String relativeFilePath) {
		return Thread.currentThread().getContextClassLoader().getResource(relativeFilePath);
	}

	static Class<?> loadClass(final ClassLoader classLoader, final Class<?> classToLoad) {
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
