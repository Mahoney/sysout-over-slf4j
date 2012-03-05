/* 
 * Copyright (c) 2009-2012 Robert Elliot
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

package uk.org.lidalia.testutils;

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
    	if (name.startsWith("uk.org.lidalia.sysoutslf4j.system")) {
    		try {
				return realClassLoader.loadClass(name);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
    	}
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
