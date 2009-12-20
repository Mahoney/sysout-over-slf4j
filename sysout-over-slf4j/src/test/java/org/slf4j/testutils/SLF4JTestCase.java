package org.slf4j.testutils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public abstract class SLF4JTestCase {

	protected static final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	protected final Logger log = lc.getLogger(getClass());
	protected ListAppender<ILoggingEvent> appender;
	
	@BeforeClass
	public static void turnOffRootLogging() {
		LoggingUtils.turnOffRootLogging();
	}

	@Before
	public void resetAppender() {
		log.detachAndStopAllAppenders();
		appender = new ListAppender<ILoggingEvent>();
		appender.setContext(lc);
		appender.start();
		log.addAppender(appender);
	}
}
