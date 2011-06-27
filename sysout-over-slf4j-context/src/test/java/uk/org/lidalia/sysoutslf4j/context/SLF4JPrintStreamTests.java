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

package uk.org.lidalia.sysoutslf4j.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static uk.org.lidalia.test.Assert.shouldThrow;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Test;

public class SLF4JPrintStreamTests {
	
	private PrintStream mockOriginalPrintStream = mock(PrintStream.class);
	private LoggerAppender mockDelegate = mock(LoggerAppender.class);
	private SLF4JPrintStream slf4jPrintStreamImpl = new SLF4JPrintStream(mockOriginalPrintStream, mockDelegate);
	
	@After
	public void noUnexpectedInteractionsWithMocks() {
		verifyNoMoreInteractions(mockOriginalPrintStream, mockDelegate);
	}

	@Test
	public void appendCharDelegatesToDelegatePrint() {
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append('a');

		verify(mockDelegate).append("a");
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printCharSeqDelegatesToDelegatePrint() {
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "expected");

		verify(mockDelegate).append("expected");
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printBoundedCharSeqDelegatesToDelegatePrint() {
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "1 expected 2", 2, 10);
		
		verify(mockDelegate).append("expected");
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void checkErrorReturnsFalseWithoutCallingOriginalPrintStream() {
		assertFalse(slf4jPrintStreamImpl.checkError());
	}
	
	@Test
	public void closeDelegatesToOriginalPrintStream() {
		slf4jPrintStreamImpl.close();

		verifyZeroInteractions(mockDelegate);
		verifyZeroInteractions(mockOriginalPrintStream);
	}
	
	@Test
	public void flushDelegatesToLoggerAppenderAppendAndLogWithEmptyString() {
		slf4jPrintStreamImpl.flush();
		verify(mockDelegate).log();
	}

	@Test
	public void printBooleanDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(true);
		verify(mockDelegate).append("true");
	}
	
	@Test
	public void printCharDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print('a');
		verify(mockDelegate).append("a");
	}
	
	@Test
	public void printCharArrayDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(new char[]{'a', 'b', 'c'});
		verify(mockDelegate).append("abc");
	}
	
	@Test
	public void printDoubleDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(2d);
		verify(mockDelegate).append("2.0");
	}
	
	@Test
	public void printFloatDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(2f);
		verify(mockDelegate).append("2.0");
	}
	
	@Test
	public void printIntDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(2);
		verify(mockDelegate).append("2");
	}
	
	@Test
	public void printLongDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print(2L);
		verify(mockDelegate).append("2");
	}

	@Test
	public void printObjectDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print((Object) "text");
		verify(mockDelegate).append("text");
	}

	@Test
	public void printNullObjectDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print((Object) null);
		verify(mockDelegate).append("null");
	}

	@Test
	public void printStringDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print("something");
		verify(mockDelegate).append("something");
	}

	@Test
	public void printNullStringDelegatesToDelegatePrint() {
		slf4jPrintStreamImpl.print((String) null);
		verify(mockDelegate).append("null");
	}

	@Test
	public void printlnDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println();
		verify(mockDelegate).appendAndLog("");
	}
	
	@Test
	public void printlnBooleanDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(true);
		verify(mockDelegate).appendAndLog("true");
	}
	
	@Test
	public void printlnCharDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println('a');
		verify(mockDelegate).appendAndLog("a");
	}
	
	@Test
	public void printlnCharArrayDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(new char[]{'a', 'b', 'c'});
		verify(mockDelegate).appendAndLog("abc");
	}
	
	@Test
	public void printlnDoubleDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(2d);
		verify(mockDelegate).appendAndLog("2.0");
	}
	
	@Test
	public void printlnFloatDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(2f);
		verify(mockDelegate).appendAndLog("2.0");
	}
	
	@Test
	public void printlnIntDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(2);
		verify(mockDelegate).appendAndLog("2");
	}
	
	@Test
	public void printlnLongDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println(2L);
		verify(mockDelegate).appendAndLog("2");
	}
	
	@Test
	public void printlnObjectDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println((Object) "text");
		verify(mockDelegate).appendAndLog("text");
	}
	
	@Test
	public void printlnNullObjectDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println((Object) null);
		verify(mockDelegate).appendAndLog("null");
	}
	
	@Test
	public void printlnStringDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println("something");
		verify(mockDelegate).appendAndLog("something");
	}
	
	@Test
	public void printlnNullStringDelegatesToDelegatePrintln() {
		slf4jPrintStreamImpl.println((String) null);
		verify(mockDelegate).appendAndLog("null");
	}
	
	@Test
	public void writeByteArrayDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		slf4jPrintStreamImpl.write(expected);
		verify(mockOriginalPrintStream).write(expected);
	}
	
	@Test
	public void writeByteArrayIntIntDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		slf4jPrintStreamImpl.write(expected, 1, 2);
		verify(mockOriginalPrintStream).write(expected, 1, 2);
	}
	
	@Test
	public void writeIntDelegatesToOriginalPrintStream() throws Exception {
		slf4jPrintStreamImpl.write(1);
		verify(mockOriginalPrintStream).write(1);
	}
	
	private static final String TEST_FORMAT = "My Birthday: %1$tc";
	private static final Calendar DATE = new GregorianCalendar(1995, Calendar.MAY, 23);
	
	@Test
	public void formatLocaleStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		PrintStream returned = slf4jPrintStreamImpl.format(Locale.JAPANESE, TEST_FORMAT, DATE);

		assertSame(slf4jPrintStreamImpl, returned);
		verify(mockDelegate).append(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
	}
		
	@Test
	public void formatStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		PrintStream returned = slf4jPrintStreamImpl.format(TEST_FORMAT, DATE);
		
		assertSame(slf4jPrintStreamImpl, returned);
		verify(mockDelegate).append(String.format(TEST_FORMAT, DATE));
	}
		
	@Test
	public void printfLocaleStringDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		PrintStream returned = slf4jPrintStreamImpl.printf(Locale.JAPANESE, TEST_FORMAT, DATE);

		assertSame(slf4jPrintStreamImpl, returned);
		verify(mockDelegate).append(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
	}
	
	@Test
	public void printfStringObjectArrayDelegatesToDelegatePrintWithCorrectlyFormattedString() {
		PrintStream returned = slf4jPrintStreamImpl.printf(TEST_FORMAT, DATE);

		assertSame(slf4jPrintStreamImpl, returned);
		verify(mockDelegate).append(String.format(TEST_FORMAT, DATE));
	}
	
	@Test
	public void setErrorThrowsUnsupportedOperationException() throws Throwable {
		UnsupportedOperationException exception = shouldThrow(UnsupportedOperationException.class, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				slf4jPrintStreamImpl.setError();
				return null;
			}
		});
		assertEquals("Setting an error on an SLF4JPrintStream does not make sense", exception.getMessage());
	}
}
