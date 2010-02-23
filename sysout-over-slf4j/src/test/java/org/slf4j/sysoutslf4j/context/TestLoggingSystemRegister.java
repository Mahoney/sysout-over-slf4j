package org.slf4j.sysoutslf4j.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerFactory.class })
public class TestLoggingSystemRegister {

	private LoggingSystemRegister loggingSystemRegister;
	private Logger loggerMock;

	@Before
	public void setStaticMocks() {
		loggerMock = createMock(Logger.class);
		mockStatic(LoggerFactory.class);
		expect(LoggerFactory.getLogger(SysOutOverSLF4J.class)).andStubReturn(loggerMock);
		replay(LoggerFactory.class);
		loggingSystemRegister = new LoggingSystemRegister();
	}
	
	@Test
	public void isInLoggingSystemReturnsFalseWhenLoggingSystemNotRegistered() {
		assertFalse(loggingSystemRegister.isInLoggingSystem("some.package.SomeClass"));
	}

	@Test
	public void registerLoggingSystemRegistersALoggingSystem() {
		loggingSystemRegister.registerLoggingSystem("some.package");
		assertTrue(loggingSystemRegister.isInLoggingSystem("some.package.SomeClass"));
	}
	
	@Test
	public void unregisterLoggingSystemUnregistersALoggingSystem() {
		loggingSystemRegister.registerLoggingSystem("some.package");
		loggingSystemRegister.unregisterLoggingSystem("some.package");
		assertFalse(loggingSystemRegister.isInLoggingSystem("some.package.SomeClass"));
	}
	
	@Test
	public void registerLoggingSystemLogsThatItWasRegistered() {
		loggerMock.info("Package {} registered; all classes within it or subpackages of it will "
				+ "be allowed to print to System.out and System.err", "some.package");
		replay(loggerMock);
		loggingSystemRegister.registerLoggingSystem("some.package");
		verifyAll();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unregisterLoggingSystemLogsThatItWasUnregisteredIfLoggingSystemRegistered() {
		Whitebox.getInternalState(loggingSystemRegister, Set.class).add("some.package");
		loggerMock.info("Package {} unregistered; all classes within it or subpackages of it will "
				+ "have System.out and System.err redirected to SLF4J", "some.package");
		replay(loggerMock);
		loggingSystemRegister.unregisterLoggingSystem("some.package");
		verifyAll();
	}
	
	@Test
	public void unregisterLoggingSystemDoesNotLogIfLoggingSystemNotRegisterdPresent() {
		replay(loggerMock);
		loggingSystemRegister.unregisterLoggingSystem("some.package");
		verifyAll();
	}
}
