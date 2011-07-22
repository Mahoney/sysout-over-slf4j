import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class SysoutOverSlf4jWithNoSystemJarOnClasspathTests {

	private ListAppender<ILoggingEvent> appender;
	private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

	@Before
	public void resetAppender() {
		Logger log = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		log.detachAndStopAllAppenders();
		appender = new ListAppender<ILoggingEvent>();
		appender.setContext(loggerContext);
		appender.start();
		log.addAppender(appender);
	}

	@Test
	public void initialisingSysoutOverSlf4jWithoutSystemJarOnClasspathLogsErrorWithoutThrowingException() {
		loggerContext.getLogger(SysOutOverSLF4J.class).setLevel(Level.TRACE);
		
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		assertEquals("You do not have sysout-over-slf4j-system on your classpath - it is required.", appender.list.get(appender.list.size() - 1).getMessage());
	}
}
