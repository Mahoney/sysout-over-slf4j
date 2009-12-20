package org.slf4j.sysoutslf4j.common;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.slf4j.testutils.ThrowableEquals.eqExceptionCause;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExceptionUtils.class)
public class TestReflectionUtils {
	
	@Test
	public void invokeMethodByNameNoArgCallsMethod() {
		
		CharSequence target = createMock(CharSequence.class);
		Integer expectedReturnVal = 1;
		expect(target.length()).andReturn(expectedReturnVal);
		replay(target);
		
		assertEquals(expectedReturnVal, ReflectionUtils.invokeMethod("length", target));
		
		verify(target);
	}

	@Test
	public void invokeMethodByNameNoArgThrowsNoSuchMethodExceptionNestedInIllegalStateExceptionWhenNoSuchMethod() {
		assertNoSuchMethodExceptionNestedInIllegalStateException(new Runnable() {
			public void run() {
				ReflectionUtils.invokeMethod("methodThatDoesntExist", new Object());
			}
		});
	}
	
	@Test
	public void invokeMethodByNameNoArgCoercesExceptionToRuntimeException() {
		
		CharSequence target = createMock(CharSequence.class);
		RuntimeException expectedException = new RuntimeException();
		expect(target.length()).andThrow(expectedException);
		
		mockStatic(ExceptionUtils.class);
		expect(ExceptionUtils.asRuntimeException(
				and(isA(InvocationTargetException.class), eqExceptionCause(expectedException)))).andReturn(expectedException);
		replayAll();
		
		try {
			ReflectionUtils.invokeMethod("length", target);
			fail();
		} catch (RuntimeException actualException) {
			assertSame(expectedException, actualException);
		}
		
		verifyAll();
	}
	
	private void assertNoSuchMethodExceptionNestedInIllegalStateException(Runnable runnable) {
		try {
			runnable.run();
			fail();
		} catch (IllegalStateException ise) {
			assertSame(NoSuchMethodException.class, ise.getCause().getClass());
		}
	}

}
