/**
 * 
 */
package org.slf4j.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.io.IOUtils;

public class SimpleClassloader extends ClassLoader {
	private final ClassLoader realClassLoader = getClass().getClassLoader();
	
    private SimpleClassloader(ClassLoader parent) {
        super(parent);
    }

    protected Class<?> findClass(String name) {
    	String fileName = name.replace('.', '/') + ".class";
    	InputStream classAsStream = realClassLoader.getResourceAsStream(fileName);
		try {
			byte[] classAsByteArray = IOUtils.toByteArray(classAsStream);
			return defineClass(name, classAsByteArray, 0, classAsByteArray.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    @Override
    public URL getResource(String name) {
    	return realClassLoader.getResource(name);
    }
    
    public static SimpleClassloader make() {
    	return make(null);
    }
    		
    public static SimpleClassloader make(final ClassLoader parent) {
    	return AccessController.doPrivileged(new PrivilegedAction<SimpleClassloader>() {
			public SimpleClassloader run() {
				return new SimpleClassloader(parent);
			}
		});
    }

}
