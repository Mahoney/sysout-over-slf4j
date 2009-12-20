package org.slf4j.sysoutslf4j.context;

import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SysOutOverSLF4J.class })
public class TestSysOutOverSLF4JServletContextListener extends SysOutOverSLF4JTestCase {
	
	private SysOutOverSLF4JServletContextListener servletContextListener = new SysOutOverSLF4JServletContextListener();
	
	@Test
	public void testContextInitializedCallsSendSystemOutAndErrToSLF4J() {
		mockStatic(SysOutOverSLF4J.class);
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		replay(SysOutOverSLF4J.class);
		
		servletContextListener.contextInitialized(null);
		
		verify(SysOutOverSLF4J.class);
	}
	
	@Test
	public void testContextDestroyedDoesNothing() {
		servletContextListener.contextDestroyed(null);
		// How do we assert that nothing happened??!
	}
}
