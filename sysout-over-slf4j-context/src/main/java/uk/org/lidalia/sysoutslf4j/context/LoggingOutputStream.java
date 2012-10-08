package uk.org.lidalia.sysoutslf4j.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

import static uk.org.lidalia.sysoutslf4j.context.CallOrigin.getCallOrigin;

class LoggingOutputStream extends ByteArrayOutputStream {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggingOutputStream.class);

    private final Level level;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final PrintStream originalPrintStream;
    private final LoggingSystemRegister loggingSystemRegister;

    LoggingOutputStream(final Level level, final ExceptionHandlingStrategy exceptionHandlingStrategy,
            final PrintStream originalPrintStream, final LoggingSystemRegister loggingSystemRegister) {
        super();
        this.level = level;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.originalPrintStream = originalPrintStream;
        this.loggingSystemRegister = loggingSystemRegister;
    }

    @Override
    public synchronized void flush() throws IOException {
        final CallOrigin callOrigin = getCallOrigin(loggingSystemRegister);
        if (callOrigin.isInLoggingSystem()) {
            writeToOriginalPrintStream();
        } else {
            String bufferAsString = new String(toByteArray());
            if (bufferAsString.endsWith("\n")) {
                reset();
                log(callOrigin, bufferAsString);
            } else if (bufferAsString.contains("\n")) {
                reset();
                List<String> messages = Arrays.asList(bufferAsString.split("\n"));
                List<String> messagesToLog = messages.subList(0, messages.size() - 1);
                for (String messageToLog : messagesToLog) {
                    log(callOrigin, messageToLog);
                }
                String lastMessage = messages.get(messages.size() - 1);
                write(lastMessage.getBytes());
            }
        }
    }

    private void writeToOriginalPrintStream() throws IOException {
        exceptionHandlingStrategy.notifyNotStackTrace();
        warnAboutPerformance();
        writeTo(originalPrintStream);
        originalPrintStream.flush();
        reset();
    }

    private static final AtomicBoolean warned = new AtomicBoolean(false);

    private static void warnAboutPerformance() {
        if (warned.compareAndSet(false, true)) {
            log.warn(LoggingMessages.PERFORMANCE_WARNING);
        }
    }

    private void log(final CallOrigin callOrigin, String bufferAsString) {
        String valueToLog = StringUtils.stripEnd(bufferAsString, " \r\n");
        try {
            if (valueToLog.length() > 0) {
                if (callOrigin.isPrintingStackTrace()) {
                    exceptionHandlingStrategy.handleExceptionLine(valueToLog, org.slf4j.LoggerFactory.getLogger(callOrigin.getClassName()));
                } else {
                    exceptionHandlingStrategy.notifyNotStackTrace();
                    LoggerFactory.getLogger(callOrigin.getClassName()).log(level, valueToLog);
                }
            }
        } catch (StackOverflowError stackOverflowError) {
            throw new IllegalStateException("Logging system " + org.slf4j.LoggerFactory.getLogger(Object.class).getClass() +
                    " is sending data to the console - please register it by calling SysOutOverSLF4J.registerLoggingSystem. " +
                    "Original message: " + System.getProperty("line.separator") + valueToLog, stackOverflowError);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        String bufferAsString = StringUtils.stripEnd(new String(toByteArray()), " \r\n");
        if (bufferAsString.length() > 0) {
            Logger logger = LoggerFactory.getLogger(SysOutOverSLF4J.class);
            logger.log(level, bufferAsString);
        }
        reset();
    }
}
