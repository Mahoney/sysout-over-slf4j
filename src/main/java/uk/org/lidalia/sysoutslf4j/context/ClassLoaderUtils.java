/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.context;

import java.net.URL;

import uk.org.lidalia.sysoutslf4j.common.StringUtils;
import uk.org.lidalia.sysoutslf4j.common.WrappedCheckedException;

final class ClassLoaderUtils {
	
	static URL getJarURL(final Class<?> classInJar) {
		final String relativeClassFilePath = getRelativeFilePathOfClass(classInJar);
		
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
