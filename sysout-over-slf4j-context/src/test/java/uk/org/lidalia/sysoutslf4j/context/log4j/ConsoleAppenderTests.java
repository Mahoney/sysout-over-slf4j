package uk.org.lidalia.sysoutslf4j.context.log4j;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.base.Predicate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.google.common.collect.Iterables.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsoleAppenderTests extends SysOutOverSLF4JTestCase {
	
	@Test
	public void appenderStillPrintsToSystemOut() {
		
		ByteArrayOutputStream outputStreamBytes = systemOutOutputStream();
		
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).setLevel(ch.qos.logback.classic.Level.WARN);

		Logger log = Logger.getRootLogger();
		log.setLevel(Level.INFO);
		log.removeAllAppenders();
        log.addAppender(new ConsoleAppender(new SimpleLayout()));
		
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

	ByteArrayOutputStream systemOutOutputStream() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream newSystemOut = new PrintStream(bytes, true);
		SystemOutput.OUT.set(newSystemOut);
		return bytes;
	}
}
