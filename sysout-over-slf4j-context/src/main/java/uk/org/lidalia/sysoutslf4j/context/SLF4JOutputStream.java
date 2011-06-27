package uk.org.lidalia.sysoutslf4j.context;

import static uk.org.lidalia.sysoutslf4j.context.CallOrigin.getCallOrigin;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

public class SLF4JOutputStream extends ByteArrayOutputStream {
	
	private final LogLevel level;
	private final ExceptionHandlingStrategy exceptionHandlingStrategy;
	
	SLF4JOutputStream(final LogLevel level, final ExceptionHandlingStrategy exceptionHandlingStrategy) {
		super();
		this.level = level;
		this.exceptionHandlingStrategy = exceptionHandlingStrategy;
	}

	@Override
	public synchronized void flush() {
		String bufferAsString = new String(toByteArray());
		if (bufferAsString.endsWith("\n")) {
			String valueToLog = StringUtils.stripEnd(bufferAsString, "\r\n");
			log(valueToLog);
			reset();
		}
	}

	private void log(final String logStatement) {
		final CallOrigin callOrigin = getCallOrigin();
		final Logger log = LoggerFactory.getLogger(callOrigin.getClassName());
		if (callOrigin.isPrintingStackTrace()) {
			exceptionHandlingStrategy.handleExceptionLine(logStatement, log);
		} else {
			exceptionHandlingStrategy.notifyNotStackTrace();
			level.log(log, logStatement);
		}
	}
}
