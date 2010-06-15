package org.slf4j.sysoutslf4j.context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

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
	
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");
		for (URL url : urls) {
			try {
				URL fullUrl = new URL(url, path);
				InputStream is = (InputStream) fullUrl.getContent();
				BufferedInputStream buffer = new BufferedInputStream(is);
				ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
				int c;
				while ((c = buffer.read()) != -1) {
					bytesOut.write(c);
				}
				byte[] bytes = bytesOut.toByteArray();
				Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
				return clazz;
			} catch (IOException e) {
				throw new ClassNotFoundException();
			}
		}
		throw new ClassNotFoundException();
	}
}
