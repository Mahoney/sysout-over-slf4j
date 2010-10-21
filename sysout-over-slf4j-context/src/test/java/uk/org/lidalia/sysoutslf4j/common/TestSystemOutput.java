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

package uk.org.lidalia.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.SystemOutput;

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
