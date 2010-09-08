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

package org.slf4j.sysoutslf4j.context;

import static org.slf4j.sysoutslf4j.context.ClassLoaderUtils.getJarURL;
import static org.slf4j.sysoutslf4j.context.ClassLoaderUtils.loadClass;

import java.net.URL;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ExceptionUtils;
import org.slf4j.sysoutslf4j.common.PrintStreamCoordinator;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.system.PrintStreamCoordinatorImpl;

class PrintStreamCoordinatorFactory {
	
	private static final String LINE_END = System.getProperty("line.separator");
	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	static PrintStreamCoordinator createPrintStreamCoordinator() {
		Class<?> candidateCoordinatorClass = getConfiguratorClassFromSLF4JPrintStreamClassLoader();
		if (candidateCoordinatorClass == null) {
			candidateCoordinatorClass = getConfiguratorClassFromSystemClassLoader();
		}
		if (candidateCoordinatorClass == null) {
			candidateCoordinatorClass = addConfiguratorClassToSystemClassLoaderAndGet();
		}
		if (candidateCoordinatorClass == null) {
			candidateCoordinatorClass = getConfiguratorClassFromCurrentClassLoader();
		}
		return makeCoordinator(candidateCoordinatorClass);
	}

	private static PrintStreamCoordinator makeCoordinator(final Class<?> coordinatorClass) {
		return ExceptionUtils.doUnchecked(new Callable<PrintStreamCoordinator>() {
			@Override
			public PrintStreamCoordinator call() throws Exception {
				Object coordinator = coordinatorClass.newInstance();
				return ReflectionUtils.wrap(coordinator, PrintStreamCoordinator.class);
			}
		});
	}

	private static Class<?> getConfiguratorClassFromSLF4JPrintStreamClassLoader() {
		final Class<?> configuratorClass;
		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
			final ClassLoader classLoader = System.out.getClass().getClassLoader();
			configuratorClass = loadClass(classLoader, PrintStreamCoordinatorImpl.class);
		} else {
			configuratorClass = null;
		}
		return configuratorClass;
	}

	private static Class<?> getConfiguratorClassFromSystemClassLoader() {
		Class<?> configuratorClass = null;
		try {
			configuratorClass = ClassLoader.getSystemClassLoader().loadClass(PrintStreamCoordinatorImpl.class.getName());
		} catch (Exception e) {
			LOG.debug("failed to load [" + PrintStreamCoordinatorImpl.class + "] from system class loader due to " + e);
		}
		return configuratorClass;
	}

	private static Class<?> addConfiguratorClassToSystemClassLoaderAndGet() {
		Class<?> configuratorClass = null;
		try {
			final URL jarUrl = getJarURL(PrintStreamCoordinator.class);
			ReflectionUtils.invokeMethod("addURL", ClassLoader.getSystemClassLoader(), URL.class, jarUrl);
			configuratorClass = ClassLoader.getSystemClassLoader().loadClass(PrintStreamCoordinatorImpl.class.getName());
		} catch (Exception exception) {
			reportFailureToAvoidClassLoaderLeak(exception);
		}
		return configuratorClass;
	}

	private static void reportFailureToAvoidClassLoaderLeak(final Exception exception) {
		LOG.warn("Unable to force sysout-over-slf4j jar url into system class loader and " +
				"then load class [" + PrintStreamCoordinatorImpl.class + "] from the system class loader." + LINE_END +
				"Unfortunately it is not possible to set up Sysout over SLF4J on this system without introducing " +
				"a class loader memory leak." + LINE_END +
				"If you never need to discard the current class loader [" + Thread.currentThread().getContextClassLoader() + "] " +
				"this will not be a problem and you can suppress this warning." + LINE_END +
				"If you wish to avoid a class loader memory leak you can place sysout-over-slf4j.jar on the system classpath " +
				"IN ADDITION TO (*not* instead of) the local context's classpath", exception);
	}

	private static Class<PrintStreamCoordinatorImpl> getConfiguratorClassFromCurrentClassLoader() {
		return PrintStreamCoordinatorImpl.class;
	}
	
	private PrintStreamCoordinatorFactory() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
