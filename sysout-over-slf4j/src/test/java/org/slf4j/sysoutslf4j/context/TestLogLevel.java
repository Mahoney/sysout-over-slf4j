package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

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
