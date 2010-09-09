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

package uk.org.lidalia.sysoutslf4j.context;

import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4JServletContextListener;

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
		mockStatic(SysOutOverSLF4J.class);
		SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
		replay(SysOutOverSLF4J.class);
		
		servletContextListener.contextDestroyed(null);
		
		verify(SysOutOverSLF4J.class);
	}
}
