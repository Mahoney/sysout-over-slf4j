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

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PerContextPrintStream.class, PerContextSystemOutput.class})
public class SLF4JSystemOutputTests extends SysOutOverSLF4JTestCase {

	@Test
	public void isSLF4JPrintStreamReturnsFalseWhenSystemOutIsSLF4JPrintStream() {
		assertFalse(PerContextSystemOutput.OUT.isPerContextPrintStream());
		assertFalse(PerContextSystemOutput.ERR.isPerContextPrintStream());
	}

	@Test
	public void isSLF4JPrintStreamReturnsTrueWhenSystemOutIsSLF4JPrintStream() {
		System.setOut(new PerContextPrintStream(System.out, null));
		assertTrue(PerContextSystemOutput.OUT.isPerContextPrintStream());
		
		System.setErr(new PerContextPrintStream(System.err, null));
		assertTrue(PerContextSystemOutput.ERR.isPerContextPrintStream());
	}

	@Test
	public void restoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream() {
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(original, output.get());
	}

	@Test
	public void restoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream() {
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PrintStream other = new PrintStream(new ByteArrayOutputStream());
		output.set(other);
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(other, output.get());
	}

	@Test
	public void restoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream() {
		assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
		assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		output.set(new PerContextPrintStream(output.get(), null));
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(original, output.get());
	}

	@Test
	public void getOriginalPrintStreamReturnsOriginalWhenOutputIsOriginalPrintStream() {
		assertSame(SystemOutput.OUT.get(), PerContextSystemOutput.OUT.getOriginalPrintStream());
		assertSame(SystemOutput.ERR.get(), PerContextSystemOutput.ERR.getOriginalPrintStream());
	}

	@Test
	public void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream() {
		getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
		getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PrintStream other = new PrintStream(new ByteArrayOutputStream());
		output.set(other);
		assertSame(other, slf4jOutput.getOriginalPrintStream());
	}

	@Test
	public void getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream() {
		getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
		getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}
	
	private void getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		output.set(new PerContextPrintStream(output.get(), null));
		assertSame(original, slf4jOutput.getOriginalPrintStream());
	}

	@Test
	public void registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggerAppenderIfSysOutIsNotSLF4JPrintStream() throws Exception {
		registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggerAppenderIfOutputIsNotSLF4JPrintStream(
				SystemOutput.OUT, PerContextSystemOutput.OUT);
	}

	@Test
	public void registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggerAppenderIfSysErrIsNotSLF4JPrintStream() throws Exception {
		registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggerAppenderIfOutputIsNotSLF4JPrintStream(
				SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggerAppenderIfOutputIsNotSLF4JPrintStream(
			final SystemOutput output, final PerContextSystemOutput slf4jOutput) throws Exception {
		PrintStream original = output.get();
		SimplePrintStream logAppenderMock = createMock(SimplePrintStream.class);
		PerContextPrintStream slf4jPrintStreamMock = createMock(PerContextPrintStream.class);
		expectNew(PerContextPrintStream.class, same(original), isA(PerContextPrintStreamDelegate.class)).andReturn(slf4jPrintStreamMock);
		slf4jPrintStreamMock.registerSimplePrintStream(logAppenderMock);
		replayAll();

		slf4jOutput.registerSimplePrintStream(logAppenderMock);
		assertSame(slf4jPrintStreamMock, output.get());
	}

	@Test
	public void registerLoggerAppenderRegistersLoggerAppenderIfSystemOutIsSLF4JPrintStream() {
		registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
	}

	@Test
	public void registerLoggerAppenderRegistersLoggerAppenderIfSystemErrIsSLF4JPrintStream() {
		registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PerContextPrintStream slf4jPrintStreamMock = createMock(PerContextPrintStream.class);
		SimplePrintStream logAppenderMock = createMock(SimplePrintStream.class);
		slf4jPrintStreamMock.registerSimplePrintStream(logAppenderMock);
		replayAll();
		
		output.set(slf4jPrintStreamMock);
		slf4jOutput.registerSimplePrintStream(logAppenderMock);
	}

	@Test
	public void deregisterLoggerAppenderDoesNothingIfOutputIsNotSLF4JPrintStream() {
		PerContextSystemOutput.OUT.deregisterSimplePrintStream();
		PerContextSystemOutput.ERR.deregisterSimplePrintStream();
		// Nothing happens
	}

	@Test
	public void deregisterLoggerAppenderDeregistersAppenderIfSystemOutIsSLF4JPrintStream() {
		deregisterLoggerAppenderDeregistersAppenderIfOutputIsSLF4JPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
	}

	@Test
	public void deregisterLoggerAppenderDeregistersAppenderIfSystemErrIsSLF4JPrintStream() {
		deregisterLoggerAppenderDeregistersAppenderIfOutputIsSLF4JPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
	}

	private void deregisterLoggerAppenderDeregistersAppenderIfOutputIsSLF4JPrintStream(SystemOutput output, PerContextSystemOutput slf4jOutput) {
		PerContextPrintStream slf4jPrintStreamMock = createMock(PerContextPrintStream.class);
		slf4jPrintStreamMock.deregisterSimplePrintStream();
		replayAll();
		
		output.set(slf4jPrintStreamMock);
		slf4jOutput.deregisterSimplePrintStream();
	}
}
