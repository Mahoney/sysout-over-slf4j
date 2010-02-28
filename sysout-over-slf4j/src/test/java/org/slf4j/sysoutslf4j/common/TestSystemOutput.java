package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

public class TestSystemOutput extends SysOutOverSLF4JTestCase {

	@Test
	public void SYSOUTGetReturnsSysout() {
		PrintStream actual = SystemOutput.OUT.get();
		assertEquals(System.out, actual);
	}

	@Test
	public void SYSERRGetReturnsSyserr() {
		PrintStream actual = SystemOutput.ERR.get();
		assertEquals(System.err, actual);
	}

	@Test
	public void SYSOUTSetAltersSysout() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.OUT.set(expected);
		assertEquals(expected, System.out);
	}

	@Test
	public void SYSERRSetAltersSyserr() {
		PrintStream expected = new PrintStream(new ByteArrayOutputStream());
		SystemOutput.ERR.set(expected);
		assertEquals(expected, System.err);
	}
	
	@Test
	public void SYSOUTToString() {
		assertEquals("System.out", SystemOutput.OUT.toString());
	}
	
	@Test
	public void SYSERRToString() {
		assertEquals("System.err", SystemOutput.ERR.toString());
	}
	
	@Test
	public void valueOf() {
		assertEquals(SystemOutput.ERR, SystemOutput.valueOf("ERR"));
	}
}
