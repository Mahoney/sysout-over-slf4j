package uk.org.lidalia.sysoutslf4j.context.logback;

import static com.google.common.collect.Iterables.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
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

        Logger log = initialiseALogbackLogger();
		log.info("some log text");

		String outString = new String(outputStreamBytes.toByteArray());

		assertTrue(outString.contains("some log text"));

        assertFalse(any(appender.list, new Predicate<ILoggingEvent>() {
            @Override
            public boolean apply(ILoggingEvent iLoggingEvent) {
                return iLoggingEvent.getMessage().contains("A logging system is sending data to the console");
            }
        }));
	}

    private Logger initialiseALogbackLogger() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger log = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        log.setLevel(Level.INFO);
        ConsoleAppender<ILoggingEvent> appender = initialiseConsoleAppender(lc);
        log.addAppender(appender);
        return log;
    }

    ConsoleAppender<ILoggingEvent> initialiseConsoleAppender(LoggerContext lc) {
		Encoder<ILoggingEvent> encoder = new EchoEncoder<ILoggingEvent>();
		encoder.start();
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
		appender.setContext(lc);
		appender.setEncoder(encoder);
		appender.start();
		return appender;
	}

	ByteArrayOutputStream systemOutOutputStream() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream newSystemOut = new PrintStream(bytes, true);
		SystemOutput.OUT.set(newSystemOut);
		return bytes;
	}
}
