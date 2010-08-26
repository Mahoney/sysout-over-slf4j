package org.slf4j.sysoutslf4j.context;

import static org.slf4j.sysoutslf4j.context.ClassLoaderUtils.getJarURL;
import static org.slf4j.sysoutslf4j.context.ClassLoaderUtils.loadClass;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamConfigurator;

class SLF4JPrintStreamConfiguratorClass {
	
	private static final String LINE_END = System.getProperty("line.separator");
	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	static Class<?> getSlf4jPrintStreamConfiguratorClass() {
		Class<?> slf4jPrintStreamConfiguratorClass = getConfiguratorClassFromSLF4JPrintStreamClassLoader();
		if (slf4jPrintStreamConfiguratorClass == null) {
			slf4jPrintStreamConfiguratorClass = getConfiguratorClassFromSystemClassLoader();
		}
		if (slf4jPrintStreamConfiguratorClass == null) {
			slf4jPrintStreamConfiguratorClass = addConfiguratorClassToSystemClassLoaderAndGet();
		}
		if (slf4jPrintStreamConfiguratorClass == null) {
			slf4jPrintStreamConfiguratorClass = getConfiguratorClassFromCurrentClassLoader();
		}
		return slf4jPrintStreamConfiguratorClass;
	}
	
	private static Class<?> getConfiguratorClassFromSLF4JPrintStreamClassLoader() {
		final Class<?> configuratorClass;
		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
			final ClassLoader classLoader = System.out.getClass().getClassLoader();
			configuratorClass = loadClass(classLoader, SLF4JPrintStreamConfigurator.class);
		} else {
			configuratorClass = null;
		}
		return configuratorClass;
	}

	private static Class<?> getConfiguratorClassFromSystemClassLoader() {
		Class<?> configuratorClass = null;
		try {
			configuratorClass = ClassLoader.getSystemClassLoader().loadClass(SLF4JPrintStreamConfigurator.class.getName());
		} catch (Exception e) {
			LOG.debug("failed to load " + SLF4JPrintStreamConfigurator.class.getName() + " from system class loader due to " + e);
		}
		return configuratorClass;
	}

	private static Class<?> addConfiguratorClassToSystemClassLoaderAndGet() {
		Class<?> configuratorClass = null;
		try {
			final URL jarUrl = getJarURL(SLF4JPrintStreamConfigurator.class);
			ReflectionUtils.invokeMethod("addURL", ClassLoader.getSystemClassLoader(), URL.class, jarUrl);
			configuratorClass = ClassLoader.getSystemClassLoader().loadClass(SLF4JPrintStreamConfigurator.class.getName());
		} catch (Exception exception) {
			reportFailureToAvoidClassLoaderLeak(exception);
		}
		return configuratorClass;
	}

	private static void reportFailureToAvoidClassLoaderLeak(final Exception exception) {
		LOG.warn("Unable to force sysout-over-slf4j jar url into system class loader and " +
				"then load class [" + SLF4JPrintStreamConfigurator.class + "] from the system class loader." + LINE_END +
				"Unfortunately it is not possible to set up Sysout over SLF4J on this system without introducing " +
				"a class loader memory leak." + LINE_END +
				"If you never need to discard the current class loader [" + Thread.currentThread().getContextClassLoader() + "] " +
				"this will not be a problem and you can suppress this warning." + LINE_END +
				"If you wish to avoid a class loader memory leak you can place sysout-over-slf4j.jar on the system classpath " +
				"IN ADDITION TO (*not* instead of) the local context's classpath", exception);
	}

	private static Class<SLF4JPrintStreamConfigurator> getConfiguratorClassFromCurrentClassLoader() {
		return SLF4JPrintStreamConfigurator.class;
	}
	
	private SLF4JPrintStreamConfiguratorClass() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
