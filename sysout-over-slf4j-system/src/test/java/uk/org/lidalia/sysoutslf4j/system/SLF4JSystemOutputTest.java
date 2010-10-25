package uk.org.lidalia.sysoutslf4j.system;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Proxy;

import org.junit.Test;

import uk.org.lidalia.testutils.NoOpInvocationHandler;

public class SLF4JSystemOutputTest {

	@Test
	public void synch() throws Exception {
		Thread changeToSLF4J = new Thread() {
			@Override
			public void run() {
				LoggerAppender loggerAppender = (LoggerAppender) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {LoggerAppender.class}, NoOpInvocationHandler.INSTANCE);
				SLF4JSystemOutput.OUT.registerLoggerAppender(loggerAppender);
				SLF4JSystemOutput.OUT.deregisterLoggerAppender();
			}
		};
		
		Thread changeSysOutAnotherWay = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					System.setOut(new PrintStream("/Users/Robert/tmp/out.txt"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		changeToSLF4J.start();
		changeSysOutAnotherWay.start();
		changeToSLF4J.join();
		changeSysOutAnotherWay.join();
	}
}
