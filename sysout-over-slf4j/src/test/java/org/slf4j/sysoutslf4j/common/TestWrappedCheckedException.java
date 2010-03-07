package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.rmi.RemoteException;

import org.junit.Test;

public class TestWrappedCheckedException {

	@Test
	public void wrappedExceptionIsCause() {
		RemoteException remoteException = new RemoteException();
		WrappedCheckedException wrappedCheckedException = new WrappedCheckedException(remoteException);
		assertSame(remoteException, wrappedCheckedException.getCause());
	}
	
	@Test
	public void wrappedExceptionIsCauseAndMessageIsMaintained() {
		RemoteException remoteException = new RemoteException();
		WrappedCheckedException wrappedCheckedException = new WrappedCheckedException("message", remoteException);
		assertSame(remoteException, wrappedCheckedException.getCause());
		assertEquals("message", wrappedCheckedException.getMessage());
	}
}
