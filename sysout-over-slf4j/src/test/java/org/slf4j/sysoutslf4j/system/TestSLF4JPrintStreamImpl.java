package org.slf4j.sysoutslf4j.system;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.common.LoggerAppender;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerAppenderProxy.class })
public class TestSLF4JPrintStreamImpl {
	
	private SLF4JPrintStreamDelegater mockDelegater = createStrictMock(SLF4JPrintStreamDelegater.class);
	private PrintStream mockOriginalPrintStream = createStrictMock(PrintStream.class);
	private SLF4JPrintStreamImpl slf4jPrintStreamImpl = new SLF4JPrintStreamImpl(mockOriginalPrintStream, mockDelegater);
	
	@After
	public void verifyMocks() {
		verifyAll();
	}
	
	@Test
	public void appendCharDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("a");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append('a');
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printCharSeqDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("expected");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "expected");
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void printBoundedCharSeqDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("expected");
		replayAll();
		PrintStream returnedPrintStream = slf4jPrintStreamImpl.append((CharSequence) "1 expected 2", 2, 10);
		assertSame(slf4jPrintStreamImpl, returnedPrintStream);
	}
	
	@Test
	public void checkErrorDelegatesToOriginalPrintStream() {
		expect(mockOriginalPrintStream.checkError()).andReturn(true);
		replayAll();
		slf4jPrintStreamImpl.checkError();
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
	public void printBooleanDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("true");
		replayAll();
		slf4jPrintStreamImpl.print(true);
	}
	
	@Test
	public void printCharDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("a");
		replayAll();
		slf4jPrintStreamImpl.print('a');
	}
	
	@Test
	public void printCharArrayDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("abc");
		replayAll();
		slf4jPrintStreamImpl.print(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void printDoubleDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2.0");
		replayAll();
		slf4jPrintStreamImpl.print(2d);
	}
	
	@Test
	public void printFloatDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2.0");
		replayAll();
		slf4jPrintStreamImpl.print(2f);
	}
	
	@Test
	public void printIntDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2");
		replayAll();
		slf4jPrintStreamImpl.print(2);
	}
	
	@Test
	public void printLongDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2");
		replayAll();
		slf4jPrintStreamImpl.print(2L);
	}
	
	@Test
	public void printObjectDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("text");
		replayAll();
		slf4jPrintStreamImpl.print((Object) "text");
	}
	
	@Test
	public void printNullObjectDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("null");
		replayAll();
		slf4jPrintStreamImpl.print((Object) null);
	}
	
	@Test
	public void printStringDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("something");
		replayAll();
		slf4jPrintStreamImpl.print("something");
	}
	
	@Test
	public void printNullStringDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("null");
		replayAll();
		slf4jPrintStreamImpl.print((String) null);
	}
	
	@Test
	public void printlnDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("");
		replayAll();
		slf4jPrintStreamImpl.println();
	}
	
	@Test
	public void printlnBooleanDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("true");
		replayAll();
		slf4jPrintStreamImpl.println(true);
	}
	
	@Test
	public void printlnCharDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("a");
		replayAll();
		slf4jPrintStreamImpl.println('a');
	}
	
	@Test
	public void printlnCharArrayDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("abc");
		replayAll();
		slf4jPrintStreamImpl.println(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void printlnDoubleDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2.0");
		replayAll();
		slf4jPrintStreamImpl.println(2d);
	}
	
	@Test
	public void printlnFloatDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2.0");
		replayAll();
		slf4jPrintStreamImpl.println(2f);
	}
	
	@Test
	public void printlnIntDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2");
		replayAll();
		slf4jPrintStreamImpl.println(2);
	}
	
	@Test
	public void printlnLongDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2");
		replayAll();
		slf4jPrintStreamImpl.println(2L);
	}
	
	@Test
	public void printlnObjectDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("text");
		replayAll();
		slf4jPrintStreamImpl.println((Object) "text");
	}
	
	@Test
	public void printlnNullObjectDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("null");
		replayAll();
		slf4jPrintStreamImpl.println((Object) null);
	}
	
	@Test
	public void printlnStringDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("something");
		replayAll();
		slf4jPrintStreamImpl.println("something");
	}
	
	@Test
	public void printlnNullStringDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln(null);
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
	public void formatLocaleStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.format(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
		
	@Test
	public void formatStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.format(TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
		
	@Test
	public void printfLocaleStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		replayAll();
		PrintStream returned = slf4jPrintStreamImpl.printf(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStreamImpl, returned);
	}
	
	@Test
	public void printfStringObjectArrayDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(TEST_FORMAT, DATE));
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
	public void registerLoggerAppenderDelegatesToDelegater() {
		LoggerAppender loggerAppender = createStrictMock(LoggerAppender.class);
		mockStatic(LoggerAppenderProxy.class);
		expect(LoggerAppenderProxy.wrap(loggerAppender)).andReturn(loggerAppender);
		mockDelegater.registerLoggerAppender(loggerAppender);
		replayAll();
		slf4jPrintStreamImpl.registerLoggerAppender(loggerAppender);
	}
}
