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

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import uk.org.lidalia.sysoutslf4j.context.LogLevel;

/**
 * Implementation of {@link LogPerLineExceptionHandlingStrategyFactory} which returns an
 * {@link ExceptionHandlingStrategy} that simply logs each line of the stack trace as a separate logging event.
 */
public final class LogPerLineExceptionHandlingStrategyFactory implements ExceptionHandlingStrategyFactory {

    private static final ExceptionHandlingStrategyFactory INSTANCE = new LogPerLineExceptionHandlingStrategyFactory();
    public static ExceptionHandlingStrategyFactory getInstance() {
        return INSTANCE;
    }

    private LogPerLineExceptionHandlingStrategyFactory() {
        super();
    }

    public ExceptionHandlingStrategy makeExceptionHandlingStrategy(
            final LogLevel logLevel, final PrintStream originalPrintStream) {
        return new LogPerLineExceptionHandlingStrategy(logLevel);
    }

    private static final class LogPerLineExceptionHandlingStrategy implements ExceptionHandlingStrategy {

        private static final Marker MARKER = MarkerFactory.getMarker("stacktrace");

        private final LogLevel logLevel;

        LogPerLineExceptionHandlingStrategy(final LogLevel logLevel) {
            super();
            this.logLevel = logLevel;
        }

        /**
         * This method is not used since nothing is buffered.
         */
        public void notifyNotStackTrace() {
            // Do nothing
        }

        public void handleExceptionLine(final String line, final Logger log) {
            logLevel.log(log, MARKER, line);
        }

    }
}
