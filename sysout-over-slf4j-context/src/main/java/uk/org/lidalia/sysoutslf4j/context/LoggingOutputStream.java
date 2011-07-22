package uk.org.lidalia.sysoutslf4j.context;

import static uk.org.lidalia.sysoutslf4j.context.CallOrigin.getCallOrigin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

public class LoggingOutputStream extends ByteArrayOutputStream {
	
	private final LogLevel level;
	private final ExceptionHandlingStrategy exceptionHandlingStrategy;
	private final PrintStream originalPrintStream;
	private final LoggingSystemRegister loggingSystemRegister;
	
	LoggingOutputStream(final LogLevel level, final ExceptionHandlingStrategy exceptionHandlingStrategy,
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
			if (bufferAsString.contains("\n")) {
				log(callOrigin, bufferAsString);
			}
		}
	}

	private void writeToOriginalPrintStream() throws IOException {
		exceptionHandlingStrategy.notifyNotStackTrace();
		writeTo(originalPrintStream);
		originalPrintStream.flush();
		reset();
	}

	private void log(final CallOrigin callOrigin, String bufferAsString) {
		String valueToLog = StringUtils.stripEnd(bufferAsString, "\r\n");
		final Logger log = LoggerFactory.getLogger(callOrigin.getClassName());
		if (callOrigin.isPrintingStackTrace()) {
			exceptionHandlingStrategy.handleExceptionLine(valueToLog, log);
		} else {
			exceptionHandlingStrategy.notifyNotStackTrace();
			level.log(log, valueToLog);
		}
		reset();
	}
}
