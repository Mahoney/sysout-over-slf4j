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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static uk.org.lidalia.testutils.Assert.assertNotInstantiable;
import static uk.org.lidalia.testutils.Assert.shouldThrow;

import java.net.URL;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.slf4j.Logger;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.WrappedCheckedException;
import uk.org.lidalia.sysoutslf4j.context.ClassLoaderUtils;

public class TestClassLoaderUtils extends SysOutOverSLF4JTestCase {
	
	@Test
	@SuppressWarnings("unchecked")
	public void loadClassLoadsClassFromClassloader() throws Exception {
		ClassLoader mockClassLoader = createMock(ClassLoader.class);
		Class expected = Object.class;
		expect(mockClassLoader.loadClass("java.lang.Object")).andReturn(expected);
		replay(mockClassLoader);
		
		assertEquals(expected, ClassLoaderUtils.loadClass(mockClassLoader, Object.class));
	}
	
	@Test
	public void loadClassMakesClassNotFoundExceptionUnchecked() throws Throwable {
		final ClassLoader mockClassLoader = createMock(ClassLoader.class);
		final ClassNotFoundException expected = new ClassNotFoundException();
		expect(mockClassLoader.loadClass("java.lang.Object")).andThrow(expected);
		replay(mockClassLoader);

		WrappedCheckedException exception = shouldThrow(WrappedCheckedException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ClassLoaderUtils.loadClass(mockClassLoader, Object.class);
				return null;
			}
		});
		assertSame(expected, exception.getCause());
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(ClassLoaderUtils.class);
	}
	
	@Test
	public void getJarUrlReturnsCredibleUrl() throws Exception {
		URL jarUrl = ClassLoaderUtils.getJarURL(Logger.class);
		assertTrue(jarUrl.toString().matches("^jar:file:.*slf4j-api.*\\.jar!\\/$"));
	}
	
	@Test
	public void exceptionBuildingJarUrlThrownAsWrappedCheckedException() throws Throwable {
		final RuntimeException exceptionGettingResource = new RuntimeException();
		Thread.currentThread().setContextClassLoader(new ClassLoader() {
			@Override
			public URL getResource(String name) {
				throw exceptionGettingResource;
			}
		});
		
		IllegalStateException exception = shouldThrow(IllegalStateException.class, new Callable<Void>() {
			public Void call() throws Exception {
				ClassLoaderUtils.getJarURL(Object.class);
				return null;
			}
		});
		
		assertSame(exceptionGettingResource, exception.getCause());
		assertEquals("Unable to build jar URL from class [" + Object.class + "]", exception.getMessage());
	}
}
