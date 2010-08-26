package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.slf4j.testutils.Assert.assertExpectedLoggingEvent;
import static org.slf4j.testutils.Assert.assertNotInstantiable;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;
import org.slf4j.testutils.SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath;
import org.slf4j.testutils.SimpleClassloader;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SLF4JPrintStreamConfiguratorClass.class, ClassLoaderUtils.class})
public class TestSLF4JPrintStreamConfiguratorClass extends SysOutOverSLF4JTestCase {

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
		Class<?> configuratorClass = ClassLoaderUtils.loadClass(contextClassLoader, SLF4JPrintStreamConfigurator.class);
		ReflectionUtils.invokeStaticMethod("replaceSystemOutputsWithSLF4JPrintStreams", configuratorClass);
		
		// Check the configurator class returned was loaded by our context class loader
		configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(contextClassLoader, configuratorClass.getClassLoader());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsClassFromSystemClassLoaderWhenOnClassPath() {
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(ClassLoader.getSystemClassLoader(), configuratorClass.getClassLoader());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassAddsClassToSystemClassLoaderAndReturnsItWhenNotOnClassPath() {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		replay(ClassLoader.class);
		
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		System.out.println(configuratorClass);
		ClassLoader classLoader = configuratorClass.getClassLoader();
		System.out.println(classLoader);
		assertSame(systemClassLoader, classLoader);
		
		assertEquals(1, appender.list.size());
		assertExpectedLoggingEvent(appender.list.get(0),
				"failed to load org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator from system class loader " +
				"due to java.lang.ClassNotFoundException: org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator",
				Level.DEBUG, SysOutOverSLF4J.class.getName());
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsLocallyLoadedClassWhenUnableToDeriveJarUrl() {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		
		mockStatic(ClassLoaderUtils.class);
		NullPointerException cause = new NullPointerException();
		expect(ClassLoaderUtils.getJarURL(SLF4JPrintStreamConfigurator.class)).andThrow(cause);
		
		replay(ClassLoader.class, ClassLoaderUtils.class);
		
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(SLF4JPrintStreamConfigurator.class, configuratorClass);
		
		assertEquals(2, appender.list.size());
		assertExpectedLoggingEvent(appender.list.get(0),
				"failed to load org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator from system class loader " +
				"due to java.lang.ClassNotFoundException: org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator",
				Level.DEBUG, SysOutOverSLF4J.class.getName());
		
		assertExpectedLoggingEvent(appender.list.get(1),
				expectedLeakWarning(),
				Level.WARN, SysOutOverSLF4J.class.getName(), cause);
	}

	private String expectedLeakWarning() {
		return "Unable to force sysout-over-slf4j jar url into system class loader and " +
		"then load class [class org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator] from the system class loader." + LINE_END +
		"Unfortunately it is not possible to set up Sysout over SLF4J on this system without introducing " +
		"a class loader memory leak." + LINE_END +
		"If you never need to discard the current class loader [" + Thread.currentThread().getContextClassLoader() + "] " +
		"this will not be a problem and you can suppress this warning." + LINE_END +
		"If you wish to avoid a class loader memory leak you can place sysout-over-slf4j.jar on the system classpath " +
		"IN ADDITION TO (*not* instead of) the local context's classpath";
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsLocallyLoadedClassWhenUnableToAddToSystemClassPath() {
		SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath systemClassLoader = createPartialMock(SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath.class, "addURL");
		URL jarUrl = ClassLoaderUtils.getJarURL(SLF4JPrintStreamConfigurator.class);
		systemClassLoader.addURL(jarUrl);
		SecurityException cause = new SecurityException();
		expectLastCall().andThrow(cause);
		
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		
		replay(ClassLoader.class, systemClassLoader);
		
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(SLF4JPrintStreamConfigurator.class, configuratorClass);
		
		assertExpectedLoggingEvent(appender.list.get(0),
				"failed to load org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator from system class loader " +
				"due to java.lang.ClassNotFoundException: org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator",
				Level.DEBUG, SysOutOverSLF4J.class.getName());
		
		assertExpectedLoggingEvent(appender.list.get(1),
				expectedLeakWarning(),
				Level.WARN, SysOutOverSLF4J.class.getName(), cause);
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(SLF4JPrintStreamConfiguratorClass.class);
	}
}
