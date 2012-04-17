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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class TestLogPerLineExceptionHandlingStrategyFactory extends SysOutOverSLF4JTestCase {

    private static final ExceptionHandlingStrategyFactory STRATEGY_FACTORY =
        LogPerLineExceptionHandlingStrategyFactory.getInstance();
    private static final String EXCEPTION_LINE = "Hello World";

    private Logger log = (Logger) LoggerFactory.getLogger(TestLogPerLineExceptionHandlingStrategyFactory.class);

    @Before
    public void setUp() {
        log.setLevel(Level.INFO);
    }

    @Test
    public void testHandleExceptionLineDelegatesToLoggerAtInfoLevel() {
        ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(LogLevel.INFO, null);
        strategy.handleExceptionLine(EXCEPTION_LINE, log);
        assertCorrectLoggingEvent(Level.INFO);
    }

    @Test
    public void testHandleExceptionLineDelegatesToLoggerAtErrorLevel() {
        ExceptionHandlingStrategy strategy = STRATEGY_FACTORY.makeExceptionHandlingStrategy(LogLevel.ERROR, null);
        strategy.handleExceptionLine(EXCEPTION_LINE, log);
        assertCorrectLoggingEvent(Level.ERROR);
    }

    private void assertCorrectLoggingEvent(Level logbackLevel) {
        assertEquals(1, appender.list.size());
        assertEquals(logbackLevel, appender.list.get(0).getLevel());
        assertEquals(EXCEPTION_LINE, appender.list.get(0).getMessage());
    }
}
