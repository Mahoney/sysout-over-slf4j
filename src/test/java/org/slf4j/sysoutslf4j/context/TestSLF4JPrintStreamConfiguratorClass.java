package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.slf4j.testutils.Assert.assertNotInstantiable;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;
import org.slf4j.testutils.SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath;
import org.slf4j.testutils.SimpleClassloader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SLF4JPrintStreamConfiguratorClass.class, ClassLoaderUtils.class})
public class TestSLF4JPrintStreamConfiguratorClass extends SysOutOverSLF4JTestCase {

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
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsLocallyLoadedClassWhenUnableToDeriveJarUrl() {
		ClassLoader systemClassLoader = new SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath();
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		
		mockStatic(ClassLoaderUtils.class);
		expect(ClassLoaderUtils.getJarURL(SLF4JPrintStreamConfigurator.class)).andThrow(new NullPointerException());
		
		replay(ClassLoader.class, ClassLoaderUtils.class);
		
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(SLF4JPrintStreamConfigurator.class, configuratorClass);
	}
	
	@Test
	public void getSlf4jPrintStreamConfiguratorClassReturnsLocallyLoadedClassWhenUnableToAddToSystemClassPath() {
		SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath systemClassLoader = createPartialMock(SystemClassLoaderWithoutSysoutOverSLF4JOnClassPath.class, "addURL");
		URL jarUrl = ClassLoaderUtils.getJarURL(SLF4JPrintStreamConfigurator.class);
		systemClassLoader.addURL(jarUrl);
		expectLastCall().andThrow(new SecurityException());
		
		mockStatic(ClassLoader.class);
		expect(ClassLoader.getSystemClassLoader()).andStubReturn(systemClassLoader);
		
		replay(ClassLoader.class, systemClassLoader);
		
		Class<?> configuratorClass = SLF4JPrintStreamConfiguratorClass.getSlf4jPrintStreamConfiguratorClass();
		assertSame(SLF4JPrintStreamConfigurator.class, configuratorClass);
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(SLF4JPrintStreamConfiguratorClass.class);
	}
}
