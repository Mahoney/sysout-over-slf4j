package uk.org.lidalia.sysoutslf4j.context.jul;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.LoggingMessages;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import static com.google.common.collect.Iterables.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsoleHandlerTests extends SysOutOverSLF4JTestCase {

    @Test
    public void appenderStillPrintsToSystemOut() {

        ByteArrayOutputStream outputStreamBytes = systemOutOutputStream();

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        Logger log = Logger.getLogger("");
        for (Handler handler : log.getHandlers()) {
            log.removeHandler(handler);
        }
        log.addHandler(new ConsoleHandler());
        log.info("some log text");

        String outString = new String(outputStreamBytes.toByteArray());

        assertTrue(outString.contains("some log text"));

        Collection<TestLogger> allLoggers = TestLoggerFactory.getAllTestLoggers().values();
        assertFalse(any(allLoggers, new Predicate<TestLogger>() {
            @Override
            public boolean apply(TestLogger testLogger) {
                return any(testLogger.getLoggingEvents(), new Predicate<LoggingEvent>() {
                    @Override
                    public boolean apply(LoggingEvent loggingEvent) {
                        return loggingEvent.getMessage().contains(LoggingMessages.PERFORMANCE_WARNING);
                    }
                });
            }
        }));
    }

    ByteArrayOutputStream systemOutOutputStream() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream newSystemOut = new PrintStream(bytes, true);
        SystemOutput.ERR.set(newSystemOut);
        return bytes;
    }
}
