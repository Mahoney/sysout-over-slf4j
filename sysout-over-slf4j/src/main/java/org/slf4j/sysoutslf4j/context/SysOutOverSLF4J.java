package org.slf4j.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import org.slf4j.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory;

/**
 *
 * Helper class that provides a method to wrap the existing
 * {@link System#out} and {@link System#err} {@link PrintStream}s with
 * custom {@link SLF4JPrintStreamImpl}s that redirect to a logging system
 * via SLF4J.
 *
 * Synchronizes on System.class to ensure proper synchronization even if this class is loaded
 * by multiple classloaders.
 *
 * @author Robert Elliot
 * @see SLF4JPrintStreamImpl
 */
public final class SysOutOverSLF4J {
	
	private static final LoggingSystemRegister LOGGING_SYSTEM_REGISTER = new LoggingSystemRegister();
	private static final SLF4JPrintStreamManager SLF4J_PRINT_STREAM_MANAGER = new SLF4JPrintStreamManager();
	
	static {
		final SysOutOverSLF4JInitialiser sysOutOverSLF4JInitialiser = new SysOutOverSLF4JInitialiser(LOGGING_SYSTEM_REGISTER);
		final Logger loggerImplementation = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		sysOutOverSLF4JInitialiser.initialise(loggerImplementation);
	}

	/**
	 * If they have not previously been wrapped, wraps the System.out and
	 * System.err PrintStreams in a LoggerPrintStream. <br/>
	 * Can be called any number of times, and is synchronized on System.class.
	 * Uses the LogPerLineExceptionHandlingStrategy for handling printlns coming from
	 * Throwable.printStackTrace()
	 */
	public static void sendSystemOutAndErrToSLF4J() {
		final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory =
			LogPerLineExceptionHandlingStrategyFactory.getInstance();
		sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
	}

	/**
	 * If they have not previously been wrapped, wraps the System.out and
	 * System.err PrintStreams in a LoggerPrintStream.
	 * Can be called any number of times, and is synchronized on System.class.
	 *
	 * @param exceptionHandlingStrategyFactory A factory for creating strategues for handling printlns coming from
	 *			Throwable.printStackTrace()
	 */
	public static void sendSystemOutAndErrToSLF4J(final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		SLF4J_PRINT_STREAM_MANAGER.sendSystemOutAndErrToSLF4J(exceptionHandlingStrategyFactory);
	}

	/**
	 * If System.out and System.err have been redirected to SLF4J, restores the originals
	 * allowing direct access to the console again.
	 * Can be called any number of times, and is synchronized on System.class.
	 */
	public static void sendSystemOutAndErrToOriginals() {
		SLF4J_PRINT_STREAM_MANAGER.sendSystemOutAndErrToOriginalsIfNecessary();
	}


	/**
	 * Registers a package as being a logging system and hence any calls to
	 * System.out/err println from classes within it should be allowed through
	 * to the original PrintStreams rather than redirected to SLF4J.
	 *
	 * @param packageName A package name e.g. org.apache.log4j
	 */
	public static void registerLoggingSystem(final String packageName) {
		LOGGING_SYSTEM_REGISTER.registerLoggingSystem(packageName);
	}

	/**
	 * Unregisters a package as being a logging system and hence any calls to
	 * System.out/err println from classes within it will be redirected to SLF4J.
	 *
	 * @param packageName A package name e.g. org.apache.log4j
	 */
	public static void unregisterLoggingSystem(final String packageName) {
		LOGGING_SYSTEM_REGISTER.unregisterLoggingSystem(packageName);
	}

	/**
	 * Checks whether the given fully qualified class name is a member of a
	 * package registered as being a logging system.
	 *
	 * @param className
	 * @return true if the class is in one of the registered logging system
	 *         packages, false otherwise
	 */
	public static boolean isInLoggingSystem(final String className) {
		return LOGGING_SYSTEM_REGISTER.isInLoggingSystem(className);
	}

	private SysOutOverSLF4J() {
		throw new UnsupportedOperationException("Not instantiable");
	}

}
