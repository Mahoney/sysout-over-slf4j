package org.slf4j.sysoutslf4j.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class SysOutOverSLF4JServletContextListener implements ServletContextListener {

	public final void contextInitialized(final ServletContextEvent servletContextEvent) {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
	}

	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
	}
}
