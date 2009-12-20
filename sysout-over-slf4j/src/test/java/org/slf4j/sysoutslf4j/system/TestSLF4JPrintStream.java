package org.slf4j.sysoutslf4j.system;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertSame;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;

public class TestSLF4JPrintStream {
	
	private SLF4JPrintStreamDelegater mockDelegater = createStrictMock(SLF4JPrintStreamDelegater.class);
	private PrintStream mockOriginalPrintStream = createStrictMock(PrintStream.class);
	private SLF4JPrintStream slf4jPrintStream = new SLF4JPrintStream(mockOriginalPrintStream, mockDelegater);
	
	private void useMocks() {
		replay(mockDelegater);
		replay(mockOriginalPrintStream);
	}
	
	@After
	public void verifyMocks() {
		verify(mockDelegater);
		verify(mockOriginalPrintStream);
	}
	
	@Test
	public void testAppendCharDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("a");
		useMocks();
		PrintStream returnedPrintStream = slf4jPrintStream.append('a');
		assertSame(slf4jPrintStream, returnedPrintStream);
	}
	
	@Test
	public void testPrintCharSeqDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("expected");
		useMocks();
		PrintStream returnedPrintStream = slf4jPrintStream.append((CharSequence) "expected");
		assertSame(slf4jPrintStream, returnedPrintStream);
	}
	
	@Test
	public void testPrintBoundedCharSeqDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("expected");
		useMocks();
		PrintStream returnedPrintStream = slf4jPrintStream.append((CharSequence) "1 expected 2", 2, 10);
		assertSame(slf4jPrintStream, returnedPrintStream);
	}
	
	@Test
	public void testCheckErrorDelegatesToOriginalPrintStream() {
		expect(mockOriginalPrintStream.checkError()).andReturn(true);
		useMocks();
		slf4jPrintStream.checkError();
	}
	
	@Test
	public void testCloseDelegatesToOriginalPrintStream() {
		mockOriginalPrintStream.close();
		useMocks();
		slf4jPrintStream.close();
	}
	
	@Test
	public void testFlushDelegatesToOriginalPrintStream() {
		mockOriginalPrintStream.flush();
		useMocks();
		slf4jPrintStream.flush();
	}
	
	@Test
	public void testPrintBooleanDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("true");
		useMocks();
		slf4jPrintStream.print(true);
	}
	
	@Test
	public void testPrintCharDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("a");
		useMocks();
		slf4jPrintStream.print('a');
	}
	
	@Test
	public void testPrintCharArrayDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("abc");
		useMocks();
		slf4jPrintStream.print(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void testPrintDoubleDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2.0");
		useMocks();
		slf4jPrintStream.print(2d);
	}
	
	@Test
	public void testPrintFloatDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2.0");
		useMocks();
		slf4jPrintStream.print(2f);
	}
	
	@Test
	public void testPrintIntDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2");
		useMocks();
		slf4jPrintStream.print(2);
	}
	
	@Test
	public void testPrintLongDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("2");
		useMocks();
		slf4jPrintStream.print(2L);
	}
	
	@Test
	public void testPrintObjectDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("text");
		useMocks();
		slf4jPrintStream.print((Object) "text");
	}
	
	@Test
	public void testPrintNullObjectDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("null");
		useMocks();
		slf4jPrintStream.print((Object) null);
	}
	
	@Test
	public void testPrintStringDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("something");
		useMocks();
		slf4jPrintStream.print("something");
	}
	
	@Test
	public void testPrintNullStringDelegatesToDelegaterPrint() {
		mockDelegater.delegatePrint("null");
		useMocks();
		slf4jPrintStream.print((String) null);
	}
	
	@Test
	public void testPrintlnDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("");
		useMocks();
		slf4jPrintStream.println();
	}
	
	@Test
	public void testPrintlnBooleanDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("true");
		useMocks();
		slf4jPrintStream.println(true);
	}
	
	@Test
	public void testPrintlnCharDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("a");
		useMocks();
		slf4jPrintStream.println('a');
	}
	
	@Test
	public void testPrintlnCharArrayDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("abc");
		useMocks();
		slf4jPrintStream.println(new char[]{'a', 'b', 'c'});
	}
	
	@Test
	public void testPrintlnDoubleDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2.0");
		useMocks();
		slf4jPrintStream.println(2d);
	}
	
	@Test
	public void testPrintlnFloatDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2.0");
		useMocks();
		slf4jPrintStream.println(2f);
	}
	
	@Test
	public void testPrintlnIntDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2");
		useMocks();
		slf4jPrintStream.println(2);
	}
	
	@Test
	public void testPrintlnLongDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("2");
		useMocks();
		slf4jPrintStream.println(2L);
	}
	
	@Test
	public void testPrintlnObjectDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("text");
		useMocks();
		slf4jPrintStream.println((Object) "text");
	}
	
	@Test
	public void testPrintlnNullObjectDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("null");
		useMocks();
		slf4jPrintStream.println((Object) null);
	}
	
	@Test
	public void testPrintlnStringDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln("something");
		useMocks();
		slf4jPrintStream.println("something");
	}
	
	@Test
	public void testPrintlnNullStringDelegatesToDelegaterPrintln() {
		mockDelegater.delegatePrintln(null);
		useMocks();
		slf4jPrintStream.println((String) null);
	}
	
	@Test
	public void testWriteByteArrayDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		mockOriginalPrintStream.write(expected);
		useMocks();
		slf4jPrintStream.write(expected);
	}
	
	@Test
	public void testWriteByteArrayIntIntDelegatesToOriginalPrintStream() throws Exception {
		byte[] expected = new byte[] { 0, 1, 2 };
		mockOriginalPrintStream.write(expected, 1, 2);
		useMocks();
		slf4jPrintStream.write(expected, 1, 2);
	}
	
	@Test
	public void testWriteIntDelegatesToOriginalPrintStream() throws Exception {
		mockOriginalPrintStream.write(1);
		useMocks();
		slf4jPrintStream.write(1);
	}
	
	private static final String TEST_FORMAT = "My Birthday: %1$tc";
	private static final Calendar DATE = new GregorianCalendar(1995, Calendar.MAY, 23);
	
	@Test
	public void testFormatLocaleStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		useMocks();
		PrintStream returned = slf4jPrintStream.format(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStream, returned);
	}
		
	@Test
	public void testFormatStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(TEST_FORMAT, DATE));
		useMocks();
		PrintStream returned = slf4jPrintStream.format(TEST_FORMAT, DATE);
		assertSame(slf4jPrintStream, returned);
	}
		
	@Test
	public void testPrintfLocaleStringDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(Locale.JAPANESE, TEST_FORMAT, DATE));
		useMocks();
		PrintStream returned = slf4jPrintStream.printf(Locale.JAPANESE, TEST_FORMAT, DATE);
		assertSame(slf4jPrintStream, returned);
	}
	
	@Test
	public void testPrintfStringObjectArrayDelegatesToDelegaterPrintWithCorrectlyFormattedString() {
		mockDelegater.delegatePrint(String.format(TEST_FORMAT, DATE));
		useMocks();
		PrintStream returned = slf4jPrintStream.printf(TEST_FORMAT, DATE);
		assertSame(slf4jPrintStream, returned);
	}
	
	@Test
	public void testSetErrorThrowsUnsupportedOperationException() {
		mockOriginalPrintStream.println("WARNING - calling setError on SLFJPrintStream does nothing");
		useMocks();
		slf4jPrintStream.setError();
	}
}
