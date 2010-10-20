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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static uk.org.lidalia.testutils.Assert.assertExpectedLoggingEvent;
import static uk.org.lidalia.testutils.Assert.assertNotInstantiable;

import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.PrintStreamCoordinator;
import uk.org.lidalia.sysoutslf4j.common.ProxyingInvocationHandler;
import uk.org.lidalia.sysoutslf4j.common.ReflectionUtils;
import uk.org.lidalia.sysoutslf4j.context.ClassLoaderUtils;
import uk.org.lidalia.sysoutslf4j.context.PrintStreamCoordinatorFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.PrintStreamCoordinatorImpl;
import uk.org.lidalia.testutils.SimpleClassloader;
import uk.org.lidalia.testutils.SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrintStreamCoordinatorFactory.class, ClassLoaderUtils.class})
public class PrintStreamCoordinatorFactoryTest extends SysOutOverSLF4JTestCase {

	private Logger log = (Logger) LoggerFactory.getLogger(SysOutOverSLF4J.class);
	private static final String LINE_END = System.getProperty("line.separator");

	@Before
	public void setUp() {
		log.setLevel(Level.TRACE);
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsClassFromSLF4JPrintStreamClassLoader() throws Exception {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		ClassLoader contextClassLoader = SimpleClassloader.make(systemClassLoader);
		
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		replay(ClassLoader.class);
		
		// Force the new PrintStream to be loaded by our context class loader
		Class<?> configuratorClass = ClassLoaderUtils.loadClass(contextClassLoader, PrintStreamCoordinatorImpl.class);
		ReflectionUtils.invokeMethod("replaceSystemOutputsWithSLF4JPrintStreams", configuratorClass.newInstance());
		
		// Check the coordinator class returned was loaded by our context class loader
		PrintStreamCoordinator proxyCoordinator = PrintStreamCoordinatorFactory.createPrintStreamCoordinator();
		ProxyingInvocationHandler invocationHandler = (ProxyingInvocationHandler) Proxy.getInvocationHandler(proxyCoordinator);
		assertSame(contextClassLoader, invocationHandler.getTarget().getClass().getClassLoader());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsClassFromSystemClassLoaderWhenOnClassPath() {
		PrintStreamCoordinator proxyCoordinator = PrintStreamCoordinatorFactory.createPrintStreamCoordinator();
		ProxyingInvocationHandler invocationHandler = (ProxyingInvocationHandler) Proxy.getInvocationHandler(proxyCoordinator);
		assertSame(ClassLoader.getSystemClassLoader(), invocationHandler.getTarget().getClass().getClassLoader());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsLocallyLoadedClassAndWarnsWhenNotOnSystemClasspath() {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		replay(ClassLoader.class, ClassLoaderUtils.class);
		
		PrintStreamCoordinator coordinator = PrintStreamCoordinatorFactory.createPrintStreamCoordinator();
		assertSame(PrintStreamCoordinatorImpl.class, coordinator.getClass());
		assertEquals(2, appender.list.size());
		assertExpectedLoggingEvent(appender.list.get(0),
				"failed to load [" + PrintStreamCoordinatorImpl.class + "] from system class loader " +
				"due to java.lang.ClassNotFoundException: " + PrintStreamCoordinatorImpl.class.getName(),
				Level.DEBUG, SysOutOverSLF4J.class.getName());
		assertExpectedLoggingEvent(appender.list.get(1),
				"Unfortunately it is not possible to set up Sysout over SLF4J on this system without introducing a class " +
				"loader memory leak." + LINE_END +
				"If you never need to discard the current class loader [" + 
				Thread.currentThread().getContextClassLoader() +
				"] this will not be a problem and you can suppress this warning." + LINE_END +
				"In the worst case discarding the current class loader may cause all subsequent attempts to print to " +
				"System.out or err to throw an exception.",
				Level.WARN, SysOutOverSLF4J.class.getName());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassDoesNotWarnWhenLocallyLoadedClassIsNotLoadedByContextClassLoader() {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		replay(ClassLoader.class, ClassLoaderUtils.class);
		
		Thread.currentThread().setContextClassLoader(new ClassLoader() {});
		PrintStreamCoordinator coordinator = PrintStreamCoordinatorFactory.createPrintStreamCoordinator();
		assertSame(PrintStreamCoordinatorImpl.class, coordinator.getClass());
		assertEquals(1, appender.list.size());
		assertExpectedLoggingEvent(appender.list.get(0),
				"failed to load [" + PrintStreamCoordinatorImpl.class + "] from system class loader " +
				"due to java.lang.ClassNotFoundException: " + PrintStreamCoordinatorImpl.class.getName(),
				Level.DEBUG, SysOutOverSLF4J.class.getName());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassDoesNotWarnWhenLocalClassLoaderIsSystemClassLoader() {
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(Thread.currentThread().getContextClassLoader());
		replay(ClassLoader.class, ClassLoaderUtils.class);
		
		PrintStreamCoordinator coordinator = PrintStreamCoordinatorFactory.createPrintStreamCoordinator();
		assertSame(PrintStreamCoordinatorImpl.class, coordinator.getClass());
		assertEquals(0, appender.list.size());
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(PrintStreamCoordinatorFactory.class);
	}
}
