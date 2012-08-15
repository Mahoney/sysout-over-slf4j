package uk.org.lidalia.sysoutslf4j.context.jul;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.Test;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.LoggingMessages;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertTrue;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

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
        assertThat(outString, containsString("some log text"));

        Collection<LoggingEvent> allLoggingEvents = TestLoggerFactory.getAllLoggingEvents();
        assertThat(allLoggingEvents, not(hasItem(equalTo(warn(LoggingMessages.PERFORMANCE_WARNING)))));
    }

    ByteArrayOutputStream systemOutOutputStream() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream newSystemOut = new PrintStream(bytes, true);
        SystemOutput.ERR.set(newSystemOut);
        return bytes;
    }
}
