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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.system.PerContextPrintStreamDelegate;
import uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream;

public class TestSLF4JPrintStream {
	
	private PerContextPrintStreamDelegate mockDelegate = createStrictMock(PerContextPrintStreamDelegate.class);
	private PrintStream mockOriginalPrintStream = createStrictMock(PrintStream.class);
	private PerContextPrintStream slf4jPrintStreamImpl = new PerContextPrintStream(mockOriginalPrintStream, mockDelegate);
	
	@After
	public void verifyMocks() {
		verifyAll();
	}
	
	@Test
	public void appendCharDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("a");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append('a');
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printCharSeqDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("expected");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "expected");
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printBoundedCharSeqDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("expected");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "1 expected 2", 2, 10);
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void checkErrorReturnsFalseWithoutCallingOriginalPrintStream() {
		replayAll();
		assertFalse(slf4jPrintStreamImpl.checkError());
	}
	
	@Test
	public void closeDelegatesToOriginalPrintStream() {
		mockOriginalPrintStream.close();
		replayAll();
		slf4jPrintStreamImpl.close();
	}
	
	@Test
	public void flushDelegatesToOriginalPrintStream() {
		mockOriginalPrintStream.flush();
		replayAll();
		slf4jPrintStreamImpl.flush();
	}
	
	@Test
	public void printBooleanDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("true");
		replayAll();
		slf4jPrintStreamImpl.print(true);
	}
	
	@Test
	public void printCharDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("a");
		replayAll();
		slf4jPrintStreamImpl.print('a');
	}
	
	@Test
	public void printCharArrayDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("abc");
		replayAll();
		slf4jPrintStreamImpl.print(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void printDoubleDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("2.0");
		replayAll();
		slf4jPrintStreamImpl.print(2d);
	}
	
	@Test
	public void printFloatDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("2.0");
		replayAll();
		slf4jPrintStreamImpl.print(2f);
	}
	
	@Test
	public void printIntDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("2");
		replayAll();
		slf4jPrintStreamImpl.print(2);
	}
	
	@Test
	public void printLongDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("2");
		replayAll();
		slf4jPrintStreamImpl.print(2L);
	}
	
	@Test
	public void printObjectDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("text");
		replayAll();
		slf4jPrintStreamImpl.print((Object) "text");
	}
	
	@Test
	public void printNullObjectDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("null");
		replayAll();
		slf4jPrintStreamImpl.print((Object) null);
	}
	
	@Test
	public void printStringDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("something");
		replayAll();
		slf4jPrintStreamImpl.print("something");
	}
	
	@Test
	public void printNullStringDelegatesToDelegatePrint() {
		mockDelegate.delegatePrint("null");
		replayAll();
		slf4jPrintStreamImpl.print((String) null);
	}
	
	@Test
	public void printlnDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("");
		replayAll();
		slf4jPrintStreamImpl.println();
	}
	
	@Test
	public void printlnBooleanDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("true");
		replayAll();
		slf4jPrintStreamImpl.println(true);
	}
	
	@Test
	public void printlnCharDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("a");
		replayAll();
		slf4jPrintStreamImpl.println('a');
	}
	
	@Test
	public void printlnCharArrayDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("abc");
		replayAll();
		slf4jPrintStreamImpl.println(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void printlnDoubleDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("2.0");
		replayAll();
		slf4jPrintStreamImpl.println(2d);
	}
	
	@Test
	public void printlnFloatDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("2.0");
		replayAll();
		slf4jPrintStreamImpl.println(2f);
	}
	
	@Test
	public void printlnIntDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("2");
		replayAll();
		slf4jPrintStreamImpl.println(2);
	}
	
	@Test
	public void printlnLongDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("2");
		replayAll();
		slf4jPrintStreamImpl.println(2L);
	}
	
	@Test
	public void printlnObjectDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("text");
		replayAll();
		slf4jPrintStreamImpl.println((Object) "text");
	}
	
	@Test
	public void printlnNullObjectDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("null");
		replayAll();
		slf4jPrintStreamImpl.println((Object) null);
	}
	
	@Test
	public void printlnStringDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln("something");
		replayAll();
		slf4jPrintStreamImpl.println("something");
	}
	
	@Test
	public void printlnNullStringDelegatesToDelegatePrintln() {
		mockDelegate.delegatePrintln(null);
		replayAll();
		slf4jPrintStreamImpl.println((String) null);
	}
	
	@Test
	public void writeByteArrayDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		mockOriginalPrintStream.write(expected);
		replayAll();
		slf4jPrintStreamImpl.write(expected);
	}
	
	@Test
	public void writeByteArrayIntIntDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		mockOriginalPrintStream.write(expected, 1, 2);
		replayAll();
		slf4jPrintStreamImpl.write(expected, 1, 2);
	}
	
	@Test
	public void writeIntDelegatesToOriginalPrintStream() throws Exception {
		mockOriginalPrintStream.write(1);
		replayAll();
		slf4jPrintStreamImpl.write(1);
	}
	
	private static final String TEST_FORMAT = "My Birthday: %1$tc";
	private static final Calendar DATE = new GregorianCalendar(1995, Calendar.MAY, 23);
	
	@Test
	public void formatLocaleStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		mockDelegate.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.format(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
		
	@Test
	public void formatStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		mockDelegate.delegatePrint(String.format(TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.format(TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
		
	@Test
	public void printfLocaleStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		mockDelegate.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.printf(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
	
	@Test
	public void printfStringObjectArrayDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		mockDelegate.delegatePrint(String.format(TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.printf(TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
	
	@Test
	public void setErrorThrowsUnsupportedOperationException() {
		mockOriginalPrintStream.println("WARNING - calling setError on SLFJPrintStream does nothing");
		replayAll();
		slf4jPrintStreamImpl.setError();
	}
	
	@Test
	public void getOriginalPrintStreamReturnsOriginalPrintStream() {
		assertSame(mockOriginalPrintStream, slf4jPrintStreamImpl.getOriginalPrintStream());
		replayAll();
	}
	
	@Test
	public void registerLoggerAppenderDelegatesToDelegate() {
		SimplePrintStream loggerAppender = createStrictMock(SimplePrintStream.class);
		mockDelegate.registerSimplePrintStream(loggerAppender);
		replayAll();
		slf4jPrintStreamImpl.registerSimplePrintStream(loggerAppender);
	}
	
	@Test
	public void deregisterLoggerAppenderDelegatesToDelegate() {
		mockDelegate.deregisterSimplePrintStream();
		replayAll();
		slf4jPrintStreamImpl.deregisterSimplePrintStream();
	}
}
