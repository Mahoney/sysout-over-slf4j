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

import org.junit.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.slf4jext.Level;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

public class TestLogPerLineExceptionHandlingStrategyFactory extends SysOutOverSLF4JTestCase {

    private static final ExceptionHandlingStrategyFactory STRATEGY_FACTORY =
        LogPerLineExceptionHandlingStrategyFactory.getInstance();
    private static final String EXCEPTION_LINE = "an exception line";
    private static final Marker STACKTRACE = MarkerFactory.getMarker("stacktrace");

    private TestLogger log = TestLoggerFactory.getTestLogger(TestLogPerLineExceptionHandlingStrategyFactory.class);

    @Test
    public void testHandleExceptionLineDelegatesToLoggerAtInfoLevel() {
        ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(Level.INFO, null);
        strategy.handleExceptionLine(EXCEPTION_LINE, log);
        assertEquals(asList(info(STACKTRACE, EXCEPTION_LINE)), log.getLoggingEvents());
    }

    @Test
    public void testHandleExceptionLineDelegatesToLoggerAtErrorLevel() {
        ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(Level.ERROR, null);
        strategy.handleExceptionLine(EXCEPTION_LINE, log);
        assertEquals(asList(error(STACKTRACE, EXCEPTION_LINE)), log.getLoggingEvents());
    }
}
