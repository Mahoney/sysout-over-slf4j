package org.slf4j.sysoutslf4j;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
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

}
