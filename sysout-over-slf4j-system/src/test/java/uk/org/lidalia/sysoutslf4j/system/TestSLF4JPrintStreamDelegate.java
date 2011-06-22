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

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.system.SimplePrintStreamStore;
import uk.org.lidalia.sysoutslf4j.system.PerContextPrintStreamDelegate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PrintStream.class })
public class TestSLF4JPrintStreamDelegate {

	private PrintStream originalPrintStreamMock = createMock(PrintStream.class);
	private SimplePrintStreamStore loggerAppenderStoreMock = createMock(SimplePrintStreamStore.class);
	private PerContextPrintStreamDelegate delegate = new PerContextPrintStreamDelegate(originalPrintStreamMock, loggerAppenderStoreMock);
	private SimplePrintStream loggerAppenderMock = createMock(SimplePrintStream.class);
	
	@After
	public void verifyMocks() {
		verifyAll();
	}

	@Test
	public void registerLoggerAppenderAddsLoggerAppenderToStore() {
		loggerAppenderStoreMock.put(loggerAppenderMock);
		replayAll();
		
		delegate.registerSimplePrintStream(loggerAppenderMock);
	}
	
	@Test
	public void deregisterLoggerAppenderRemovesLoggerAppenderToStore() {
		loggerAppenderStoreMock.remove();
		replayAll();
		
		delegate.deregisterSimplePrintStream();
	}
	
	@Test
	public void delegatePrintlnUsesOriginalPrintStreamIfNoLoggerAppenderForClassLoader() {
		expect(loggerAppenderStoreMock.get()).andReturn(null);
		originalPrintStreamMock.println("the message");
		replayAll();
		
		delegate.delegatePrintln("the message");
	}
	
	@Test
	public void delegatePrintlnCallsLoggerAppenderAppendAndLog() {
		expect(loggerAppenderStoreMock.get()).andReturn(loggerAppenderMock);
		loggerAppenderMock.println("the message");
		replayAll();
		
		delegate.delegatePrintln("the message");
	}
	
	@Test
	public void delegatePrintUsesOriginalPrintStreamIfNoLoggerAppenderForClassLoader() {
		expect(loggerAppenderStoreMock.get()).andReturn(null);
		originalPrintStreamMock.print("the message");
		replayAll();
		
		delegate.delegatePrint("the message");
	}
	
	@Test
	public void delegatePrintCallsLoggerAppenderAppendAndLogWhenMessageEndsWithUnixLineBreak() {
		expect(loggerAppenderStoreMock.get()).andReturn(loggerAppenderMock);
		loggerAppenderMock.println("the message");
		replayAll();
		
		delegate.delegatePrint("the message\n");
	}

	@Test
	public void delegatePrintCallsLoggerAppenderAppendAndLogWhenMessageEndsWithWindowsLineBreak() {
		expect(loggerAppenderStoreMock.get()).andReturn(loggerAppenderMock);
		loggerAppenderMock.println("the message");
		replayAll();
		
		delegate.delegatePrint("the message\r\n");
	}
	
	@Test
	public void delegatePrintCallsLoggerAppenderAppendWhenMessageDoesNotEndWithLineBreak() {
		expect(loggerAppenderStoreMock.get()).andReturn(loggerAppenderMock);
		loggerAppenderMock.print("the message");
		replayAll();
		
		delegate.delegatePrint("the message");
	}
}
