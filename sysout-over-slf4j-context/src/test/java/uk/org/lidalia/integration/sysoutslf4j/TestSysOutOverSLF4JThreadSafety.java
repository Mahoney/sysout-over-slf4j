package uk.org.lidalia.integration.sysoutslf4j;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class TestSysOutOverSLF4JThreadSafety extends SysOutOverSLF4JTestCase {
	
	private Logger log = (Logger) LoggerFactory.getLogger(TestSysOutOverSLF4JThreadSafety.class);

	@Test
	public void sysoutOverSLF4JLogsCorrectlyInMultipleThreads() throws InterruptedException {
		
		log.setLevel(Level.INFO);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		final CountDownLatch start = new CountDownLatch(1);
		
		ExecutorService executor = Executors.newFixedThreadPool(60);
		int numberOfTimesToPrint = 10000;
		for (int i = 1; i <= numberOfTimesToPrint; i++) {
			final int count = i;
			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					start.await();
					System.out.println("logging " + count);
					return null;
				}
			});
		}
		
		for (int i = 1; i <= numberOfTimesToPrint; i++) {
			final int count = i;
			executor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					start.await();
					synchronized(System.out) {
						System.out.print("append1 ");
						System.out.print("append2 " + count);
						System.out.println();
					}
					return null;
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
