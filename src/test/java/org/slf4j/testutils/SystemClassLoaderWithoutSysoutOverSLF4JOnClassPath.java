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
