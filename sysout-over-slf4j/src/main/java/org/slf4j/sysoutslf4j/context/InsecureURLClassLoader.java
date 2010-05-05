package org.slf4j.sysoutslf4j.context;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

class InsecureURLClassLoader extends ClassLoader {

	private final List<URL> urls;
	
	private InsecureURLClassLoader(List<URL> urls, ClassLoader parent) {
		super(parent);
		this.urls = Collections.unmodifiableList(urls);
	}
	
	static InsecureURLClassLoader newInstance(List<URL> urls, ClassLoader parent) {
		return new InsecureURLClassLoader(urls, parent);
	}
	
	List<URL> getURLs() {
		return urls;
	}
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return findClass(name);
	}
	
	protected Class<?> findClass(final String name)
	 throws ClassNotFoundException
   {
	try {
		ZipFile file = AccessController.doPrivileged(new PrivilegedExceptionAction<ZipFile>() {
		    public ZipFile run() throws ClassNotFoundException {
			String path = name.replace('.', '/').concat(".class");
			for (URL url : urls) {
				try {
					URL fullUrl = new URL(url, path);
					InputStream is = (InputStream) fullUrl.getContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				juc.
//				return defineClass(name, juc.get, 0, len)
//				juc.get
//				JarFile jf = juc.getJarFile();

			}
			return null;
//			Resource res = ucp.getResource(path, false);
//			if (res != null) {
//			    try {
//				return defineClass(name, res);
//			    } catch (IOException e) {
//				throw new ClassNotFoundException(name, e);
//			    }
//			} else {
//			    throw new ClassNotFoundException(name);
//			}
		    }
		});
		System.err.println(file.getName());
		return null;
	} catch (java.security.PrivilegedActionException pae) {
	    throw (ClassNotFoundException) pae.getException();
	}
   }
}
