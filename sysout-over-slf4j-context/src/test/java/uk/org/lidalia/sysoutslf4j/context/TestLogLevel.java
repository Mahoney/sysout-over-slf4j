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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

import uk.org.lidalia.sysoutslf4j.context.LogLevel;

public class TestLogLevel {
	
	private Logger mockLogger = createStrictMock(Logger.class);
	private Marker expectedMarker = makeMarker();
	
	@After
	public void verifyMocks() {
		verify(mockLogger);
	}
	
	@Test
	public void testLogStringLevelTraceDelegatesToLogger() {
		mockLogger.trace("expected");
		replay(mockLogger);
		LogLevel.TRACE.log(mockLogger, "expected");
	}
	
	@Test
	public void testLogStringLevelDebugDelegatesToLogger() {
		mockLogger.debug("expected");
		replay(mockLogger);
		LogLevel.DEBUG.log(mockLogger, "expected");
	}
	
	@Test
	public void testLogStringLevelInfoDelegatesToLogger() {
		mockLogger.info("expected");
		replay(mockLogger);
		LogLevel.INFO.log(mockLogger, "expected");
	}
	
	@Test
	public void testLogStringLevelWarnDelegatesToLogger() {
		mockLogger.warn("expected");
		replay(mockLogger);
		LogLevel.WARN.log(mockLogger, "expected");
	}
	
	@Test
	public void testLogStringLevelErrorDelegatesToLogger() {
		mockLogger.error("expected");
		replay(mockLogger);
		LogLevel.ERROR.log(mockLogger, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelTraceDelegatesToLogger() {
		mockLogger.trace(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.TRACE.log(mockLogger, expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelDebugDelegatesToLogger() {
		mockLogger.debug(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.DEBUG.log(mockLogger, expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelInfoDelegatesToLogger() {
		mockLogger.info(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.INFO.log(mockLogger, expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelWarnDelegatesToLogger() {
		mockLogger.warn(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.WARN.log(mockLogger, expectedMarker, "expected");
	}
	
	@Test
	public void testLogMarkerStringLevelErrorDelegatesToLogger() {
		mockLogger.error(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.ERROR.log(mockLogger, expectedMarker, "expected");
	}
	
	@Test
	public void valueOf() {
		assertEquals(LogLevel.WARN, LogLevel.valueOf("WARN"));
		replay(mockLogger);
	}

	private Marker makeMarker() {
		return (new BasicMarkerFactory()).getMarker("expected");
	}
	
}
