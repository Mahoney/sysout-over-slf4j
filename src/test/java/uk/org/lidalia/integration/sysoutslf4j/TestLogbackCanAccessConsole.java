package uk.org.lidalia.integration.sysoutslf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.OnConsoleStatusListener;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.common.SystemOutput;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import static org.junit.Assert.assertTrue;

public class TestLogbackCanAccessConsole extends SysOutOverSLF4JTestCase {

    @Test
    public void systemOutNoLongerGoesToSystemOut() throws Exception {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        final String message = "Something happened";
        new OnConsoleStatusListener().addStatusEvent(new InfoStatus(message, this));

        final String printedToSysOut = sysOutMock.toString();
        assertTrue(printedToSysOut+" should contain "+message, printedToSysOut.contains(message));
    }

    private OutputStream setUpMockSystemOutput(SystemOutput systemOutput) {
        OutputStream sysOutMock = new ByteArrayOutputStream();
        systemOutput.set(new PrintStream(sysOutMock));
        return sysOutMock;
    }
}
