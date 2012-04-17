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

package uk.org.lidalia.sysoutslf4j.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

import uk.org.lidalia.lang.RunnableCallable;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import static org.junit.Assert.assertEquals;

public class TestSysOutOverSLF4JThreadSafety extends SysOutOverSLF4JTestCase {

    private Logger log = (Logger) LoggerFactory.getLogger(TestSysOutOverSLF4JThreadSafety.class);

    @Test
    public void sysoutOverSLF4JLogsCorrectlyInMultipleThreads() throws InterruptedException {

        log.setLevel(Level.INFO);
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        final CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(60);
        int numberOfTimesToPrint = 100;
        for (int i = 1; i <= numberOfTimesToPrint; i++) {
            final int count = i;
            executor.submit((Runnable) new RunnableCallable() {
                @Override
                public void run2() throws Exception {
                    start.await();
                    System.out.println("logging " + count);
                }
            });
        }

        for (int i = 1; i <= numberOfTimesToPrint; i++) {
            final int count = i;
            executor.submit((Runnable) new RunnableCallable() {
                @Override
                public void run2() throws Exception {
                    start.await();
                    synchronized(System.out) {
                        System.out.print("append1 ");
                        System.out.print("append2 " + count);
                        System.out.println();
                    }
                }
            });
        }

        start.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        assertEquals(numberOfTimesToPrint * 2, appender.list.size());

        List<String> messages = new ArrayList<String>();
        for (ILoggingEvent loggingEvent : appender.list) {
            messages.add(loggingEvent.getMessage());
        }
        Collections.sort(messages);

        List<String> expectedMessages = new ArrayList<String>();
        for (int i = 1; i <= numberOfTimesToPrint; i++) {
            expectedMessages.add("append1 append2 " + i);
        }
        for (int i = numberOfTimesToPrint + 1; i <= (numberOfTimesToPrint * 2); i++) {
            expectedMessages.add("logging " + (i - numberOfTimesToPrint));
        }
        Collections.sort(expectedMessages);
        assertEquals(expectedMessages, messages);

    }
}
