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

import static org.junit.Assert.assertEquals;
import static uk.org.lidalia.testutils.Assert.assertNotInstantiable;
import static uk.org.lidalia.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.system.StringUtils;

public class TestStringUtils extends SysOutOverSLF4JTestCase {

	@Test
	public void stripEndStripsEnd() {
		assertEquals("hello wo", StringUtils.stripEnd("hello world", "elders"));
	}
	
	@Test
	public void stripEndReturnsEmptyStringIfEmptyStringPassedIn() {
		assertEquals("", StringUtils.stripEnd("", "irrelevant"));
	}
	
	@Test
	public void stripEndReturnsInputIfEmptyStripCharsPassedIn() {
		assertEquals("hello", StringUtils.stripEnd("hello", ""));
	}
	
	@Test
	public void stripEndThrowsNullPointerExceptionIfInputIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.stripEnd(null, "irrelevant");
				return null;
			}
		});
	}
	
	@Test
	public void stripEndThrowsNullPointerExceptionIfStripCharsIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.stripEnd("irrelevant", null);
				return null;
			}
		});
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(StringUtils.class);
	}
}
