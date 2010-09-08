/* 
 * Copyright (c) 2009-2010 Robert Elliot
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

package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.testutils.LoggingUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggingSystemRegister.class })
@SuppressStaticInitializationFor("org.slf4j.sysoutslf4j.context.LoggingSystemRegister")
public class TestLoggingSystemRegister {

	private LoggingSystemRegister loggingSystemRegister = new LoggingSystemRegister();
	private Logger loggerMock = createMock(Logger.class);

	@BeforeClass
	public static void turnOffRootLogging() {
		LoggingUtils.turnOffRootLogging();
	}
	
	@Before
	public void setStaticMocks() {
		Whitebox.setInternalState(LoggingSystemRegister.class, loggerMock);
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
		replayAll();
		loggingSystemRegister.registerLoggingSystem("some.package");
		verifyAll();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unregisterLoggingSystemLogsThatItWasUnregisteredIfLoggingSystemRegistered() {
		Whitebox.getInternalState(loggingSystemRegister, Set.class).add("some.package");
		loggerMock.info("Package {} unregistered; all classes within it or subpackages of it will "
				+ "have System.out and System.err redirected to SLF4J", "some.package");
		replayAll();
		loggingSystemRegister.unregisterLoggingSystem("some.package");
		verifyAll();
	}
	
	@Test
	public void unregisterLoggingSystemDoesNotLogIfLoggingSystemNotRegisterdPresent() {
		replayAll();
		loggingSystemRegister.unregisterLoggingSystem("some.package");
		verifyAll();
	}
}
