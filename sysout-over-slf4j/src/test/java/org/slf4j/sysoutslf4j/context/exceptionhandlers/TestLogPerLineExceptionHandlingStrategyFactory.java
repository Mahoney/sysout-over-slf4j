package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.context.LogLevel;
import org.slf4j.testutils.SLF4JTestCase;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class TestLogPerLineExceptionHandlingStrategyFactory extends SLF4JTestCase {
	
	private static final ExceptionHandlingStrategyFactory STRATEGY_FACTORY =
		LogPerLineExceptionHandlingStrategyFactory.getInstance();
	private static final String EXCEPTION_LINE = "Hello World";
	
	private Logger log = (Logger) LoggerFactory.getLogger(TestLogPerLineExceptionHandlingStrategyFactory.class);
	
	@Before
	public void setUp() {
		log.setLevel(Level.INFO);
	}
	
	@Test
	public void testHandleExceptionLineDelegatesToLoggerAtInfoLevel() {
		ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(LogLevel.INFO, null);
		strategy.handleExceptionLine(EXCEPTION_LINE, log);
		assertCorrectLoggingEvent(Level.INFO);
	}
	
	@Test
	public void testHandleExceptionLineDelegatesToLoggerAtErrorLevel() {
		ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(LogLevel.ERROR, null);
		strategy.handleExceptionLine(EXCEPTION_LINE, log);
		assertCorrectLoggingEvent(Level.ERROR);
	}

	private void assertCorrectLoggingEvent(Level logbackLevel) {
		assertEquals(1, appender.list.size());
		assertEquals(logbackLevel, appender.list.get(0).getLevel());
		assertEquals(EXCEPTION_LINE, appender.list.get(0).getMessage());
	}
}
