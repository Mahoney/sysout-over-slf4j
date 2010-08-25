package org.slf4j.sysoutslf4j;

import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.powermock.core.MockRepository;
import org.slf4j.sysoutslf4j.context.SysOutOverSLF4J;
import org.slf4j.testutils.SLF4JTestCase;

public abstract class SysOutOverSLF4JTestCase extends SLF4JTestCase {
	
	protected ClassLoader originalContextClassLoader;
	protected PrintStream SYS_OUT;
	protected PrintStream SYS_ERR;
	
	@Before
	public void storeOriginalSystemOutAndErr() {
		SYS_OUT = System.out;
		SYS_ERR = System.err;
	}
	
	@After
	public void restoreOriginalSystemOutAndErr() {
		System.setOut(SYS_OUT);
		System.setErr(SYS_ERR);
	}
	
	@Before
	public void storeOriginalContextClassLoader() {
		originalContextClassLoader = Thread.currentThread().getContextClassLoader();
	}

	@After
	public void restoreOriginalContextClassLoader() {
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	}

	@After
	public void verifyAllMocks() {
		for (Object classToReplayOrVerify : MockRepository.getObjectsToAutomaticallyReplayAndVerify()) {
			try {
				replay(classToReplayOrVerify);
			} catch (IllegalStateException ise) {
				// ignore
			}
		}
		verifyAll();
		resetAll();
	}
	
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
}
