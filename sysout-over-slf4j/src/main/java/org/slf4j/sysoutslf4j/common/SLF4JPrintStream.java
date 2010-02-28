package org.slf4j.sysoutslf4j.common;

import java.io.PrintStream;

public interface SLF4JPrintStream {

	void registerLoggerAppender(Object loggerAppender);

	PrintStream getOriginalPrintStream();

}
