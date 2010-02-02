package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;
import org.slf4j.sysoutslf4j.context.LogLevel;

public class TestSystemOutput extends SysOutOverSLF4JTestCase {

	@Test
	public void SYSOUTGetReturnsSysout() {
		PrintStream actual = SystemOutput.SYSOUT.get();
		assertEquals(System.out, actual);
	}

	@Test
	public void SYSERRGetReturnsSyserr() {
		PrintStream actual = SystemOutput.SYSERR.get();
		assertEquals(System.err, actual);
	}

	@Test
	public void SYSOUTSetAltersSysout() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.SYSOUT.set(expected);
		assertEquals(expected, System.out);
	}

	@Test
	public void SYSERRSetAltersSyserr() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.SYSERR.set(expected);
		assertEquals(expected, System.err);
	}
	
	@Test
	public void SYSOUTLogLevelIsInfo() {
		assertSame(LogLevel.INFO, SystemOutput.SYSOUT.getLogLevel());
	}
	
	@Test
	public void SYSERRLogLevelIsError() {
		assertSame(LogLevel.ERROR, SystemOutput.SYSERR.getLogLevel());
	}
	
	@Test
	public void SYSOUTToString() {
		assertEquals("System.out", SystemOutput.SYSOUT.toString());
	}
	
	@Test
	public void SYSERRToString() {
		assertEquals("System.err", SystemOutput.SYSERR.toString());
	}
	
	@Test
	public void valueOf() {
		assertEquals(SystemOutput.SYSERR, SystemOutput.valueOf("SYSERR"));
	}
}
