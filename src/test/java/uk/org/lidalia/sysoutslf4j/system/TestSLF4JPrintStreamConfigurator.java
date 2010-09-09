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

package uk.org.lidalia.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.PrintStreamCoordinator;
import uk.org.lidalia.sysoutslf4j.system.LoggerAppenderStore;
import uk.org.lidalia.sysoutslf4j.system.PrintStreamCoordinatorImpl;
import uk.org.lidalia.sysoutslf4j.system.SLF4JPrintStreamDelegate;
import uk.org.lidalia.sysoutslf4j.system.SLF4JPrintStreamImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PrintStreamCoordinatorImpl.class, SLF4JPrintStreamImpl.class })
public class TestSLF4JPrintStreamConfigurator extends SysOutOverSLF4JTestCase {
	
	private PrintStreamCoordinator configurator = new PrintStreamCoordinatorImpl();
	
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
