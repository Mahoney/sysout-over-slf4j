package org.slf4j.integration.sysoutslf4j;

import org.junit.After;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.context.SysOutOverSLF4J;

public abstract class SysOutOverSlf4jIntegrationTestCase extends SysOutOverSLF4JTestCase {
	
	protected void callSendSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
		Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
		Thread.currentThread().setContextClassLoader(classLoader);
		sysOutOverSLF4JClass.getMethod("sendSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	}
	
	protected void callStopSendingSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
		Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
		Thread.currentThread().setContextClassLoader(classLoader);
		sysOutOverSLF4JClass.getMethod("stopSendingSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	}
	
	@After
	public void removeSysoutSLF4JPrintStreams() {
		SysOutOverSLF4J.restoreOriginalSystemOutputs();
	}
}
