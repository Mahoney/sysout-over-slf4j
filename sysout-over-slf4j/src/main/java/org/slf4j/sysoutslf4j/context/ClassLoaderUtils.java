package org.slf4j.sysoutslf4j.context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.slf4j.sysoutslf4j.common.StringUtils;
import org.slf4j.sysoutslf4j.common.WrappedCheckedException;

final class ClassLoaderUtils {
	
	private static ClassLoader SYSTEM_CLASSLOADER = ClassLoader.getSystemClassLoader();
	
	static ClassLoader makeNewClassLoaderForJar(final Class<?> classInJar) {
		final URL jarURL = getJarURL(classInJar);
		return InsecureURLClassLoader.newInstance(Arrays.asList(new URL[]{jarURL}), SYSTEM_CLASSLOADER);
	}
	
	private static URL getJarURL(final Class<?> classInJar) {
		final String relativeClassFilePath = getRelativeFilePathOfClass(classInJar); // NOPMD
		final URL classURL = getResource(relativeClassFilePath);
		final String classUrlString = classURL == null ? "" : classURL.toString();
		final String jarURLString = StringUtils.substringBefore(classUrlString, relativeClassFilePath);
		try {
			return new URL(jarURLString);
		} catch (MalformedURLException malformedURLException) {
			throw new WrappedCheckedException(
					"Unable to build jar URL from url [" + classURL + "] from class [" + classInJar + "]", malformedURLException);
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
