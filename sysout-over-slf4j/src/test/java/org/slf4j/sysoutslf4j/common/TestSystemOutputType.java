package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

public class TestSystemOutputType extends SysOutOverSLF4JTestCase {

	@Test
	public void testSYSOUTGetReturnsSysout() {
		PrintStream actual = SystemOutput.SYSOUT.get();
		assertEquals(System.out, actual);
	}

	@Test
	public void testSYSERRGetReturnsSyserr() {
		PrintStream actual = SystemOutput.SYSERR.get();
		assertEquals(System.err, actual);
	}

	@Test
	public void testSYSOUTSetAltersSysout() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.SYSOUT.set(expected);
		assertEquals(expected, System.out);
	}

	@Test
	public void testSYSERRSetAltersSyserr() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.SYSERR.set(expected);
		assertEquals(expected, System.err);
	}
	
	@Test
	public void testSYSOUTToString() {
		assertEquals("System.out", SystemOutput.SYSOUT.toString());
	}
	
	@Test
	public void testSYSERRToString() {
		assertEquals("System.err", SystemOutput.SYSERR.toString());
	}
}
