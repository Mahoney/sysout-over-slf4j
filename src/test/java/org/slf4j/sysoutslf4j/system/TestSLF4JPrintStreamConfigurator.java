package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.common.PrintStreamCoordinator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SLF4JPrintStreamConfigurator.class, SLF4JPrintStreamImpl.class })
public class TestSLF4JPrintStreamConfigurator extends SysOutOverSLF4JTestCase {
	
	private PrintStreamCoordinator configurator = new SLF4JPrintStreamConfigurator();
	
	@Test
	public void replaceSystemOutputsWithSLF4JPrintStreams() throws Exception {
		SLF4JPrintStreamImpl outSlf4jPrintStreamImpl = expectSLF4JPrintStreamToBeBuilt(System.out);
		SLF4JPrintStreamImpl errSlf4jPrintStreamImpl = expectSLF4JPrintStreamToBeBuilt(System.err);
		replayAll();
		
		configurator.replaceSystemOutputsWithSLF4JPrintStreams();
		assertEquals(outSlf4jPrintStreamImpl, System.out);
		assertEquals(errSlf4jPrintStreamImpl, System.err);
	}
	
	@Test
	public void restoreOriginalSystemOutputs() throws Exception {
		configurator.replaceSystemOutputsWithSLF4JPrintStreams();
		configurator.restoreOriginalSystemOutputs();
		assertEquals(SYS_OUT, System.out);
		assertEquals(SYS_ERR, System.err);
	}

	private SLF4JPrintStreamImpl expectSLF4JPrintStreamToBeBuilt(PrintStream originalPrintStream) throws Exception {
		LoggerAppenderStore loggerAppenderStoreMock = createMock(LoggerAppenderStore.class);
		expectNew(LoggerAppenderStore.class).andReturn(loggerAppenderStoreMock);
		SLF4JPrintStreamDelegate slf4jPrintStreamDelegateMock = createMock(SLF4JPrintStreamDelegate.class);
		expectNew(SLF4JPrintStreamDelegate.class, originalPrintStream, loggerAppenderStoreMock).andReturn(slf4jPrintStreamDelegateMock);
		SLF4JPrintStreamImpl slf4jPrintStreamImplMock = createMock(SLF4JPrintStreamImpl.class);
		expectNew(SLF4JPrintStreamImpl.class, originalPrintStream, slf4jPrintStreamDelegateMock).andReturn(slf4jPrintStreamImplMock);
		return slf4jPrintStreamImplMock;
	}
}
