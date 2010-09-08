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

/**
 * 
 */
package org.slf4j.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.IOUtils;

public class SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath extends URLClassLoader {
	
	private final ClassLoader realSystemClassLoader = ClassLoader.getSystemClassLoader();
	private URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], null);
	
    public SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath() {
        super(new URL[0], null);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	if (name.startsWith("org.slf4j.sysoutslf4j")) {
    		try {
    			return findClassFromClassLoader(name, urlClassLoader);
    		} catch (Exception e) {
    			throw new ClassNotFoundException(name, e);
    		}
    	}
    	return findClassFromClassLoader(name, realSystemClassLoader);
    }

	private Class<?> findClassFromClassLoader(String name, ClassLoader classLoader) throws ClassFormatError {
		String fileName = name.replace('.', '/') + ".class";
		InputStream classAsStream = classLoader.getResourceAsStream(fileName);
		if (classAsStream == null) {
			throw new NullPointerException("failed to find " + name + " in class loader " + classLoader);
		}
		try {
			byte[] classAsByteArray = IOUtils.toByteArray(classAsStream);
			return defineClass(name, classAsByteArray, 0, classAsByteArray.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    
    @Override
    public URL getResource(String name) {
    	return realSystemClassLoader.getResource(name);
    }
    
    @Override
    public void addURL(URL url) {
    	urlClassLoader = new URLClassLoader(new URL[]{url}, null);
    }
}
