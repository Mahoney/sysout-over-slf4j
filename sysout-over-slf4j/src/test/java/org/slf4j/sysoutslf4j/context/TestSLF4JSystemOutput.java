package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

import java.io.PrintStream;
import java.lang.reflect.Constructor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.system.SLF4JPrintStreamImpl;
import org.slf4j.testutils.SimpleClassloader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SLF4JPrintStreamImpl.class, SLF4JSystemOutput.class })
public class TestSLF4JSystemOutput extends SysOutOverSLF4JTestCase {

	@Test
	public void OUTGetReturnsUnwrappedSLF4JPrintStreamIfInSameClassLoader() {
		SLF4JPrintStreamImpl expected = createMock(SLF4JPrintStreamImpl.class);
		System.setOut(expected);
		assertSame(expected, SLF4JSystemOutput.OUT.get());
	}

	@Test
	public void ERRGetReturnsUnwrappedSLF4JPrintStreamIfInSameClassLoader() {
		SLF4JPrintStreamImpl expected = createMock(SLF4JPrintStreamImpl.class);
		System.setErr(expected);
		assertSame(expected, SLF4JSystemOutput.ERR.get());
	}
	
	@Test
	public void OUTGetReturnsWrappedSLF4JPrintStreamIfInDifferentClassLoader() throws Exception {
		PrintStream realPrintStream = buildSlf4jPrintStreamInDifferentClassLoader();
		System.setOut(realPrintStream);
		SLF4JPrintStreamProxy expected = createMock(SLF4JPrintStreamProxy.class);
		expectNew(SLF4JPrintStreamProxy.class, realPrintStream).andReturn(expected);
		replay(SLF4JPrintStreamProxy.class);
		
		assertSame(expected, SLF4JSystemOutput.OUT.get());
	}

	@Test
	public void ERRGetReturnsWrappedSLF4JPrintStreamIfInDifferentClassLoader() throws Exception {
		PrintStream realPrintStream = buildSlf4jPrintStreamInDifferentClassLoader();
		System.setErr(realPrintStream);
		SLF4JPrintStreamProxy expected = createMock(SLF4JPrintStreamProxy.class);
		expectNew(SLF4JPrintStreamProxy.class, realPrintStream).andReturn(expected);
		replay(SLF4JPrintStreamProxy.class);
		
		assertSame(expected, SLF4JSystemOutput.ERR.get());
	}

	private PrintStream buildSlf4jPrintStreamInDifferentClassLoader() throws Exception {
		ClassLoader classLoader = new SimpleClassloader();
		Class<?> slf4jPrintStreamImplClass = classLoader.loadClass(SLF4JPrintStreamImpl.class.getName());
		Class<?> slf4jPrintStreamDelegaterClass = classLoader.loadClass("org.slf4j.sysoutslf4j.system.SLF4JPrintStreamDelegater");
		Constructor<?> constructor = slf4jPrintStreamImplClass.getDeclaredConstructor(PrintStream.class, slf4jPrintStreamDelegaterClass);
		constructor.setAccessible(true);
		return (PrintStream) constructor.newInstance(null, null);
	}
	
	@Test
	public void valueOf() {
		assertSame(SLF4JSystemOutput.ERR, SLF4JSystemOutput.valueOf("ERR"));
	}
	
}
