/* 
 * Copyright (c) 2009-2012 Robert Elliot
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
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

public class LogLevelTests {
	
	private Logger mockLogger = mock(Logger.class);
	private Marker expectedMarker = (new BasicMarkerFactory()).getMarker("expected");
	
	@Test
	public void testLogStringLevelTraceDelegatesToLogger() {
		LogLevel.TRACE.log(mockLogger, "expected");
		verify(mockLogger).trace("expected");
	}
	
	@Test
	public void testLogStringLevelDebugDelegatesToLogger() {
		LogLevel.DEBUG.log(mockLogger, "expected");
		verify(mockLogger).debug("expected");
	}
	
	@Test
	public void testLogStringLevelInfoDelegatesToLogger() {
		LogLevel.INFO.log(mockLogger, "expected");
		verify(mockLogger).info("expected");
	}
	
	@Test
	public void testLogStringLevelWarnDelegatesToLogger() {
		LogLevel.WARN.log(mockLogger, "expected");
		verify(mockLogger).warn("expected");
	}
	
	@Test
	public void testLogStringLevelErrorDelegatesToLogger() {
		LogLevel.ERROR.log(mockLogger, "expected");
		verify(mockLogger).error("expected");
	}
	
	@Test
	public void testLogMarkerStringLevelTraceDelegatesToLogger() {
		LogLevel.TRACE.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger).trace(expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelDebugDelegatesToLogger() {
		LogLevel.DEBUG.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger).debug(expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelInfoDelegatesToLogger() {
		LogLevel.INFO.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger).info(expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelWarnDelegatesToLogger() {
		LogLevel.WARN.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger).warn(expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelErrorDelegatesToLogger() {
		LogLevel.ERROR.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger).error(expectedMarker, "expected");
	}
	
	@Test
	public void valueOf() {
		assertEquals(LogLevel.WARN, LogLevel.valueOf("WARN"));
	}
}
