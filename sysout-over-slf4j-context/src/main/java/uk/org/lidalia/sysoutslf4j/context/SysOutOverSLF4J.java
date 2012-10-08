/*
 * Copyright (c) 2009-2012 Robert Elliot
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.context;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.lang.RunnableCallable;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.system.PerContextSystemOutput;

import static uk.org.lidalia.lang.Exceptions.throwUnchecked;

/**
 * Public interface to the sysout-over-slf4j module. Provides all methods necessary to manage wrapping the existing
 * {@link System#out} and {@link System#err} {@link java.io.PrintStream}s with
 * custom {@link uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream}s that redirect to a logging system
 * via SLF4J.
 *
 * Synchronizes on System.class to ensure proper synchronization even if this class is loaded
 * by multiple classloaders.
 *
 * @author Robert Elliot
 * @see uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream
 */
public final class SysOutOverSLF4J {

    private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);
    private static final LoggingSystemRegister LOGGING_SYSTEM_REGISTER = new LoggingSystemRegister();

    /**
     * If they have not previously been wrapped, wraps the System.out and
     * System.err PrintStreams in an {@link uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream} and registers
     * SLF4J for the current context.<br/>
     * Can be called any number of times, and is synchronized on System.class.<br/>
     * Uses the {@link uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.LogPerLineExceptionHandlingStrategyFactory}
     * for handling printlns coming from Throwable.printStackTrace().<br/>
     * Logs at info level for System.out and at error level for System.err.
     */
    public static void sendSystemOutAndErrToSLF4J() throws SysOutOverSLF4JSystemNotPresentException {
        sendSystemOutAndErrToSLF4J(Level.INFO, Level.ERROR);
    }

    /**
     * If they have not previously been wrapped, wraps the System.out and
     * System.err PrintStreams in an {@link uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream} and registers
     * SLF4J for the current context's classloader.<br/>
     * Can be called any number of times, and is synchronized on System.class.<br/>
     * Uses the LogPerLineExceptionHandlingStrategy for handling printlns coming from
     * Throwable.printStackTrace().
     *
     * @param outLevel The SLF4J {@link Level} at which calls to System.out should be logged
     * @param errLevel The SLF4J {@link Level} at which calls to System.err should be logged
     */
    public static void sendSystemOutAndErrToSLF4J(final Level outLevel, final Level errLevel) throws SysOutOverSLF4JSystemNotPresentException {
        sendSystemOutAndErrToSLF4J(outLevel, errLevel, LogPerLineExceptionHandlingStrategyFactory.getInstance());
    }

    /**
     * If they have not previously been wrapped, wraps the System.out and
     * System.err PrintStreams in an {@link uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream} and registers
     * SLF4J for the current context's classloader.<br/>
     * Can be called any number of times, and is synchronized on System.class.
     * Logs at info level for System.out and at error level for System.err.
     *
     * @param exceptionHandlingStrategyFactory
     *             The {@link uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory}
     *             for creating strategies for handling printlns coming from Throwable.printStackTrace()
     */
    public static void sendSystemOutAndErrToSLF4J(final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws SysOutOverSLF4JSystemNotPresentException {
        sendSystemOutAndErrToSLF4J(Level.INFO, Level.ERROR, exceptionHandlingStrategyFactory);
    }

    /**
     * If they have not previously been wrapped, wraps the System.out and
     * System.err PrintStreams in an {@link uk.org.lidalia.sysoutslf4j.system.PerContextPrintStream} and registers
     * SLF4J for the current context's classloader.<br/>
     * Can be called any number of times, and is synchronized on System.class.<br/>
     *
     * @param outLevel The SLF4J {@link Level} at which calls to System.out should be logged
     * @param errLevel The SLF4J {@link Level} at which calls to System.err should be logged
     * @param exceptionHandlingStrategyFactory
     *             The {@link uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory}
     *             for creating strategies for handling printlns coming from Throwable.printStackTrace()
     */
    public static void sendSystemOutAndErrToSLF4J(final Level outLevel, final Level errLevel,
            final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) throws SysOutOverSLF4JSystemNotPresentException {
        synchronized (System.class) {
            doWithSystemClasses(new RunnableCallable() {
                @Override
                public void run2() {
                    registerNewLoggerAppender(exceptionHandlingStrategyFactory, PerContextSystemOutput.OUT, outLevel);
                    registerNewLoggerAppender(exceptionHandlingStrategyFactory, PerContextSystemOutput.ERR, errLevel);
                    LOG.info("Redirected System.out and System.err to SLF4J for this context");
                }
            });
        }
    }

    private static void registerNewLoggerAppender(
            final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory,
            final PerContextSystemOutput perContextSystemOutput, final Level logLevel) {
        final PrintStream slf4jPrintStream = buildPrintStream(exceptionHandlingStrategyFactory, perContextSystemOutput, logLevel);
        ReferenceHolder.preventGarbageCollectionForLifeOfClassLoader(slf4jPrintStream);
        perContextSystemOutput.registerPrintStreamForThisContext(slf4jPrintStream);
    }

    private static PrintStream buildPrintStream(
            final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory,
            final PerContextSystemOutput perContextSystemOutput, final Level logLevel) {
        final PrintStream originalPrintStream = perContextSystemOutput.getOriginalPrintStream();
        final ExceptionHandlingStrategy exceptionHandlingStrategy = exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(logLevel, originalPrintStream);
        return new PrintStream(new LoggingOutputStream(logLevel, exceptionHandlingStrategy, originalPrintStream, LOGGING_SYSTEM_REGISTER), true);
    }

    /**
     * Stops using SLF4J for calls to System.out and System.err in the current context.
     * Has no effect on any other contexts that may be using sysout-over-slf4j.<br/>
     * Can be called any number of times, and is synchronized on System.class.
     */
    public static void stopSendingSystemOutAndErrToSLF4J() throws SysOutOverSLF4JSystemNotPresentException {
        synchronized (System.class) {
            doWithSystemClasses(new RunnableCallable() {
                @Override
                public void run2() {
                    for (PerContextSystemOutput systemOutput : PerContextSystemOutput.values()) {
                        systemOutput.deregisterPrintStreamForThisContext();
                    }
                }
            });
        }
    }

    /**
     * If System.out and System.err have been redirected to SLF4J, restores the original PrintStreams
     * allowing direct access to the console again.<br/>
     * This will stop all contexts in the JVM from using sysout-over-slf4j. It is not necessary to call
     * {@link SysOutOverSLF4J#stopSendingSystemOutAndErrToSLF4J} as well as this method.
     * Can be called any number of times, and is synchronized on System.class.
     */
    public static void restoreOriginalSystemOutputs() throws SysOutOverSLF4JSystemNotPresentException {
        synchronized (System.class) {
            doWithSystemClasses(new RunnableCallable() {
                @Override
                public void run2() {
                    for (PerContextSystemOutput systemOutput : PerContextSystemOutput.values()) {
                        systemOutput.restoreOriginalPrintStream();
                    }
                }
            });
        }
    }

    public static boolean systemOutputsAreSLF4JPrintStreams() throws SysOutOverSLF4JSystemNotPresentException {
        return doWithSystemClasses(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return PerContextSystemOutput.OUT.isPerContextPrintStream();
            }
        });
    }

    private static <T> T doWithSystemClasses(Callable<T> callable) throws SysOutOverSLF4JSystemNotPresentException {
        try {
            return callable.call();
        } catch (NoClassDefFoundError error) {
            if (error.getMessage().contains("sysoutslf4j/system")) {
                throw new SysOutOverSLF4JSystemNotPresentException(error);
            } else {
                throw error;
            }
        } catch (Exception e) {
            return throwUnchecked(e, (T) null);
        }
    }

    /**
     * Registers a package as being a logging system and hence any calls to
     * System.out/err println from classes within it should be allowed through
     * to the original PrintStreams rather than redirected to SLF4J.
     *
     * @param packageName A package name e.g. org.someloggingsystem
     */
    public static void registerLoggingSystem(final String packageName) {
        LOGGING_SYSTEM_REGISTER.registerLoggingSystem(packageName);
    }

    /**
     * Unregisters a package as being a logging system and hence any calls to
     * System.out/err println from classes within it will be redirected to SLF4J.
     *
     * @param packageName A package name e.g. org.someloggingsystem
     */
    public static void unregisterLoggingSystem(final String packageName) {
        LOGGING_SYSTEM_REGISTER.unregisterLoggingSystem(packageName);
    }

    /**
     * Checks whether the given fully qualified class name is a member of a
     * package registered as being a logging system.
     *
     * @param className The fully qualifed name of the class which may be in
     *         a registered logging system
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
