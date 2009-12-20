package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.context.LogLevel;

public interface ExceptionHandlingStrategyFactory {

	ExceptionHandlingStrategy makeExceptionHandlingStrategy(LogLevel logLevel, PrintStream originalPrintStream);
	
}
