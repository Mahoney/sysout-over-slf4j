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

package uk.org.lidalia.sysoutslf4j.context.exceptionhandlers;

import java.io.PrintStream;

import uk.org.lidalia.slf4jext.Level;

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
     * {@link ExceptionHandlingStrategy} for the new System.out and System.err print streams.<br>
     *
     * It is called with the {@link Level} of the SLF4JPrintStream and the original System output
     * PrintStream that is being replaced, so that ExceptionHandlingStrategies can have direct access to
     * the console if they wish.
     *
     * @param logLevel The {@link Level} of the parent PrintStream (by default INFO for System.out
     *             and ERROR for System.err)
     * @param originalPrintStream The original System output PrintStream providing direct access to the console
     * @return an {@link ExceptionHandlingStrategy} that will be called with each line of an Exception that is
     *             printed using Throwable.printStacktrace() or Throwable.printStacktrace(System.out)
     */
    ExceptionHandlingStrategy makeExceptionHandlingStrategy(Level logLevel, PrintStream originalPrintStream);

}
