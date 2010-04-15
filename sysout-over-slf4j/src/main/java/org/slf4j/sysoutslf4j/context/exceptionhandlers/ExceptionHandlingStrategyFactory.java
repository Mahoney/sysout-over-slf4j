package org.slf4j.sysoutslf4j.context.exceptionhandlers;

import java.io.PrintStream;

import org.slf4j.sysoutslf4j.context.LogLevel;

/**
 * <p>
 * Interface for factories that create ExceptionHandlingStrategies to allow users to provide their own mechanism
 * for handling the difficult problem of capturing the output of Throwable.printStacktrace() and turning it into
 * logging events.
 * </p>
 * 
 * <p>
 * The default implementation is {@link LogPerLineExceptionHandlingStrategyFactory} which returns an
 * {@link ExceptionHandlingStrategy} that simply logs each line of the stack trace as a separate logging event.
 * </p> 
 */
public interface ExceptionHandlingStrategyFactory {

	/**
	 * This method will be called twice for each context that is sent to SLF4J, once each to return an
	 * {@link ExceptionHandlingStrategy} for the new System.out and System.err print streams.<br/>
	 * 
	 * It is called with the {@link LogLevel} of the SLF4JPrintStream and the original System output
	 * PrintStream that is being replaced, so that ExceptionHandlingStrategies can have direct access to
	 * the console if they wish.
	 * 
	 * @param logLevel The {@link LogLevel} of the parent PrintStream (by default INFO for System.out
	 * 			and ERROR for System.err)
	 * @param originalPrintStream The original System output PrintStream providing direct access to the console
	 * @return an {@link ExceptionHandlingStrategy} that will be called with each line of an Exception that is
	 * 			printed using Throwable.printStacktrace() or Throwable.printStacktrace(System.out)
	 */
	ExceptionHandlingStrategy makeExceptionHandlingStrategy(LogLevel logLevel, PrintStream originalPrintStream);
	
}
