package uk.org.lidalia.sysoutslf4j.context;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;

public class ConsoleAppenderTests extends SysOutOverSLF4JTestCase {
	
	@Test
	public void appenderStillPrintsToSystemOut() {
		
		ByteArrayOutputStream outputStreamBytes = systemOutOutputStream();
		
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger log = lc.getLogger(Logger.ROOT_LOGGER_NAME);
		log.setLevel(Level.INFO);
		log.detachAndStopAllAppenders();
		ConsoleAppender<ILoggingEvent> appender = initialiseConsoleAppender(lc);
		log.addAppender(appender);
		
		log.info("some log text");
		
		String outString = new String(outputStreamBytes.toByteArray());
		
		assertTrue(outString.contains("some log text"));
	}

	public ConsoleAppender<ILoggingEvent> initialiseConsoleAppender(LoggerContext lc) {
		Encoder<ILoggingEvent> encoder = new EchoEncoder<ILoggingEvent>();
		encoder.start();
		ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
		appender.setContext(lc);
		appender.setEncoder(encoder);
		appender.start();
		return appender;
	}

	public ByteArrayOutputStream systemOutOutputStream() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream newSystemOut = new PrintStream(bytes, true);
		SystemOutput.OUT.set(newSystemOut);
		return bytes;
	}
}
