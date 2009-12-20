package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

public class TestLogLevel {
	
	private Logger mockLogger = createStrictMock(Logger.class);
	
	@Test
	public void testLogStringLevelTraceDelegatesToLogger() {
		mockLogger.trace("expected");
		replay(mockLogger);
		LogLevel.TRACE.log(mockLogger, "expected");
		verify(mockLogger);
	}
	
	@Test
	public void testLogStringLevelDebugDelegatesToLogger() {
		mockLogger.debug("expected");
		replay(mockLogger);
		LogLevel.DEBUG.log(mockLogger, "expected");
		verify(mockLogger);
	}
	
	@Test
	public void testLogStringLevelInfoDelegatesToLogger() {
		mockLogger.info("expected");
		replay(mockLogger);
		LogLevel.INFO.log(mockLogger, "expected");
		verify(mockLogger);
	}
	
	@Test
	public void testLogStringLevelWarnDelegatesToLogger() {
		mockLogger.warn("expected");
		replay(mockLogger);
		LogLevel.WARN.log(mockLogger, "expected");
		verify(mockLogger);
	}
	
	@Test
	public void testLogStringLevelErrorDelegatesToLogger() {
		mockLogger.error("expected");
		replay(mockLogger);
		LogLevel.ERROR.log(mockLogger, "expected");
		verify(mockLogger);
	}
	
	@Test
	public void testLogMarkerStringDelegatesToLogger() {
		Marker expectedMarker = makeMarker();
		mockLogger.error(expectedMarker, "expected");
		replay(mockLogger);
		LogLevel.ERROR.log(mockLogger, expectedMarker, "expected");
		verify(mockLogger);
	}

	private Marker makeMarker() {
		return (new BasicMarkerFactory()).getMarker("expected");
	}
	
}
